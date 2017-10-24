function [event] = recentTagInfoLoop(recentTagInfoDir, tags, folderDir)
%Function to loop recent tag info
%event = recentTagInfoLoop(uint8('Z:\04_Simulation\Winter2017\Program connecting PLC to simulation'), uint8(' ABBFreq$ FanucCurrent$ RFID56$ RFID57$'), uint8('Z:\04_Simulation\Winter2017\Program connecting PLC to simulation'))

%initializes variables
event = 0;
events = 0;
seconds = 0;
detectedTime = 0;
detectedTag = '';

tagLast = 0;
dateLast = 0;

tagNames = 0;
tag = 0;
date = 0;

%sets up plot
Fig1 = figure(1);
Fig1.Position = [100, 100, 600, 800];   
Fig1.PaperPositionMode = 'auto';

plot1 = plot(seconds, events);
title('Detecting events: 1 is event');
xlabel('time since detecting (s)');
ylabel('event');
ylim([0 2]);

%sets tagLast
[tagNames, tagLast, dateLast] = recentTagInfo(tags, folderDir); 

%loops to detect changes
for i = 1:30
    tempEvent = 0; %event used for graph
    
    %moves to directory of recent tag info
    rtid = char(recentTagInfoDir);
    cd (rtid);
    
    %sets new tag and date
    [tagNames, tag, date] = recentTagInfo(tags, folderDir);
    
    %combines tag and tagLast to compare
    tagsCombined = [tagLast' ; tag'] %has to flip so each column is one tag
    [rows, columns] = size(tagsCombined);
    
    %combines date arrays
    combinedTimes = [dateLast date];
    
    %Goes through each row looking for differences
    for j = 1:(rows - 1)
        if(~isequal(tagsCombined(j, :), tagsCombined(j + 1, :)))
            %Stores column of difference bewteen rows
            diffIndex = logical((tagsCombined(j, :) + tagsCombined(j + 1, :)) == 1);
            %finds tag that changed
            detectedTag = tagNames(diffIndex)
            %records event
            event = 1;
            tempEvent = 1;
            %records time detected
            detectedTime = 0; %combinedTimes(j) %j + 1?
        end
    end
    
    %if tempEvent == 1
    %sim('SingleMachine_Simple');
    %end
    
    %sets new last values to check
    tagLast = tag;
    datelast = date;
    
    %updates plot
    seconds = [seconds i];
    events = [events tempEvent];
    plot1 = plot(seconds, events);
    title('Detecting events: 1 is event');
    xlabel('time since detecting (s)');
    ylabel('event');
    ylim([-1 2]);
    
    %waits one second for new data
    pause(1);
end
    
end
