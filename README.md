
HOWTO
-----

 - python facebookdb.py [your_facebook_login_email] [facebook_passwd] [facebook_id]
 - facebook_id can be obtained from following link from facebook. 
   [http://www.facebook.com/profile.php?id=1545751628] 1545751628 is
   your id. 
   sometimes if you have a username for facebook such as 
   [http://www.facebook.com/simon] here simon is your id. 
 - use python tobinary.py user_profiles.txt to convert the crawled
   result to binary format. 


RESULT 
------

 - crawled results are saved into files with utf-8 encoding, so
 somethings there might be errors if utf-8 is not properly supported 
 on your computer. 
 - social_graph.txt -> The crawled social graph using your account. 
 - user_profiles.txt -> The encoded profile of users. 
 - user_wallposts.txt -> The recent wallposts of users. 

-------
FORMAT
------

 - user_profiles.txt
 columns meaning of binary from left to right [27 columns excl. uid]
 sex, networks, relationships, interested_in, bio,
 favorite_quotations, religious_views, political_views, friendlist,
 photos_albums, myposts, websites, address, im_screen_name, email,
 facebookuri, phone, family, brithday, current_city, hometown,
 activities, interests, employers, grad_school, college, high_school

FILES
-----

facebookdb.py
 - facebook crawler main file. 
 - This file is responsible for the scheduling of crawling. 

fb_db.py
 - database management file, currently not used. 
 
friendlist_extractor.py
 - extract friend list from users. 

profile_extractor.py
 - extract profiles from user info page. 

snippets.py
 - code snippets. 
 - please ignore. 

tobinary.py
 - Convert the result into binary format. 

wallpost_extractor.py
 - extract wallpost from user's wallpost page. 
 - currently, this script needs to be updated to be fully functional. 
