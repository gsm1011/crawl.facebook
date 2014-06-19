import MySQLdb

class fbdb:
    ''' This class will be responsible for storage and retrieval of the crawled
    data. Possible tables that this class will handle include : 
    friend_link, wall_post, profile, messages etc. '''
    def __init__(self):
        self.friends = {} 
        
    def setupdb():
        ''' Create database and tables. '''
        try:
            connect()
            cursor.execute('''CREATE DATABASE IF NOT EXISTS facebook;''')
            # create the profile link table. 
            cursor.execute('''CREATE TABLE IF NOT EXISTS friend_link(
                  fid varchar(20) not null, name varchar(20), 
                  profile_link varchar(100), primary key(fid));''') 
        except MySQLdb.Error, e:
            print "Error %d: %s" % (e.args[0], e.args[1])

    def connect(self):
        ''' Connect to the database. '''
        conn = MySQLdb.connect(host='localhost',
                               user='ada',
                               passwd='825123',
                               db='facebook')
        cursor = conn.cursor() 
        return cursor 

    def insert_profile_link(self, fid, name, plink):
        ''' Insert profile link into the table. ''' 
        try:
            cursor = self.connect() 
            value = "\""+fid+"\",\""+name+"\",\""+plink+"\""
            print value 
            cursor.execute('''
                 INSERT INTO friend_link (fid,name,profile_link)
                 VALUES ('''+ value +''')''')
            print "Inserted " + str(cursor.rowcount) + " inserted!!"
            
        except MySQLdb.Error, e:
            print "Error %d: %s" % (e.args[0], e.args[1]) 
            return False
        finally:
            cursor.close()
        return True

    def query_profile_link(self):
        ''' Query records of the friend_link table; '''
        try:
            cursor = self.connect() 
            cursor.execute('''SELECT * FROM friend_link;''')
            while(1):
                row = cursor.fetchone()
                if row == None:
                    break
                print "%s, %s, %s" % (row[0], row[1], row[2]) 
            print "Totally fetched %d records!" % cursor.rowcount

        except MySQLdb.Error, e:
            print "Error %d: %s" % (e.args[0], e.args[1]) 
            return False
