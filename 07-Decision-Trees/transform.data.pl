#!/usr/bin/perl

use warnings;
use strict;

my $infile="titanic3.clean.reordered.csv";

my $mean_age=0;
my $num=0;

open(IN,$infile) or die;
while(<IN>) {
    if (m/pclass/) {
        print;
        next;
    }
    my $line = $_;
    my @f = split(/,/,$line);
    #print $f[4],"\n";
    $mean_age+=$f[4];
    ++$num;
}
close(IN);

$mean_age/=$num;
print "mean_age=",$mean_age,"\n";

open(IN,$infile) or die;
while(<IN>) {
    if (m/pclass/) {
        print;
        next;
    }
    my $line = $_;
    my @f=split(/,/,$line);
    if ($f[4]>$mean_age) {
        $f[4]="\"Old\"";
    } else {
        $f[4]="\"Young\"";
    }
    print join(",",@f);
}
close(IN);

