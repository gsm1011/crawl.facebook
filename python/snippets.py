                # recent_event += str(content)
            continue
            reg_link_start = re.compile(r'<.*[^>].*>')
            reg_link_end = re.compile('</a>') 
            reg_span_start = re.compile('<span*">') 
            reg_span_end = re.compile(r'span')
            reg_space = re.compile(r'\s+')

            print '+++++++', recent_event
            regex = re.compile(r"(?i)<\/?\w+((\s+\w+(\s*=\s*(?:\".*?\"|'.*?'|[^'\">\s]+))?)+\s*|\s*)\/?>")
            regex.sub('', recent_event)
            reg_link_start.sub('', recent_event)
            reg_link_end.sub('', recent_event)
            reg_span_start.sub('', recent_event)
            reg_span_end.sub('', recent_event)

            print '=======', recent_event
            continue 

            if event_type.has_key('class'):
                event_img_class = str(event_type['class']) # event type from the small icon.                 
            else:
                event_img_class = 'N/A'

            # Friendship connection event. 
            if event_img_class.find(friend_event) > 0: 
                print 'Friendship event.....'
                passiveName_block = act_title.find('span')
                if passiveName_block != None:
                    passiveName = passiveName_block.string
                    #print passiveName
                
                otherNames = act_title.findAll('a')

                if len(otherNames) == 1: 
                    print passiveName, 'is now friends with', otherNames[0].string
                    wallposts += passiveName + ' is now friends with ' + otherNames[0].string
                elif len(otherNames) == 2:
                    print passiveName + ' are now friends with ', 
                    wallposts += passiveName + ' are now friends with '
                    othername1 = otherNames[0].string
                    print othername1 + ',',
                    twomore = otherNames[1].find('span',{'class':'uiTooltipText'}) 
                    if twomore == None: 
                        othername2 = otherNames[1].string
                    else:
                        # clean the unwanted contents. 
                        othername2 = str(twomore).replace('<br />', ', ')                        
                        othername2 = othername2.replace('<span class="uiTooltipText">', '')
                        othername2 = othername2.replace('</span>', '')
                            
                    print othername2
                wallposts += '\n'
                
            # like event. 
            elif event_img_class.find(like_event) > 0:
                print 'Like event.....'
                passiveName_block = act_title.find('span')
                if passiveName_block != None:
                    passiveName = passiveName_block.string
                print passiveName + ' likes ',
                wallposts += passiveName + ' likes '
                entities = act_title.findAll('a')
                # you may like on thing, two things or more than two things. 
                if len(entities) == 1: 
                    page1 = entities[0].string
                    print page1, 
                    wallposts += page1
                if len(entities) == 2:
                    page1 = entities[0].string
                    print page1 + ',', 
                    wallposts += page1
                    twomore = entities[1].find('span',{'class':'uiTooltipText'}) 
                    if twomore == None:
                        page2 = entities[1].string
                    else:
                        page2 = str(twomore).replace('<br />', ', ')
                        page2 = page2.replace('<span class="uiTooltipText">', '')
                        page2 = page2.replace('</span>', '')
                    print page2 

                wallposts += '\n'
    
            elif event_img_class.find(comment_event) > 0:
                print 'Comment event.....'
                comment_contents = act_title.contents
                comment_text = str(comment_contents[0]).replace('&quot;','"')
                comment_entity = act_title.findAll('a')
                if len(comment_entity) == 2: 
                    print comment_text, comment_entity[0].string+'\'s', comment_entity[1].string

                # print comment_text + comment_entity[0].string + '\'s' + unicode(comment_entity[1].string) + '.'
                # wallposts += comment_text + comment_entity[0].string + '\'s' + comment_entity[1].string + '.'
                # wallposts += '\n'
            elif event_img_class.find(status) > 0: 
                print 'Status change event......'
                who = act_title.span.string
                do = str(act_title.contents[1])
                what = act_title.a.string
                print who, do, what
                
            elif event_img_class.find('sp_bhj0aj sx_332bda') > 0: 
                print 'profile picture change event......'
                who = act_title.span.string
                dowhat = str(act_title.contents[1])
                print who, dowhat

            elif event_img_class.find('sp_a2jb2c sx_e0a377') > 0: 
                print 'locale change event......'
                who = act_title.span.string 
                do = str(act_title.contents[1])
                what = act_title.a.string
                print who, do, what

            else: 
                print 'unidentified event. ', event_type

            if event_type_user != None: 
                # none facebook related events. such as playing game. .
                print 'not facebook related events......'
                who = act_title.span.string
                do = str(act_title.contents[1])
                what = act_title.a.string
                print who, do, what

        print
        print 
 


    def buildSocialGraph(self, fromfriend):
        ''' Extract all the friends of a friend and put the profile link to the global 
        link repository. '''
        print " - Getting friend list of", fromfriend
        if re.compile('\D').findall(fromfriend) == []: # friend id is numerical.
            friendurl = self.getProfileLink(fromfriend) + '&sk=friends&v=friends'
        else:                   # friend id is alphabetical.
            friendurl = self.getProfileLink(fromfriend) + '?sk=friends&v=friends'
        retries = 3 
        while True:
            try:                # handle HTTP request error!!
                friendpage = self.browser.open(friendurl)
                friendsoup = BeautifulSoup(''.join(friendpage.get_data()))
                print self.browser.geturl()
                friendpage = self.browser.open(friendurl)
                friendsoup = BeautifulSoup(''.join(friendpage.get_data()))
                print self.browser.geturl()
                break
            except: 
                print "Crawling", fromfriend, "failed, retrying..."
                time.sleep(5)
                self.login()
                retries -= 1
                if retries <= 0: return False              

        friends = friendsoup.html.body.findAll(name='div')
        friends1 = BeautifulSoup(''.join(str(friends)))
        friend_div = friends1.find(name='div',attrs={'id':'pagelet_main_column_personal'})
        if friend_div == None: return False # friend list not public. 
        friends = friend_div.findAll('div',{'class':'fsl fwb fcb'})
        if friends != []:     # friends in a list. 
            for friend in friends:
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

                ''' Save result to social graph. '''
                if not self.friendlinks.has_key(friend_id): 
                    self.friendlinks[friend_id] = fromfriend
                    self.linkqueue.put(friend_id)
                    self.social_graph.write(friend_id+" "+fromfriend +" "+unicode(friend_name)+"\n")
                    self.social_graph.flush()
                    if self.__PRINT_FRIEND_GRAPH__ == 1: 
                        print friend_id, self.friendlinks[friend_id]
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

                ''' Save result to social graph. '''
                if not self.friendlinks.has_key(friend_id): 
                    self.friendlinks[friend_id] = fromfriend
                    self.linkqueue.put(friend_id)
                    self.social_graph.write(friend_id+" "+fromfriend +" "+unicode(friend_name)+"\n")
                    self.social_graph.flush()
                    if self.__PRINT_FRIEND_GRAPH__ == 1: 
                        print friend_id, self.friendlinks[friend_id]
        return True

    def getFriendProfiles(self, friend_id):
        ''' Get the profiles of friends starting from friend. ''' 
        if re.compile('\D').findall(friend_id) == []: # friend id is numerical.
            friend_link = self.getProfileLink(friend_id) + '&sk=info'
        else:                   # friend id is alphabetical.
            friend_link = self.getProfileLink(friend_id) + '?sk=info'
            
        while True:         # handle HTTP request error!!
            try:
                print " - Extracting profile for : ", friend_id, friend_link
                profile_page = self.browser.open(friend_link)
                profile_soup = BeautifulSoup(''.join(profile_page.get_data()))
                profiles = profile_soup.find('div',{'id':'pagelet_main_column_personal'})
                profiles = BeautifulSoup(str(profiles))
                print self.browser.geturl()
                break
            except:
                print "Extracting profile page error... ", friend_id
                time.sleep(5)
                self.login()              
        # profile sections.
        # ==================================================
        # photos. 
        profile_photo = profiles.find('div',{'id':'pagelet_photo_bar'})
        
        # ==================================================
        # education and work. 
        profile_eduwork = profiles.find('div',{'id':'pagelet_eduwork'})
        experience = 'Education and Work - '
        if profile_eduwork == None:
            experience += 'private.'
        else: 
            eduwork = profile_eduwork.find('div',{'class':'phs'})
            if eduwork == None: 
                experience += 'private.' 
            else:
                ''' Find experience label, like jobs, schools. '''
                exps = eduwork.findAll('th', {'class':'label'})
                ''' get Education and work experiences. '''
                for exp in exps:
                    label = exp.string 
                    experience += label + ':'
                    exp_titles = exp.nextSibling.findAll('div', {'class':'experienceTitle'})
                    for exp_title in exp_titles:
                        experience += ' ' + exp_title.a.span.string
                        exp_info = exp_title.nextSibling.findAll('span')
                        for info in exp_info:
                            experience += ' ' + info.string + ','
                        exp_body = exp_title.nextSibling.nextSibling
                        if exp_body != None: 
                            for body in exp_body.contents:
                                if body != '':
                                    experience += body + ','
                        experience += ';'
        return experience           
        # ==================================================
        # philosophy
        profile_philosophy = profiles.find('div',{'id':'pagelet_philosophy'})   
        philosophy = 'Philosophy - '
        if profile_philosophy == None:
            philosophy += 'private.'
        else: 
            phil = profile_philosophy.find('div',{'class':'phs'})
            if phil == None: 
                philosophy += 'private.' 
            else:
                ''' Find philosophy label, like jobs, schools. '''
                exps = phil.findAll('th', {'class':'label'})
                ''' get philosophy. '''
                for exp in exps:
                    label = exp.string 
                    philosophy += label + ':'
                    exp_titles = exp.nextSibling.findAll('div', {'class':'experienceTitle'})
                    for exp_title in exp_titles:
                        philosophy += ' ' + exp_title.a.span.string
                        exp_info = exp_title.nextSibling.findAll('span')
                        for info in exp_info:
                            philosophy += ' ' + info.string + ','
                        exp_body = exp_title.nextSibling.nextSibling
                        if exp_body != None: 
                            for body in exp_body.contents:
                                if body != '':
                                    philosophy += body + ','
                        philosophy += ';'
        print philosophy
        
        profile_sports = profiles.find('div',{'id':'pagelet_sports'})

        # ==================================================
        # Arts and entertainments. 
        profile_arts = profiles.find('div',{'id':'pagelet_arts_and_entertainment'})
        arts_entertain = 'Arts and Entertainments - '
        if profile_arts == None:
            arts_entertain += 'private.'
        else: 
            arts = profile_arts.find('div',{'class':'phs'})
            if arts == None: 
                arts_entertain += 'private.' 
            else:
                ''' Find arts_entertain label, like jobs, schools. '''
                exps = arts.findAll('th', {'class':'label'})
                ''' get arts_entertain. '''
                for exp in exps:
                    label = exp.string 
                    arts_entertain += label + ':'
                    exp_titles = exp.nextSibling.findAll('div', {'class':'mediaPageName'})
                    for exp_title in exp_titles:
                        arts_entertain += ' ' + exp_title.string
                        arts_entertain += ','
        print arts_entertain

        # ==================================================
        # Activities and interests.
        profile_activity = profiles.find('div',{'id':'pagelet_activities_and_interests'})
        activities = 'Activities and Interests - '
        if profile_activity == None:
            activities += 'private.'
        else:
            activity = profile_activity.find('div',{'class':'phs'})
            acts = activity.findAll('th',{'class':'label'})
            for act in acts:
                try:        # to avoid the empty contents.
                    label = act.string
                    if label.string != None: activities += label + ' : '
                except:
                    break
                exp_titles = act.nextSibling.findAll('div', {'class':'experienceTitle'})
                for exp_title in exp_titles:
                    activities += ' ' + exp_title.a.span.string
                    exp_info = exp_title.nextSibling.findAll('span')
                    for info in exp_info:
                        activities += ' ' + info.string + ','
                    exp_body = exp_title.nextSibling.nextSibling
                    if exp_body != None: 
                        for body in exp_body.contents:
                            if body != '':
                                activities += body + ','
                        activities += ', '
                activities += '; '
            
                exp_titles = act.nextSibling.findAll('div', {'class':'mediaRowWrapper '})
                for exp_title in exp_titles:
                    exps = exp_title.findAll('div', {'class':'mediaPageName'})
                    for exp in exps:
                        activities += exp.string + ', '

        print activities

        # ==================================================
        # TODO 
        profile_groups = profiles.find('div',{'id':'pagelet_profile_groups'})
        
        # ==================================================
        # Basic personal information.
        profile_basic = profiles.find('div',{'id':'pagelet_basic'})
        basicinformation = 'Basic Information - ' 
        if profile_basic == None:
            basicinformation += 'private.'
        else:
            basicinfo = profile_basic.find('h4')
            if basicinfo != None:
                section = basicinfo.string 
                basicinfo = profile_basic.find('div',{'class':'phs'})
                basics = basicinfo.findAll('th',{'class':'label'})
                for basic in basics:
                    label = basic.string 
                    basicinformation += label + ':'
                    for content in basic.nextSibling.contents: 
                        if unicode(content).find('<') == -1: # remove tags like <br/>
                            basicinformation += unicode(content)
                    basicinformation += '; '

        # ==================================================
        # Contact information. 
        profile_contact = profiles.find('div',{'id':'pagelet_contact'})
        contacts = 'Contact Information - '
        if profile_contact == None:
            contacts += 'private.'
        else:
            contact = profile_contact.find('div',{'class':'phs'})
            conts = contact.findAll('th',{'class':'label'})
            for cont in conts:
                label = cont.string
                contacts += label + ':'
                if label.find('Screen') != -1: # screen name.
                    screen_names = cont.nextSibling.findAll('li')
                    for screen_name in screen_names:
                        contacts += str(screen_name.contents[0])
                        contacts += screen_name.span.string + ','
                if label.find('Website') != -1: # Websites. 
                    websites = cont.nextSibling.findAll('a')
                    for website in websites:
                        contacts += website['href'] + ', ' 
                if label.find('Email') != -1: # Email. 
                    emails = cont.nextSibling.findAll('li')
                    for email in emails: 
                        contacts += email.string + ', ' 
                if label.find('Phone') != -1: # Phone.
                    phones = cont.nextSibling.findAll('li')
                    for phone in phones:
                        contacts += str(phone.contents[0]) + '(' + phone.span.string + '), '
                if label.find('Facebook') != -1: # Facebook. 
                    facebook = cont.nextSibling.a.string
                    contacts += facebook + ', ' 
                if label.find('Address') != -1: # Address. 
                    addresses = cont.nextSibling.findAll('li')
                    for address in addresses: 
                        contacts += address.string + ', '
        print contacts

        # ==================================================
        # TODO
        profile_privacy_notice = profiles.find('div',{'id':'pagelet_privacy_notice'})
        print
        return basicinformation + ' | ' + experience + ' | ' + contacts + ' | ' + activities 

    def getWallPosts(self, friend_id):
        ''' Get wall posts for the friends starting from
        friend_id. '''
        print " - Extracting wallposts for : " + friend_id, 
        if re.compile('\D').findall(friend_id) == []: # friend id is numerical.
            friend_link = self.getProfileLink(friend_id) + '&sk=wall'
        else:                   # friend id is alphabetical.
            friend_link = self.getProfileLink(friend_id) + '?sk=wall'
        wallposts = ''
        try:                    # handle HTTP request error!!
            pw = self.browser.open(friend_link)
            pws = BeautifulSoup(''.join(pw.get_data()))
        except:
            self.login()
            return 
        pws1 = pws.find(name='div',attrs={'id':'pagelet_homme_stream'})
        posts = pws.findAll('div',{'class':re.compile('storyInnerContent*')})
        events = ''         # post events.
        for post in posts:
            datetime = post.find('abbr')['data-date'] # date and time.
            # Author of the post, might be like events. 
            ''' There are several types of events for the wall post. 
            First, [author] do something; 
            Second, [some-friend] likes something; 
            Third, [some-friend] was tagged by [somebody] at [somewhere]; 
            Fourth, [some-body] commented on [some-body]'s content;
            Fifth, TO be added. '''
            author = post.find('div',{'class':re.compile('actorName*')})
            wallposts += ' | ' + events
            events = ''
            if author == None:           
                passive = post.find('h6').a
                p = passive.string
                try:            # likes and dislikes and comments. 
                    a = passive.nextSibling.nextSibling.string
                    events = p+' likes/commented on '+ a +'\'s post/photo!'
                except:         # tagging event. 
                    a = passive.nextSibling.nextSibling.nextSibling.nextSibling.string
                    events = p+' was tagged on '+a+'\'s photo!'
            else:               # [author] do something.
                if author.a.contents != None:
                    events = author.a.string
            # Message contents. 
            message = post.find('span')
            if message.string != None:
                events += " MESSAGE: " + message.string
            elif message.contents != []:
                if isinstance(message.contents[0], str): 
                    events += " MESSAGE: " + message.contents[0]

        return wallposts 
