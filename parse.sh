#!/usr/bin/env bash
if [[ -z $1 ]]; then 
  echo "Empty display variable!"
  exit; 
fi

# Environment variables.
FACEBOOK=/home/shumin/data/facebook
SELENIUM=$FACEBOOK/selenium
CLASSPATH=$SELENIUM/selenium-java-2.33.0.jar:$FACEBOOK
for file in $SELENIUM/libs/*.jar; do
    CLASSPATH=$CLASSPATH:${file}
done

export CLASSPATH
export DISPLAY=:$1
##javac CrawlFacebook.java
#javac ParseInfo.java

java ParseInfo $2 $3
