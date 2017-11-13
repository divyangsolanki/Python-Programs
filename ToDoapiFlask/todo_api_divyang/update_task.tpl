%#template for editing a task

<p>Update the task with ID = {{no}}</p>
<form action="/update/{{no}}" method="get">
Description :: <input type="text" name="Description" value="{{old[0]}}" size="100" maxlength="100"><br/><br/>
Due_date :: <input type="date" name="Due_date" value="{{old[0]}}"><br/><br/>
Status :: <select name="status">
<option>open</option>
<option>closed</option>
</select>
<br/><br/>
<input type="submit" name="save" value="save">
</form>
