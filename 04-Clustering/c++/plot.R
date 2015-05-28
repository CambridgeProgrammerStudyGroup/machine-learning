x <- read.table("clusters.dat");
x[,5] <- as.factor(x[,5])
png(file="cluster.png",width=600,height=600);
plot(x[,1],x[,2],col=x[,5])
dev.off();
