x <- read.table("../data/ex2x.dat")
y <- read.table("../data/ex2y.dat")

ft <- lm(y[,1]~x[,1])
ft
anova(ft)

png(file="age_vs_weight.png",height=600,width=600);
plot(x[,1],y[,1])
abline(ft)
dev.off()

