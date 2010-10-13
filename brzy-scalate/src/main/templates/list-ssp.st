<% val $attributeName$ = attribute[List[$fullClassName$]]("$attributeName$") %>
<% attributes("layout") = "/layouts/default.ssp" %>
<table>
<caption>$shortClassName$ List</caption>
<thead>
  <tr>
    <th>ID</th>
    <th>First Name</th>
    <th>Last Name</th>
  </tr>
</thead>
<tfoot>
  <tr>
    <td colspan="3">Paging: <a href="\${res("$shortClassName$s/create")}">Create</a></td>
  </tr>
</tfoot>
<tbody>
  <% for(it <- $attributeName$) { %>
  <tr>
    <td><a href="\${res("its/" + it.id)}">\${it.id}</a></td>
    <td>\${it.firstName}</td>
    <td>\${it.lastName}</td>
  </tr>
  <%}%>
</tbody>
</table>
