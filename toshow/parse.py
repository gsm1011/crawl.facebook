from bs4 import BeautifulSoup
import codecs, sys

html_doc = ''.join(open('test.html', 'r').read())

soup = BeautifulSoup(html_doc)

# print soup.prettify().encode('ascii', 'replace') 

divs = soup.find_all('div', recursive=True)

for div in divs:
    print div
    for ul in div.find_all('ul', recursive=False): print ul 
    for span in div.find_all('span', id='userContent'): print span
    
# str = '<div><div><div></div></div></div>'
# for div in BeautifulSoup(str).find_all('div', recursive=True):
#     print div
