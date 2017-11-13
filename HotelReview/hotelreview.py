'''
FileName : hotelreview.py
Date : 20/6/2017
'''
#import libraries
import json
import re,operator
import collections
from collections import *
import time

#function for all operations
def readfile():
    #filename to readfile
    file ='feedback.txt'
    search = input("Enter the words you want to search for (separate with commas): ") #enter the words to count
    search = [word.strip() for word in search.lower().split(",")]
    # create a dictionary for all search words, setting each count to 0
    search_dict = dict.fromkeys(search, 0)

    wordcount ={} #dictionary for mention's count
    topstat ={} #dictionary for topstatement
    hotelid ={} #dictionary for hotelid
    temp_topstatement={} #temeparory dictionary for topstatement
    temp_topstatement = defaultdict(lambda:0,temp_topstatement) #defaultdict for temp_topstatement
    topstatement={"desc":None,"hotelid":None} #intialize json with none
    temp = 0
    #open file to read
    with open(file, 'r') as f:
        file_read= f.read()

        #counting number of mentions in file
        file_split = re.findall(r'[^,.;\s]+',file_read) #remove unwanted character from word list
        for word in search_dict:
            wordcount[word] = file_split.count(word) #count number of mentions in file

        #now counting feedback which has highest number of mentions in file
        ftopstatment = file_read.split('\n') #split with new line to count number of mentions for individual feedback
        for i in range(len(ftopstatment)):
            count=0
            #condition to count only for feedback not for hotelid
            if i % 2 == 0:
                pass
            else:
                filesplit = re.findall(r'[^,.;\s]+',ftopstatment[i]) #remove unwanted character from word list
                for word in search_dict:
                    count += filesplit.count(word) #count number of mentions for individual feedback
                temp_topstatement[ftopstatment[i-1]] += count
                if count > temp: #compare it with temperory count to find maximum of count
                    temp = count
                    topstatement['hotelid'] = ftopstatment[i-1] #assign hotelid
                    topstatement['desc'] = ftopstatment[i] #assign feedback for that hotelid

        #now sort hotelid according to count of mentions
        sorted_output = sorted(temp_topstatement.items(), key=operator.itemgetter(1),reverse=True)
        finaloutput = []
        #find hotelid based on desending order
        for i,j in sorted_output:
            #append hotelid into finaloutput
            finaloutput.append(i)
        #make finaljson
        finalJson = {"mentions":wordcount,"topstatement":topstatement,"hotels":finaloutput}
        print(finalJson)
        print("--- %s seconds ---" % (time.time() - start))

if __name__ == "__main__":
    start = time.time()
    print("start:  ",start)
    readfile()
