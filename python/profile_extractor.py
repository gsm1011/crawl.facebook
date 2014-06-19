import mechanize, sys
from BeautifulSoup import BeautifulSoup, NavigableString, UnicodeDammit, Tag

def extractProfile(page):
    ''' extract the profiles of a user. ''' 
    # print 'Extracting profile ... ... '
    extract_content = False
    DEBUG = True 
    # page = ''.join(open(page,'r').read())
    profile_soup = BeautifulSoup(page)
    profile_contents = ''
    content_sep = '\t'
    # profile_soup = BeautifulSoup(''.join(open(page).read()))
    # print profile_soup
    # Personal information like location, hometown. 
    profile_bylines = profile_soup.find('div',{'class':'fbProfileByline'})
    byline_profile = 'PROFILE:'
    if profile_bylines != None: 
        bylines = profile_bylines.findAll('span',{'class':'fbProfileBylineFragment'})
        if len(bylines) == 0:
            byline_profile += 'N/A'
        for byline in bylines: 
            byline_contents = byline.contents
            for byline_content in byline_contents:
                if isinstance(byline_content, NavigableString):
                    byline_profile += unicode(byline_content)
                if isinstance(byline_content, Tag):
                    soup = BeautifulSoup(unicode(byline_content))
                    byline_link = soup.find('a')
                    if byline_link != None:
                        if byline_link.string != None:
                            byline_profile += unicode(byline_link.string)
            byline_profile += '|'
    else:
        byline_profile += 'N/A'

    profile_contents += byline_profile + content_sep
    if DEBUG:
        print byline_profile

    profiles = profile_soup.find('div',{'id':'pagelet_main_column_personal'})
    #print profiles 
    if profiles == None: return profile_contents + 'failed'
    profiles = BeautifulSoup(str(profiles))
    profile_eduwork = profiles.find('div',{'id':'pagelet_eduwork'})
    experience = 'edu_work:'
    if profile_eduwork == None:
        experience += 'N/A,'
    else: 
        eduwork = profile_eduwork.find('div',{'class':'phs'})
        if eduwork == None: 
            experience += 'N/A,' 
        else:
            ''' Find experience label, like jobs, schools. '''
            exps = eduwork.findAll('th', {'class':'label'})
            ''' get Education and work experiences. '''
            for exp in exps:
                label = exp.string 
                if extract_content == False: 
                    experience += label + ','
                    continue
                else:
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
    if DEBUG:
        print experience 

    profile_phil = profiles.find('div',{'id':'pagelet_philosophy'})
    philosophy = 'philosophy:'
    if profile_phil == None:
        philosophy += 'N/A,'
    else: 
        phil = profile_phil.find('div',{'class':'phs'})
        if phil == None: 
            philosophy += 'N/A,' 
        else:
            ''' Find philosophy label, like jobs, schools. '''
            exps = phil.findAll('th', {'class':'label'})
            ''' get philosophy. '''
            for exp in exps:
                label = exp.string 
                if extract_content == False: 
                    philosophy += label + ','
                    continue
                else: 
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
    if DEBUG:
        print philosophy

    profile_arts = profiles.find('div',{'id':'pagelet_arts_and_entertainment'})
    arts_entertain = 'arts_entertain:'
    if profile_arts == None:
        arts_entertain += 'N/A,'
    else: 
        arts = profile_arts.find('div',{'class':'phs'})
        if arts == None: 
            arts_entertain += 'N/A,' 
        else:
            ''' Find arts_entertain label, like jobs, schools. '''
            exps = arts.findAll('th', {'class':'label'})
            ''' get arts_entertain. '''
            for exp in exps:
                label = exp.string 
                if extract_content == False: 
                    arts_entertain += label + ','
                    continue
                else: 
                    arts_entertain += label + ':'
                exp_titles = exp.nextSibling.findAll('div', {'class':'mediaPageName'})
                for exp_title in exp_titles:
                    arts_entertain += ' ' + exp_title.string
                    arts_entertain += ','
    if DEBUG:
        print arts_entertain

    profile_activity = profiles.find('div',{'id':'pagelet_activities_and_interests'})
    activities = 'act_interests:'
    if profile_activity == None:
        activities += 'N/A,'
    else:
        activity = profile_activity.find('div',{'class':'phs'})
        if activity == None: 
            activities += 'N/A,'
        else:
            acts = activity.findAll('th',{'class':'label'})
            for act in acts:
                try:        # to avoid the empty contents.
                    label = act.string
                    if label.string != None: 
                        if extract_content == False: 
                            activities += label + ','
                            continue
                        else: 
                            activities += label + ':'
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
                
                exp_titles = act.nextSibling.findAll('div', {'class':'mediaRowWrapper '})
                for exp_title in exp_titles:
                    exps = exp_title.findAll('div', {'class':'mediaPageName'})
                    for exp in exps:
                        activities += exp.string + ', '
                
                act_lists = act.nextSibling.findAll('a')
                for act_list in act_lists: 
                    if act_list.string != None:
                        activities += ' ' + act_list.string + ','
                activities += '; '
    if DEBUG:
        print activities

    profile_basic = profiles.find('div',{'id':'pagelet_basic'})
    basicinformation = 'basic_info:' 
    if profile_basic == None:
        basicinformation += 'N/A,'
    else:
        basicinfo = profile_basic.find('h4')
        if basicinfo == None:
            basicinformation += 'N/A,'
        else:
            section = basicinfo.string 
            basicinfo = profile_basic.find('div',{'class':'phs'})
            basics = basicinfo.findAll('th',{'class':'label'})
            for basic in basics:
                label = basic.string 
                if extract_content == False: 
                    basicinformation += label + ','
                    continue
                basicinformation += label + ':'
                for content in basic.nextSibling.contents: 
                    if unicode(content).find('<') == -1: # remove tags like <br/>
                        basicinformation += unicode(content)
                    basicinformation += '; '
    if DEBUG:
        print basicinformation

    profile_contact = profiles.find('div',{'id':'pagelet_contact'})
    contacts = 'contact:'
    if profile_contact == None:
        contacts += 'N/A,'
    elif str(profile_contact.string) is None:
        contacts += 'N/A,'
    else:
        contact = profile_contact.find('div',{'class':'phs'})
        if contact == None: 
            contacts += 'N/A,'
        else:
            conts = contact.findAll('th',{'class':'label'})
            for cont in conts:
                label = cont.string
                if extract_content == False: 
                    contacts += label + ','
                    continue
                else:
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
    if DEBUG:
        print contacts

    if extract_content == False:
        profile_contents += experience[0:len(experience)-1] + content_sep
        profile_contents += philosophy[0:len(philosophy)-1] + content_sep
        profile_contents += arts_entertain[0:len(arts_entertain)-1] + content_sep
        profile_contents += activities[0:len(activities)-1] + content_sep
        profile_contents += basicinformation[0:len(basicinformation)-1] + content_sep
        profile_contents += contacts[0:len(contacts)-1] + content_sep
    else:         
        profile_contents += experience + content_sep
        profile_contents += philosophy + content_sep
        profile_contents += arts_entertain + content_sep
        profile_contents += activities + content_sep
        profile_contents += basicinformation + content_sep
        profile_contents += contacts + content_sep
        
    if DEBUG == True:
        print profile_contents 
        print 
    return profile_contents


if __name__ == '__main__':
    print "Extract profile"
    print extractProfile(sys.argv[1])
    # extractProfile('profile1.html')
    # extractProfile('profile2.html')
    # extractProfile('profile3.html')
    # extractProfile('profile4.html')
