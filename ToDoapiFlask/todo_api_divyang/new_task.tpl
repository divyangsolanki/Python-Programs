%template for generating new task
<p>Add a new task to the ToDo list:</p>
<form action="/new" method="GET">
Description: <input type="text" size="100" maxlength="100" name="Description" required><br/><br/>
Due_date: <input type="date" name="Due_date" required><br/><br/>
<input type="submit" name="save" value="save">
</form>
