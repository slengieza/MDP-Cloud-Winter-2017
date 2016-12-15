package com.mdp.producer;

//java
import java.io.*;
import java.util.*;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

//topic
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import kafka.common.TopicExistsException;

//producer
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.producer.KeyedMessage;
import scala.collection.JavaConversions;

//Kafka
import com.mdp.producer.JsonToString;

public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private boolean trace = false;

    // Kafka 
    private Properties props;
    private ProducerConfig config;
    private Producer<String, String> producer;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE/*, ENTRY_DELETE, ENTRY_MODIFY*/);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } 
            else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    WatchDir(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();

        register(dir);

        // enable trace after initial registration
        this.trace = true;

        // Kafka 
        this.props = new Properties();
        this.props.put("bootstrap.servers", "localhost:9092");
        this.props.put("acks", "all");
        this.props.put("metadata.broker.list", "localhost:9092");
        this.props.put("serializer.class", "kafka.serializer.StringEncoder");
        this.props.put("request.required.acks", "1");
        this.config = new ProducerConfig(props);
        this.producer = new Producer<String, String>(config);
    }

    void processEvents() {
        while(true) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                //System.out.format("%s: %s\n", event.kind().name(), child);
                (new Thread(new HandleEvent(child))).start();
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    // Kafka 
    public static void  new_topic(String topic){
        try{
            //used to create topic
            //ZkClient zkClient = new ZkClient("migsae-kafka.aura.arc-ts.umich.edu:2181/kafka", 10000, 10000, ZKStringSerializer$.MODULE$);
            ZkClient zkClient = new ZkClient("localhost:2181", 10000, 10000, ZKStringSerializer$.MODULE$);
            // topic name, replication factor, replication factor, config properties
            System.out.println(ZkUtils.getSortedBrokerList(zkClient));
            AdminUtils.createTopic(zkClient, topic, 3, 1, new Properties());
        }
        catch (TopicExistsException e){
            System.out.println("Topic exists");
        }
    }

    public static void main(String[] args) throws IOException {
        // Kafka 
        String topic = "test1";
        String group_id = "report";
        //new_topic(topic);

        // register directory and process its events
        //Path is the directory you want to listen to
        Path dir = Paths.get("/Users/stevenlengieza/Research/MDP-Cloud-Fall-2016/data");
        new WatchDir(dir).processEvents();
    }

    public class HandleEvent implements Runnable {
        private File file;

        public HandleEvent(Path file){
            this.file = new File(file.toString());
        }

        public void run(){
            if(file.toString().toLowerCase().endsWith(".dat")){
                // Kafka 
                System.out.println("Getting kafkaMessages");
                HashMap<Long, List<Long>> kafkaMessages = JsonToString.GetKafkaMessage(file);
                Set set = kafkaMessages.entrySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    Map.Entry mentry = (Map.Entry)iterator.next();
                    System.out.println("Sending data for Timestamp " + mentry.getKey());
                    //System.out.println("Timestamp: " + mentry.getKey() + " data " + (Arrays.toString(((List)mentry.getValue()).toArray())));
                    String dataList = "";
                    Iterator it = ((List)mentry.getValue()).iterator();
                    while(it.hasNext()){
                        dataList += it.next().toString() + "\t";
                    }
                    KeyedMessage<String, String> data = new KeyedMessage<String, String>("test1", dataList);
                    producer.send(data);
                }
                file.delete();
                return;
            }
            else{
                System.out.println("File " + this.file.getName() + " does not end with .dat");
                return;
            }
        }
    }
}