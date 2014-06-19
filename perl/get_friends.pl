#!/usr/bin/perl 
# This file is used to extract friends from the friends lists. 
print $ARGV[0]; 
$src_user = $ARGV[0]; 

LOOP:while(<STDIN>) {
    chomp; 			# remove line breaking symbol.
    my $fnd_lst = "";
    # print $_ . "\n";
    if (m/<div class="dualList fbProfileBrowserListContainer">(.*)(<\/div>){4}/g) {
	$fnd_lst = $1; 
    } else {
	# print "Error while extracting list, can't find pattern.";
	next LOOP; 
    }

    # now get friend information from list. 
    while ($fnd_lst =~ m/<a href="http:\/\/www.facebook.com\/([^"]+)"[^>]+eng_tid&quot;:([^,]+)[^>]+>(.*?)<\/a>/g) {
	my $f_nname = $1; 	# nick name. 
	my $f_id = $2; 		# fb unique id.
	my $f_name = $3; 
	
	# nick name. 
	if ($f_nname =~ m/(.*)\?ref=pb/) {
	    $f_nname = $1; 
	} else {
	    $f_nname = $f_id;
	}

	print $f_nname . " " . $src_user . " " . $f_id . " " . $f_name . "\n";
    }

    # next LOOP; 
    # # get friend id and friend names. 
    # while($fnd_lst =~ m/<li [^>]*>(.*?)<\/li>/g) {
    # 	$_ = $1;
    # 	# print $_ . "\n";
    # 	my $f_id = "00000";		# friend fb unique id. 
    # 	my $f_nick_name = "";		# some people may have nick name.
    # 	my $f_name = "";	# friend name. 

    # 	# get facebook unique id of friend. 
    # 	if (m/eng_tid&quot;:([^,]+),/) {
    # 	    $f_id = $1; 
    # 	}

    # 	# get nick name of friend. 
    # 	if (m/http:\/\/www.facebook.com\/([a-z0-9\.]+)\?ref=pb/) {
    # 	    $f_nick_name = $1; 
    # 	    print "get nick name: " . $f_nick_name . "\n";
    # 	}
    # 	s/<[^>]+>/ /g; 		# html tags. 
    # 	s/  +/ /g;		# multiple spaces. 
    # 	s/^ +//g;		# leading spaces. 
    # 	$f_name = $_; 

    # 	print $f_id . " : " . $f_name . "\n"; 
    # }
}
