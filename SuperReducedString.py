import sys


s = raw_input().strip()
s= list(s)
i=0
while i < len(s)-1:
    if s[i]== s[i+1]:
        del(s[i])
        del(s[i])
        i=0
        if len(s) == 0:
            print 'Empty String'
            break
    else:
        i+=1
print ''.join(s)