FBDIR=/home/shumin/data/facebook
RUN=$FBDIR/run.sh
PARSE=$FBDIR/parse.sh

if [ $1 -eq 1 ]; then 
  #cd $FBDIR/shumin.guo && nohup $RUN 97 &
  #cd $FBDIR/min.han && nohup $RUN 98 &
  cd $FBDIR/keke.chen && nohup $RUN 99 &
else 
  echo 'Error'
  #cd $FBDIR/steve.shumin.guo && nohup $RUN 100&
  cd $FBDIR/shawn.min.han && nohup $RUN 101&
  #cd $FBDIR/jason.gtkeke && nohup $RUN 102&
fi
