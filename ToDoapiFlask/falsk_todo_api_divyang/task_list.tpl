%#template to generate a HTML table from a list of tuples
<p><h3>Your TODO Items:</h3></p>
<table border="1">
  <tr>
      <th>Task_id</th>
      <th>Description</th>
      <th>Due_date</th>
      <th>Modified_date</th>
      <th>Update</th>
    </tr>
%for row in rows:
  %id, title,Due_date,Modified_date = row
  <tr>
  %for col in row:
    <td>{{col}}</td>
  %end
  <td><a href="/update/{{id}}"> Update </a></td>
  </tr>
%end
</table>
<p>Tomorrow's task :: <a href="/tomorrowlist">Tomorrowlist</a> item</p>
<p>Create New task :: <a href="/new">New</a> item</p>
<p>Show Done tasks ::  <a href="/done">Done Items</a></p>
