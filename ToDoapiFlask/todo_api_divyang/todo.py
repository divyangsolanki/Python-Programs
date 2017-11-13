#import libraries
import sqlite3
from bottle import route, run, debug
from bottle import redirect, request, template

#----------------------------------------------------------------------
#Function for updating the fields of todo list like Description and Due_date.
@route('/update/:no', method='GET')
def edit_item(no):
    """
    update a TODO item
    """

    if request.GET.get('save','').strip():
        #get the parameters from html form
        Description = request.GET.get('Description','').strip()
        status = request.GET.get('status','').strip()
        Due_date = request.GET.get('Due_date','').strip()

        #if status of task is open then it will set the value=1
        if status == 'open':
            status = 1
        else:
            status = 0

        #conncetion to the database
        conn = sqlite3.connect('todo.db')
        c = conn.cursor()

        #update query to update fields of task
        c.execute("UPDATE task SET Description = ?, Due_date = ?, Modified_date = Date('now'), status = ? WHERE Task_id LIKE ?", (Description, Due_date, status, no))
        conn.commit()
        redirect("/")
    else:
        #conncetion to the database
        conn = sqlite3.connect('todo.db')
        c = conn.cursor()

        #select query to get the Description of particular task
        c.execute("SELECT Description FROM task WHERE Task_id LIKE ?", (no))
        cur_data = c.fetchone()

        return template('update_task', old=cur_data, no=no)

#----------------------------------------------------------------------
#Function for deleting the particular task
@route("/delete/:no")
def deleted(no):
    """
    Delete items
    """
    #conncetion to the database
    conn = sqlite3.connect('todo.db')
    c = conn.cursor()

    #delete task based on Task_id
    c.execute("DELETE FROM task WHERE Task_id LIKE ? ",(no))
    conn.commit()
    redirect("/")

#----------------------------------------------------------------------
#Function to get list of task which are closed or done
@route("/done")
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

    output = template("show_done", rows=result)
    return output

#----------------------------------------------------------------------
#Function to get list of task which are pending
@route("/")
@route("/todo")
def todo_list():
    """
    Show the main page which is the current TODO list
    """
    #conncetion to the database
    conn = sqlite3.connect("todo.db")
    c = conn.cursor()

    #select query to get all values of task
    c.execute("SELECT Task_id, Description, Due_date, Modified_date FROM task WHERE status LIKE '1'")
    result = c.fetchall()
    c.close()

    output = template("task_list", rows=result)
    return output

#----------------------------------------------------------------------
#Function for getting list of tomorrow's taks
@route("/tomorrowlist", method="GET")
def tomorrowlist():
    #conncetion to the database
    conn = sqlite3.connect("todo.db")
    c = conn.cursor()

    #select query to get all values of list of tomorrow
    c.execute("SELECT Task_id, Description, Due_date, Modified_date FROM task WHERE Due_date LIKE Date('now','+1 day')")
    result = c.fetchall()
    c.close()

    output = template("task_list", rows=result)
    return output

#----------------------------------------------------------------------
#Function for creating new task
@route("/new", method="GET")
def new_item():
    """
    Add a new TODO item
    """
    if request.GET.get("save", "").strip():
        #get parameters form form
        Description = request.GET.get("Description", "").strip()
        duedate = request.GET.get("Due_date", "").strip()

        #conncetion to the database
        conn = sqlite3.connect("todo.db")
        c = conn.cursor()

        #create table if it is not EXISTS
        con.execute("CREATE TABLE IF NOT EXISTS task (Task_id INTEGER PRIMARY KEY, Description char(100), Created_date  DATE, Due_date DATE, Modified_date DATE, deleted bool, status bool)")

        #insert values into databse
        c.execute("INSERT INTO task (Description, Created_date, Due_date, Modified_date, deleted, status) VALUES (?,Date('now') ,?, Date('now'), 0, 1)", (Description,duedate))
        new_id = c.lastrowid
        conn.commit()
        c.close()

        redirect("/")
    else:
        #return template which shows all tasks
        return template("new_task.tpl")

#main function
if __name__ == "__main__":
    debug(True)
    run()
