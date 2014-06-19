import mechanize,re, codecs
from BeautifulSoup import BeautifulSoup, NavigableString, UnicodeDammit

def buildSocialGraph(fromfriend, page, linkqueue=None, \
                         friendlinks=None, social_graph=None):
    ''' Build the social graph. 
    page - the page to get a list of friends. 
    queue - the queue for crawling. 
    friend_dict - the dictionary to unique the friend list. 
    ''' 
    DEBUG = False
    try: 
        friendsoup = BeautifulSoup(page)
        # friendsoup = BeautifulSoup(''.join(open(filename).read()))
        friends = friendsoup.html.body.findAll(name='div')
        friends1 = BeautifulSoup(''.join(str(friends)))
        friend_div = friends1.find(name='div',attrs={'id':'pagelet_main_column_personal'})

        if friend_div == None: return False # friend list not public. 
        friends = friend_div.findAll('div',{'class':'fsl fwb fcb'})
        if friends != []:     # friends in a list. 
            for friend in friends:
                if friend.a == None:
                    continue 
                friend_name = friend.a.string 
                friend_link = friend.a['href']
                id_pos = friend_link.rfind('=')
                if id_pos == -1:    # link format: http://www.facebook.com/farrahwu
                    slash_pos = friend_link.rfind('/')
                    friend_id = friend_link[slash_pos+1:len(friend_link)]
                else:               # link format: http://www.facebook.com/link.php?id=65266
                    friend_id = friend_link[id_pos+1:len(friend_link)]
                if friend.nextSibling != None: 
                    friend_name += '(' + friend.nextSibling.string + ')'

                # Save result to social graph.
                if not friendlinks.has_key(friend_id): 
                    friendlinks[friend_id] = fromfriend
                    linkqueue.put(friend_id)

                    # write social graph to file. 
                    social_graph.write(friend_id+" "+fromfriend +" "+unicode(friend_name)+"\n")
                    social_graph.flush()
                    if DEBUG == True: 
                        print friend_id, unicode(friend_name), friendlinks[friend_id]
           
        else:                   # friends in a table. 
            friends = friend_div.findAll('div',{'class':'photoWrapper'})
            for friend in friends:
                friend_name = friend.nextSibling.string
                if friend_name.find('Suggest Friends') != -1: continue
                friend_link = friend.parent['href']
                id_pos = friend_link.rfind('=')
                if id_pos == -1:    # link format: http://www.facebook.com/farrahwu
                    slash_pos = friend_link.rfind('/')
                    friend_id = friend_link[slash_pos+1:len(friend_link)]
                else:               # link format: http://www.facebook.com/link.php?id=65266
                    friend_id = friend_link[id_pos+1:len(friend_link)]

            # Save result to social graph.
                if not friendlinks.has_key(friend_id): 
                    friendlinks[friend_id] = fromfriend
                    linkqueue.put(friend_id)
                    # write social graph to file. 
                    social_graph.write(friend_id+" "+fromfriend +" "+unicode(friend_name)+"\n")
                    social_graph.flush()
                
                if DEBUG == True:
                    print friend_id, friend_name

        # Friends that are not displayed. 
        hiden_friends = friend_div.find('div',{'class':'fbProfileBrowserLargeList largeList'})
        if hiden_friends == None:
            return True
        friends = hiden_friends.findAll('a')
        for friend in friends:
            friend_link = friend['href']

            id_pos = friend_link.rfind('=')
            if id_pos == -1:    # link format: http://www.facebook.com/farrahwu
                slash_pos = friend_link.rfind('/')
                friend_id = friend_link[slash_pos+1:len(friend_link)]
            else:               # link format: http://www.facebook.com/link.php?id=65266
                friend_id = friend_link[id_pos+1:len(friend_link)]

            # The name of friend.
            if friend.span.string != None: 
                friend_name = friend.span.string 

            if not friendlinks.has_key(friend_id): 
                friendlinks[friend_id] = fromfriend
                linkqueue.put(friend_id)
                social_graph.write(friend_id+" "+fromfriend +" "+unicode(friend_name)+"\n")
                social_graph.flush()
                
            if DEBUG == True:
                print friend_id, friend_name, friend_link
    except: 
        social_graph_log = open('social_graph.log', 'a')
        social_graph_log.write('Error while building friend list for user ' + fromfriend)
        social_graph_log.close()
        return False
    return True

def testVisibleItems(page):
    ''' test which items are visible? (wall, info, photo, notes and
    friends)'''  
    DEBUG = False
    try:
        # friendsoup = BeautifulSoup(''.join(open(page).read()))
        friendsoup = BeautifulSoup(page)
        friends = friendsoup.html.body.findAll(name='div')
        friends1 = BeautifulSoup(''.join(str(friends)))
        friend_div = friends1.find(name='div',attrs={'id':'pagelet_left_column'})
        
        # Test if items are visible?
        wall = friend_div.find('li',{'id':'navItem_wall'})
        info = friend_div.find('li',{'id':'navItem_info'})
        photos = friend_div.find('li',{'id':'navItem_photos'})
        notes = friend_div.find('li',{'id':'navItem_notes'})
        friends = friend_div.find('li',{'id':'navItem_friends'})
        questions = friend_div.find('li',{'id':'navItem_questions'})

        # How many friends does this user have ?
        friend_count = friend_div.find('div',{'id':'pagelet_relationships'})
        fcount = 'friendcount:'
        if friend_count == None: 
            fcount += 'N/A\t'
        else:
            friends_link = friend_count.findAll('span',{'class':'fcg'})
            for link in friends_link: 
                try: 
                    counttext = link.a.string 
                    idx1 = counttext.find('(')
                    idx2 = counttext.find(')')
                    count = counttext[idx1+1:idx2]
                    fcount += count + '\t'
                except: 
                    continue
    
        # YES means visible and NO means not. 
        visibility = fcount 
        if wall == None: visibility += 'wallpost:NO\t'
        else: visibility += 'wallpost:YES\t'
        if info == None: visibility += 'info:NO\t'
        else: visibility += 'info:YES\t'
        if photos == None: visibility += 'photos:NO\t'
        else: visibility += 'photos:YES\t'
        if notes == None: visibility += 'notes:NO\t'
        else: visibility += 'notes:YES\t'
        if friends == None: visibility += 'friends:NO\t'
        else: visibility += 'friends:YES\t'
        #if questions == None: visibility += 'questions:NO\t'
        #else: visibility += 'questions:YES\t'
        if DEBUG == True:
            print visibility
    except: 
        return ''
    return visibility


if __name__ == '__main__':
    # print "Extract friends"
    #buildSocialGraph('friend_list.html')
    #buildSocialGraph('friend_table.html')
    testVisibleItems('friend_list.html')
    testVisibleItems('friend_list2.html')
    testVisibleItems('friend_list3.html')
