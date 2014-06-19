#!/usr/bin/env python 
import sys

## print sys.argv[0], sys.argv[1]

uid=sys.argv[1].strip('.txt') 

##print uid

f = open(sys.argv[1], 'r')

for line in f:
    parts = line.split(',')
    if (len(parts) != 3): pass
    print uid+','+parts[0] + ',' + parts[1]
