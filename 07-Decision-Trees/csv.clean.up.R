# some R code to clean-up the dataset

df <- read.csv("titanic3.csv")
dim(df)
idx <- 1:10
df.complete <- df[complete.cases(df[,idx]),idx]
# reorder to put target variable "survived" at the end
df.rd <- df.complete[,c(1,3,4,5,6,7,8,9,10,2)]
summary(df.rd)
write.csv(df.rd,file="titanic3.clean.reordered.csv",row.names=FALSE)
