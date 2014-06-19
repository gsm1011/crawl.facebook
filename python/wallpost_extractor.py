import mechanize, re, sys, codecs
from BeautifulSoup import BeautifulSoup, NavigableString, UnicodeDammit, Tag

def extractWallPosts(page):
    ''' Get wall posts for the friends starting from
    friend_id. '''
    DEBUG = False
    
    # Please comment following line if used as module. 
    # page = ''.join(open(page).read())
    
    wallsoup = BeautifulSoup(page)
    wallposts_list = wallsoup.find(name='ul',attrs={'id':'profile_minifeed'})
    if wallposts_list == None:
        return 'Wallposts INVISIBLE.\n'

    # Common wallpost stories. 
    stories = wallposts_list.findAll('li',{'class':re.compile('pvm uiUnifiedStory*')})
    
    wallposts = unicode('\nCOMMON STORIES:\n')
    if DEBUG:
        print '\nCOMMON STORIES.'
    for story in stories:
        # Actors. 
        actor = story.find('h6')
        proactor = actor.find('a')
        proactor_name = proactor.string 
        wallposts += '\nACTOR: ' + proactor_name + '\n'
        if DEBUG:
            print '\nACTOR:', proactor_name # , proactor['href']
        
        # ==================================================
        # Short messages by author. 
        short_msg = actor.find('span',{'class':'messageBody'})
        if short_msg != None: 
            msg = short_msg.string
            wallposts += 'MESAGE: '
            if DEBUG:
                print 'MESSAGE:', 
            if msg != None:
                wallposts += unicode(msg) + '\n'
                if DEBUG: 
                    print msg
                else: 
                    msgs = short_msg.contents
                    for msg in msgs: 
                        if isinstance(msg, NavigableString):
                            wallposts += unicode(msg) + ' '
                            if DEBUG:
                                print unicode(msg), 
            # contents hide in span.           
            span_msg = short_msg.find('span')
            if span_msg != None:
                wallposts += unicode(span_msg.string)
                if DEBUG: 
                    print span_msg.string, 
            wallposts += '\n'
            if DEBUG:
                print 
        # Attachment, which is actually shared message contents from other people or party. 
        body_title = story.find('div', {'class':'uiAttachmentTitle'})
        if body_title != None: 
            title = body_title.find('a')
            if title != None: 
                wallposts += 'ATTACH_TITLE: ' + unicode(title.string) + '\n'
                if DEBUG:
                    print 'ATTACH_TITLE:', title.string
        # TODO:subtitle 
        # body_subtitle = 
        body_desc = story.find('div', {'class':'mts uiAttachmentDesc'})
        if body_desc != None:
            desc = body_desc.string
            wallposts += 'ATTACH_DESC: ' + unicode(desc) + '\n'
            if DEBUG:
                print 'ATTACH_DESC:', desc 

        # Comments for story. 
        comment_block = actor.nextSibling.form
        msg_footer = comment_block.find('span',{'class':'uiStreamFooter'})
        time = msg_footer.find('abbr')
        if time != None: 
            wallposts += 'MSG_TIME: ' + unicode(time['data-date']) + '\n'
            if DEBUG:
                print 'MSG_TIME:',time['data-date']
        # comment contents, note that some story does not have comments. 
        commentlist = comment_block.find('ul')
        if commentlist != None: 
            comments = commentlist.findAll('li')
            for comment in comments:
                # Anyone likes these items? 
                likes = comment.find('a',{'title':'See people who like this item'})
                if likes != None:
                    wallposts += 'LIKES: ' + unicode(likes.string) + ' like this.\n'
                    if DEBUG:
                        print 'LIKES:', likes.string, 'like this.'
                # Only one/two person like(s) this. 
                else:
                    likes = comment.find('div',{'class':'UIImageBlock_Content UIImageBlock_ICON_Content'})
                    if likes != None:
                        likes = likes.contents
                        wallposts += 'LIKES:' 
                        if DEBUG:
                            print 'LIKES:',
                        for like in likes:
                            if isinstance(like, Tag): 
                                soup = BeautifulSoup(unicode(like))
                                link = soup.find('a')
                                if link != None:
                                    wallposts += unicode(like.string) + ' '
                                    if DEBUG:
                                        print link.string,
                            elif isinstance(like, NavigableString): 
                                wallposts += unicode(like)
                                if DEBUG: 
                                    print unicode(like),
                        wallposts += '\n'
                        if DEBUG:
                            print

                # Comment contents by people. 
                cmt_contents = comment.findAll('li')
                for cmt_content in cmt_contents: 
                    # How many comments are made? if a lot, it will not be displayed. 
                    cnt_str = unicode(cmt_content)
                    cmt_num_start = cnt_str.find('View all')
                    if cmt_num_start > 0: 
                        cmt_num_end = cnt_str.find('comments')
                        wallposts += 'COMMENT_NUM: ' + unicode(cnt_str[cmt_num_start+9:cmt_num_end+8]) + '\n'
                        if DEBUG: 
                            print 'COMMENT_NUM:', cnt_str[cmt_num_start+9:cmt_num_end+8]
                    # who make comment? 
                    cmt_actor_link = cmt_content.find('a',{'class':'actorName'})
                    if cmt_actor_link != None:
                        cmt_actor = cmt_actor_link.string
                        wallposts += 'COMMENTOR: ' + unicode(cmt_actor) + '\n'
                        if DEBUG:
                            print 'COMMENTOR:',cmt_actor
                    # What is the comment. 
                    cmt_cnt_block = cmt_content.find('span')
                    if cmt_cnt_block != None: 
                        cmt_cnt = cmt_cnt_block.string
                        wallposts += 'COMMENT: ' + unicode(cmt_cnt) + '\n'
                        if DEBUG:
                            print 'COMMENT:',cmt_cnt
                    # When was the comment made? 
                    cmt_datetime = cmt_content.find('abbr')
                    if cmt_datetime != None: 
                        cmt_time = cmt_datetime.string 
                        wallposts += 'COMMENT_TIME: ' + unicode(cmt_time) + '\n'
                        if DEBUG:
                            print 'COMMENT_TIME:',cmt_time

                        
    # A group of current activity lists. RECENT ACTIVITY
    # ============================================================================
    wallposts += '\nRECENT ACTIVITY\n'
    if DEBUG:
        print '\nRECENT ACTIVITY'
    activitylists = wallposts_list.findAll('li',{'class':re.compile('pvm uiStreamMinistoryGroup*')})
    # event types identifiers. 
    # The class of the small image icon. 
    friend_event = 'sp_4lqaeu sx_2115d3' 
    like_event = 'sp_16l9lq sx_3221eb'
    comment_event = 'sp_bhj0aj sx_1a05ed'
    status = 'sp_dra1ca sx_d48cdd'
    for activitylist in activitylists: 
        activities = activitylist.findAll('li')
        for activity in activities:
            # Identify event types. 
            event_type = activity.find('i')
            event_type_user = activity.find('img') # non-facebook event.
            if event_type == None: 
                continue
            act_title = activity.find('h6')

            # A unified way to extract recent events.
            if act_title == None: 
                continue
            contents = act_title.contents
            recent_event = ''
            for content in contents: 
                if isinstance(content, Tag): 
                    soup = BeautifulSoup(unicode(content))
                    link = soup.find('a')
                    if link != None:
                        evenmore = link.find('span',{'class':'uiTooltipText'})
                        if evenmore != None:
                            othername = unicode(evenmore).replace('<br />', ', ')                        
                            othername = othername.replace('<span class="uiTooltipText">', '')
                            othername = othername.replace('</span>', '')
                            wallposts += unicode(othername) + ' '
                            if DEBUG:
                                print othername, 
                        else: 
                            wallposts += unicode(link.string) + ' ' 
                            if DEBUG:
                                print link.string,    
                    span = soup.find('span')
                    if span != None:
                        wallposts += unicode(span.string) + ' '
                        if DEBUG:
                            print span.string,

                if isinstance(content, NavigableString): 
                    wallposts += unicode(content) + ' '
                    if DEBUG:
                        print unicode(content),
            wallposts += '\n'
            if DEBUG: 
                print 
       
    if DEBUG:
        print wallposts 
    return wallposts

if __name__ == '__main__':
    ''' Module test, the first parameter is the wall post page. ''' 
    print extractWallPosts(sys.argv[1])
