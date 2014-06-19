#!/usr/bin/env python
import contextlib
from pyvirtualdisplay import Display 
from selenium import webdriver
import selenium.webdriver.support.ui as ui # waiting. 
from selenium.webdriver.common.keys import Keys
import codecs


#def wait_for_ajax(browser, timeout=5000):
#    js_condition = 'selenium.browserbot.getCurrentWindow().jQuery.active == 0'
#    browser.wait_for_condition(js_condition, timeout)

display = Display(visible=0, size=(1024, 768))
display.start() 

# Create the firefox web browser
browser = webdriver.Firefox()
browser.get('https://www.facebook.com')
assert 'Facebook' in browser.title 

# login to facebook. 
email = 'gsmsteve@gmail.com'
pwd = 'Gsm1011!'
browser.find_element_by_id('email').clear()
browser.find_element_by_id('email').send_keys(email)

browser.find_element_by_id('pass').clear()
browser.find_element_by_id('pass').send_keys(pwd)

browser.find_element_by_id('pass').send_keys(Keys.RETURN)

browser.get('https://www.facebook.com/lebin.lin.9/friends')

wait = ui.WebDriverWait(browser, 10)
wait.until(lambda browser: 'More About' in browser.page_source)

out = codecs.open('out.selen', 'w', encoding='utf-8')
out.write(unicode(browser.page_source)); 

out.close() 
# print browser.page_source.decode('utf-8', 'ignore')OBOBB
