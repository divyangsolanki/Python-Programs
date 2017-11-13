import sqlite3
con = sqlite3.connect("todo.db")
cur = con.cursor()
cur.execute("CREATE TABLE IF not exists users ( id integer primary key autoincrement,username text,password text)")
