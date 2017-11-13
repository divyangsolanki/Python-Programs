#import libraries
import sqlite3
from flask import Flask
from flask import Flask, flash, redirect, render_template, request, session, abort, url_for
import os
from flask_login import login_required

app = Flask(__name__)
#----------------------------------------------------------------------
#Function for updating the fields of todo list like Description and Due_date.
@app.route('/update/<int:Task_id>', methods=['GET','POST'])
def edit_item(Task_id):
    """
    update a TODO item
    """

    if request.method == 'POST':
        #get the parameters from html form
        Description = request.form['Description']
        status = request.form['status']
        Due_date = request.form['Due_date']

        #if status of task is open then it will set the value=1
        if status == 'open':
            status = 1
        else:
            status = 0

        #conncetion to the database
        conn = sqlite3.connect('todo.db')
        c = conn.cursor()

        #update query to update fields of task
        c.execute("UPDATE task SET Description = ?, Due_date = ?, Modified_date = Date('now'), status = ? WHERE Task_id LIKE ?", (Description, Due_date, status, Task_id))
        conn.commit()
        return redirect("/todo")
    else:
        Task_id = str(Task_id)
        #conncetion to the database
        conn = sqlite3.connect('todo.db')
        c = conn.cursor()

        #select query to get the Description of particular task
        c.execute("SELECT Description,Due_date FROM task WHERE Task_id = ?",[Task_id])
        cur_data = c.fetchone()
        conn.commit()
        return render_template('update_task.html',Task_id= Task_id,old= cur_data)

#----------------------------------------------------------------------
#Function for deleting the particular task
@app.route("/delete/<int:Task_id>")
def deleted(Task_id):
    """
    Delete items
    """
    #conncetion to the database
    conn = sqlite3.connect('todo.db')
    c = conn.cursor()
    Task_id = str(Task_id)
    #delete task based on Task_id
    c.execute("DELETE FROM task WHERE Task_id LIKE ? ",[Task_id])
    conn.commit()
    return redirect("/todo")

#----------------------------------------------------------------------
#Function to get list of task which are closed or done
@app.route("/done")
def show_done():
    """
    Show all items that are done
    """
    #conncetion to the database
    conn = sqlite3.connect('todo.db')
    c = conn.cursor()

    #if status of task is 0 it means task is completed.
    c.execute("SELECT Task_id, Description FROM task WHERE status LIKE 0")
    result = c.fetchall()
    c.close()
    return render_template("show_done.html", rows=result)

#----------------------------------------------------------------------
#Function to get list of task which are pending

@app.route("/todo",methods=['GET','POST'])
def todo_list():
    """
    Show the main page which is the current TODO list
    """
    if 'logged_in' not in session:
        return render_template('login.html')
    else:
        #conncetion to the database
        conn = sqlite3.connect("todo.db")
        c = conn.cursor()

        #select query to get all values of task
        c.execute("SELECT Task_id, Description, Due_date, Modified_date FROM task WHERE status LIKE '1'")
        result = c.fetchall()
        c.close()
        return render_template("task_list.html", rows=result)

@app.route('/signup',methods=['GET','POST'])
def signup():

    conn = sqlite3.connect("todo.db")
    c = conn.cursor()
    username = "admin"#request.form('username')
    password = "admin"#request.form('password')
    c.execute("INSERT into users (username,password) values (?,?)",(username,password))
    return render_template('login.html')

@app.route('/login', methods=['POST'])
def do_admin_login():
    conn = sqlite3.connect("todo.db")
    c = conn.cursor()

    c.execute("select * from users WHERE username= ?",[request.form['username']])
    result = c.fetchone()
    if result:
        if request.form['password'] == result[2] and request.form['username'] == result[1]:
            session['logged_in'] = True
            session['username'] = result[1]
        else:
            return "error : Login Failed"
    return redirect('/todo')

@app.route('/logout', methods=['GET'])
def do_admin_logout():
    if session.get('logged_in') and session['logged_in'] == True:
        session['logged_in'] = False
        session.pop('logged_in',None)
    else:
        return "error : there is no any login!"
    return todo_list()


#----------------------------------------------------------------------
#Function for getting list of tomorrow's taks
@app.route("/tomorrowlist", methods=["GET"])
def tomorrowlist():
    #conncetion to the database
    conn = sqlite3.connect("todo.db")
    c = conn.cursor()

    #select query to get all values of list of tomorrow
    c.execute("SELECT Task_id, Description, Due_date, Modified_date FROM task WHERE Due_date LIKE Date('now','+1 day')")
    result = c.fetchall()
    c.close()
    return render_template("task_list.html", rows=result)

#----------------------------------------------------------------------
#Function for creating new task
@app.route("/new", methods=['GET','POST'])
def new():
    """
    Add a new TODO item
    """
    if request.method == "POST":
        #get parameters form form
        Description = request.form["Description"]
        duedate = request.form["Due_date"]

        #conncetion to the database
        conn = sqlite3.connect("todo.db")
        c = conn.cursor()

        #create table if it is not EXISTS
        conn.execute("CREATE TABLE IF NOT EXISTS task (Task_id INTEGER PRIMARY KEY, Description char(100), Created_date  DATE, Due_date DATE, Modified_date DATE, deleted bool, status bool)")

        #insert values into databse
        c.execute("INSERT INTO task (Description, Created_date, Due_date, Modified_date, deleted, status) VALUES (?,Date('now') ,?, Date('now'), 0, 1)", (Description,duedate))
        new_id = c.lastrowid
        conn.commit()
        c.close()

        return redirect(url_for ('todo_list'))
    else:
        #return template which shows all tasks
        return render_template("new_task.html")

#main function
if __name__ == "__main__":
    app.secret_key = os.urandom(12)
    app.run(debug=True,host='0.0.0.0', port=4000)
