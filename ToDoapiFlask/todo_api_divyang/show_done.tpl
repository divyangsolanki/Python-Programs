%#template to generate a HTML table from a list of tuples 
<p><h3>Already Done TODO Items:</h3></p>
<p><a href="/">Home</a></p>
<table border="1">
  <tr>
      <th>Task_id</th>
      <th>Description</th>
      <th>Update</th>
      <th>Delete</th>
    </tr>
%for row in rows:
  %id, title = row
  <tr>
  %for col in row:
    <td>{{col}}</td>
  %end
  <td><a href="/update/{{id}}"> Update</a></td>
  <td><a href="/delete/{{id}}"> Delete</a></td>
  </tr>
%end
</table>
<p>Create ::  <a href="/new">New</a> item</p>
