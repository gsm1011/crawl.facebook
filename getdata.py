import mechanize, sys
from bs4 import BeautifulSoup

#############################
# Login to facebook.        # 
#############################
br = mechanize.Browser(); 
br.addheaders = [('User-Agent', 'Firefox')]#Googlebot
br.set_handle_equiv(False)
br.set_handle_redirect(True)
br.set_handle_robots(False)
br._factory.is_html = True

br.open('https://login.facebook.com/login.php')
br.select_form(nr=0)

# br.forms() whill show all the forms. 
br['email']='' # Facebook email. 
br['pass']=''  # Facebook password. 

r = br.submit(); 
if r == None: 
    print 'Error logging into facebook. '
    sys.exit(); 

# Save the document to file. 
import os
uid = 'shumin.guo'
dir = 'htmls/' + uid + '/'
try: 
    os.mkdir(dir);
except:
    pass

timeline = open(dir + 'timeline.html', 'w')
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid).read());
timeline.write(htmldoc); 
# timeline.write(htmldoc.decode('utf-8', 'replace'))
timeline.close() 

about = open(dir + 'about.html', 'w')
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/about').read());
about.write(htmldoc)
about.close() 

photos = open(dir + 'photos.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/photos').read());
photos.write(htmldoc)
photos.close() 

friends = open(dir + 'friends.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/friends').read());
friends.write(htmldoc)
friends.close() 

music = open(dir + 'music.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/music').read());
music.write(htmldoc)
music.close() 

movies = open(dir + 'movies.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/movies').read());
movies.write(htmldoc)
movies.close() 

tv = open(dir + 'tv.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/tv').read());
tv.write(htmldoc)
tv.close() 

books = open(dir + 'books.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/books').read());
books.write(htmldoc)
books.close() 

games = open(dir + 'games.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/games').read());
games.write(htmldoc)
games.close() 

likes = open(dir + 'likes.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/likes').read());
likes.write(htmldoc)
likes.close() 

events = open(dir + 'events.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/events').read());
events.write(htmldoc)
events.close() 

groups = open(dir + 'groups.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/groups').read());
groups.write(htmldoc)
groups.close() 

notes = open(dir + 'notes.html', 'w') 
htmldoc = ''.join(br.open('https://www.facebook.com/' + uid + '/notes').read());
notes.write(htmldoc)
notes.close() 

sys.exit()
###########################
# Parse the html document.# 
###########################
soup = BeautifulSoup(html_doc)

divs = soup.find_all('div', recursive=True)
print divs

for div in divs:
    print div
    continue

    spans = div.find_all('span')

    for span in spans:
        print span
