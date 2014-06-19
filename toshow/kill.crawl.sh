jps | grep CrawlFacebook | awk '{print $1}' | xargs kill -9
ps -ef | grep firefox | awk '{print $2}' | xargs kill -9
