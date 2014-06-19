#!/usr/bin/perl 
# This file is used to calculate the visibility of data. 

open(MYFILE, "$ARGV[0]") || die("File open error."); 
open(NOBODYFILE, "$ARGV[1]") || die("File open error."); 

$type = $ARGV[2]; 		# data type, F/FOF

%vistable = (); 		# key is user id, value is an array of
# visibility staff. 

while (<MYFILE>) {
    chomp; 
    if(/^([^ ]+) (.*)$/) {
	my $uid = $1; 
	my $vis = $2; 
	$vistable{$uid} = $vis; 
    }
}

# print %vistable; 
LOOP2:while (<NOBODYFILE>) {
    chomp;

    if(/(^[^ ]+) (.*$)/) {
	my $uid = $1;
	my @myvis = split(/ /, $vistable{$uid});
	my @nobodyvis = split(/ /, $2);

	# print @myvis . " " . @nobodyvis . "\n";
	# if hash key exists, calculate visibility now.
	# notations. 
	# E --> everyone. 
	# F --> Friend only. 
	# ERR --> errors. 
	# O --> only *ME*.
	if ((@myvis == @nobodyvis) && (@myvis == 27)) {
	    # apply visibility rule to the table. 
	    my $vislist = "";
	    for(my $i = 0; $i < @myvis; $i++) {
		# print $myvis[$i] . " " . $nobodyvis[$i] . " ";
		if ($type eq "F") { # type is friend. 
		    if($myvis[$i] == "1") {
			if ($nobodyvis[$i] == "1") {
			    # Y/Y
			    $vislist .= "E,"; 
			} else {
			    # Y/N
			    $vislist .= "F/FOF,";
			}
		    } else {
			if ($nobodyvis[$i] == "1") {
			    # N/Y
			    $vislist .= "ERR,";
			} else {
			    # N/N
			    $vislist .= "O,";
			}	# nobody vis.
		    }		# myvis.
		} else {	# type is friend of friend. 
		    if($myvis[$i] == "1") {
			if ($nobodyvis[$i] == "1") {
			    # Y/Y
			    $vislist .= "E,"; 
			} else {
			    # Y/N
			    $vislist .= "FOF,";
			}
		    } else {
			if ($nobodyvis[$i] == "1") {
			    # N/Y
			    $vislist .= "ERR,";
			} else {
			    # N/N
			    $vislist .= "O/F,";
			}	# nobody vis.
		    }		# myvis.
		}
	    }
	    $vislist =~ s/,$//g;
	    $vislist .= "\n";
	    if ($vislist !~ /ERR/) {
		print $vislist;
	    } 
	}
    }
}

close MYFILE; 
close NOBODYFILE;
