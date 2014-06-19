#!/usr/bin/perl 
# This file is used to get visibility setting of 
# profile items, and we will get the result of how
# people changed their profile settings. 
# 
# we have settings denoted as user only(O/0), friends(F/1),
# friendsoffriends(FOF/2), everyone(E/3). 
# 
# we have profile items as follows: 
# sex(3), networks(3), relationships(3), interested_in(1), bio(3),
# favorite_quotations, religious_views(1), political_views(1), friendlist,
# photos_albums(3), myposts, websites(3), address, im_screen_name, email,
# facebookuri(3), phone(1), family(3), brithday(3), current_city(3),
# hometown(3), activities(3), interests(3), employers(3), grad_school(3),
# college(3), high_school(3).
# signature of changing. 
# 3 3 3 1 3 x 1 1 x 3 x 3 x x x 3 1 3 3 3 3 3 3 3 3 3 3
# here x means not available. 

while (<STDIN>)  {
    chomp; 
    @itemnames = ("sex", "networks", "relationships", "interested_in",
		  "bio", "favorite_quotations", "religious_views",
		  "political_views", "friendlist", "photos_albums", "myposts",
		  "websites", "address", "im_screen_name", "email", "facebookuri",
		  "phone", "family", "brithday", "current_city", "hometown",
		  "activities", "interests", "employers", "grad_school", "college",
		  "high_school");

    @items = split(/,/); 
    @defaultsetting =
    ("3","3","3","1","3","x","1","1","x","3","x","3",
    "x","x","x","3","1","3","3","3","3","3","3","3", 
     "3","3","3");  

    # sensitivity setting of facebook data.
    # for each profile item, we need to set how many people set their
    # profile to each level. 
    @sens_setting = ([0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0],
		     [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0],
		     [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0],
		     [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0],
		     [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0]);  

    my $change_count = 0; 
    my $i = 0; 

    # default setting. 
    while ($i < $#defaultsetting) {
	if (($defaultsetting[$i] ne $items[$i]) &&
	    ($defaultsetting[$i] ne "x")) {
	    $change_count++; 
	    # print $itemnames[$i] . " "; 
	}

	# get sensitivity settings. 
	my $setting = $items[$i]; 
	print $setting + 0; 
	print " ";
	$sens_setting[$i][$setting+0]++;
	# print $sens_setting[$i][$setting] . " ";
	$i++; 
    }
    print $change_count . "\n";
    $change_count = 0; 
    # $sens_setting[2][3] = 989;
}

# print the result.
# print @sens_setting; 
print "\n";  
for my $i (0 .. 26) {
    for my $j (0 .. 3) {
	print "[" . $i . "," . $j . "]" . "=" . $sens_setting[$i][$j] . " ";
    }
    print "\n";
}
#print $sens_setting[$i][$j] . "\n";
