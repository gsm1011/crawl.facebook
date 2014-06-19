#!/usr/bin/bash

FBDIR=/home/shumin/data/facebook
RUN=$FBDIR/run.sh
PARSE=$FBDIR/parse.sh

if [ $1 -eq 1 ]; then 
  cd $FBDIR/shumin.guo && nohup $PARSE 97 shumin.guo.about/ shumin.guo.xmls/ &
  cd $FBDIR/min.han && nohup $PARSE 98 min.han.902.about/ min.han.902.xmls/ &
  cd $FBDIR/keke.chen && nohup $PARSE 99 gtkeke.about/ gtkeke.xmls/ &
else 
  cd $FBDIR/steve.shumin.guo && nohup $PARSE 100 steven.guo.142.about/ steven.guo.142.xmls/ &
  cd $FBDIR/shawn.min.han && nohup $PARSE 101 shawn.guo.315.about/ shawn.guo.315.xmls/ &
  cd $FBDIR/jason.gtkeke && nohup $PARSE 102 jason.guo.313.about/ jason.guo.313.xmls/ &
fi
