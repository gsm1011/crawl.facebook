from bs4 import BeautifulSoup
import sys

file = open(sys.argv[1], 'r')
str = ''.join(file.read());

# print str
soup = BeautifulSoup(str); 

print soup.prettify().encode('ascii', 'replace'); 
