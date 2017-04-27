#!/usr/bin/perl

@files = `ls ../ErrorInput/`;
chop @files;

while ($filename = shift @files) {
    print "Working on $filename\n";
    print `java havabol/HavaBol ../ErrorInput/$filename &> ../ErrorOutput/Output$filename`;
}
