import os
from bottle import route, run, template
from bottle import request, response
from bottle import get, post, put, delete
import sqlite3


index_html = '''Table::  {{ task }} </strong>.'''
template1 = '''<p>Add a new task to the ToDo list:</p>
<form action="/create" method="GET">
Description<input name="Description" type="text" size="100" maxlength="100" />
<input name="due_date" type="date" />
<input type="submit" name="save" value="save">
</form>'''


# @get('/create')
# def index():
#     conn = sqlite3.connect('todo.db')
#     c = conn.cursor()
#     conn.execute("CREATE TABLE IF NOT EXISTS task (Task_id INTEGER PRIMARY KEY AUTOINCREMENT, Description char(100) NOT NULL, due_date DATETIME NOT NULL)")
#
#     return '''<form method="POST" action="/create">
#                 Description<input name="Description" type="text" />
#                 <input name="due_date" type="date" />
#                 <input type="submit" />
#               </form> '''


@get('/create')
def create_task():
    if request.GET.get("save", "").strip():
        Description = request.GET.get('Description')
        due_date = request.GET.get('due_date')
        Task_id = request.GET.get('Task_id')
        conn = sqlite3.connect('todo.db')
        c = conn.cursor()
        conn.execute("CREATE TABLE IF NOT EXISTS task (Task_id INTEGER PRIMARY KEY AUTOINCREMENT, Description char(100) NOT NULL, due_date DATETIME NOT NULL)")
        conn.execute("INSERT INTO task (Description,due_date) VALUES (?,?)",(Description,due_date))
        conn.commit()
        json ={
            'Task_id':Task_id,'Description':Description, 'due_date':due_date
        }
    else:
        return template(template1)

@get('/list')
def task_list():
    
if __name__ == '__main__':
    port = int(os.environ.get('PORT', 8080))
    run(host='0.0.0.0', port=port, debug=True)
