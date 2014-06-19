#!/usr/bin/perl
# This file is to get facebook id for data extraction. 
# on facebook, each user will have a unique id given by facebook on
# registration. And as an option, a user can make his own unique
# personalized id such as simon.guo etc.. 
LOOP:while(<STDIN>) {
    chomp; 
    @f_info = split(/:/); 

    $f_id = $f_info[0]; 
    $f_id = $f_info[1] if ($f_info[1] =~ /\w+/); 
    print $f_id . " " . $f_info[2] . "\n";
}
