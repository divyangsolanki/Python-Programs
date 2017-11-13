'''
FileName : passwordgenerator.py
Date : 15/6/2017
'''
#import libraries
import string,json
import random
from datetime import datetime, timedelta
from random import choice

def passwordgenerator(list1):
    #enter length of password
    m =int(input("Length of password: "))
    #check whether length is more than 8 or not
    if(m>=8):
        l = []
        #how many password you want to generate.
        for _ in range(int(input("Number of password to Generate: "))):
            #make set of every things that you want in your password like letters, numbers etc.
            set1 = string.ascii_uppercase
            set2 = string.digits
            set3 = string.ascii_lowercase
            set4 = string.punctuation
            set5 = string.ascii_lowercase + string.ascii_uppercase + string.punctuation  + string.digits
            #password should contain atleast 1 uppercase letter, 1 lowercase letter, 1 digit and 1 special character.
            build = [set1, set2, set3, set4] + [set5] * (m)
            #shuffle all items
            random.shuffle(build)
            password = []
            index = 0
            last = ''
            #join every item from build until it reached password length
            while len(password) < m:
                #randomly generate password
                choice = random.choice(build[index])
                if choice != last:
                    password.append(choice)
                    index += 1
                    last = choice
            password = ''.join(password) # join every character
            current_time = datetime.now()
            expire_time = datetime.now() + timedelta(seconds=15) # password will expire in 15 seconds
            expire_time += timedelta(seconds=15)
            store ='Password ::  '+ password + "   CreatedTime ::  "+ str(current_time) +'  Expire_time ::  '+ str(expire_time)
            l.append(store)
        return l
    else:
        print("Password length should be more than 8")

def passwordvalidator(store):
    passw = input("Enter password to check it's validity :: ")
    match= [x for x in store if passw in x]
    match1= str(match)
    print(match1)
    position = match1.find('Expire_time ::  ')
    match_str = match1[position+16:-2]
    pass_time = datetime.strptime(match_str,'%Y-%m-%d %H:%M:%S.%f')
    print("Expire_time : ",pass_time)
    print("Current_time: ",datetime.now())
    if pass_time < datetime.now():
        print("Password has been expired")
    else:
        print("Password has not been expired")

if __name__ == "__main__":
    print("--------------")
    print("Choice 1: For passwordgenerator")
    print("Choice 2: For passwordvalidator")
    print("Choice 3 : Exit")
    print("--------------")
    while True:
        choice = int(input("Enter your choice :: "))
        list1 =[]
        if choice == 1:
            passl = passwordgenerator(list1)
            print(passl)
        elif choice == 2:
            passwordvalidator(passl)
        else:
            exit()
