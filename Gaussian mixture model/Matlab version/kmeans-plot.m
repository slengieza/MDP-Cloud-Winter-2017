rng default; % For reproducibility
X = [current_50/50, voltage_50/50;
    current_40/50, voltage_40/50;
    current_20/50, voltage_20/50];
figure;
plot(X(:,1),X(:,2),'.');
title 'current/50 and voltage/50 cluster';

opts = statset('Display','final');
[idx,C] = kmeans(X,3,'Distance','cityblock',...
    'Replicates',5,'Options',opts);

figure;
plot(X(idx==1,1),X(idx==1,2),'r.','MarkerSize',25)
hold on
plot(X(idx==2,1),X(idx==2,2),'b.','MarkerSize',25)
plot(X(idx==3,1),X(idx==3,2),'g.','MarkerSize',25)
plot(C(:,1),C(:,2),'kx',...
     'MarkerSize',15,'LineWidth',3)
legend('frequency 50','frequency 40','frequency 20','Centroids',...
       'Location','NE')
title 'current/50 and voltage/50 cluster by k-means'
xlabel('current/50')
ylabel('voltage/50')
hold off