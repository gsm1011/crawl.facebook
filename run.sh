#!/usr/bin/env bash
# Run Xvfb before running this script.

if [[ -z $1 ]]; then 
  echo 'Empty display variable!'
  exit;
fi

FACEBOOK=/home/shumin/data/facebook
SELENIUM=$FACEBOOK/selenium

CLASSPATH=$SELENIUM/selenium-java-2.33.0.jar:$FACEBOOK

for file in $SELENIUM/libs/*.jar; do
    CLASSPATH=$CLASSPATH:${file}
done

export CLASSPATH
export DISPLAY=:$1
#javac CrawlFacebook.java

echo $DISPLAY
java CrawlFacebook
