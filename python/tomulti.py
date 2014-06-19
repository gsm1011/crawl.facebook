#!/usr/bin/python 
import sys
# @brief This script is used to tranform facebook binary data to
# multiple valued data. 
# Rules:
#+----------+---------+----------+-----------+---------+
#| Account  |   YES   | YES      |   NO      |  NO     |
#|----------+---------+----------+-----------+---------+
#| Nobody   |   YES   | NO       |   YES     |  NO     |
#+----------+---------+----------+-----------+---------+
#| New Value| Everyone| f/ff     |   ERROR   | Only ME |
#+----------+---------+----------+-----------+---------+
# Assign value according to the following: 
# private: 0
# f/ff: 1 (friend/friend of friend)
# everyone: 2

# read in the data into hash table with key as the user id and value
# as the binary item settings. 
fuser = open(sys.argv[1])
fnobody = open(sys.argv[2])

userdata = {}
nobodydata = {}
u = []
for user in fuser:
    [u.append(i) for i in user.strip().split(',')]
    uid = u[0]
    userdata[uid] = u[1:]
    del u[:]

for nobody in fnobody:
    [u.append(i) for i in nobody.strip().split(',')]
    uid = u[0]
    nobodydata[uid] = u[1:]
    del u[:]

# Build the multiple value data for the generated data. 
for key in userdata.keys():
    if nobodydata.has_key(key):
        binuser = userdata[key]
        binnobody = nobodydata[key]
        result = ''
        if len(binuser) == len(binnobody) and len(binuser) == 28:
            #print key,
            length = len(binuser)
            # print length
            for i in range(length):
                # print binuser[i], binnobody[i]
                bu = binuser[i]
                bn = binnobody[i]
                if bu == 'Y' and bn == 'Y': result += '2,'
                if bu == 'Y' and bn == 'N': result += '1,'
                if bu == 'N' and bn == 'Y': result += 'x,' # impossible, remove it from data. 
                if bu == 'N' and bn == 'N': result += '0,'
            # filter out the errous items. 
            if result.find('x') < 0: 
                print result#, key
        else:
            print 'binary data is of wrong format.'
            continue

fuser.close()
fnobody.close()
