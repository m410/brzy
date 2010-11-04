<% attributes("layout") = "/layouts/default.ssp" %>
<%@ import val $attributeName$s: List[$fullClassName$]  %>
#if(hasFlash()) <div class="success">\${flash()}</div> #end
<table>
  <caption>Person List</caption>
  <thead>
    <tr>
      <th>ID</th>
      <th>First Name</th>
      <th>Last Name</th>
      <th>&nbsp;</th>
    </tr>
  </thead>
  <tfoot>
    <tr>
      <td colspan="4">Paging: <a href="\${link("/$attributeName$s/create")}">Create</a></td>
    </tr>
  </tfoot>
  <tbody>
    #for($attributeName$ <- $attributeName$s)
    <tr>
      <td><a href="\${link("/$attributeName$s/" + $attributeName$.id)}">\${$attributeName$.id}</a></td>
      <td>\${$attributeName$.firstName}</td>
      <td>\${$attributeName$.lastName}</td>
      <td><a href="\${link("/$attributeName$s/"+$attributeName$.id)}">i</a>
        <a href="\${link("/$attributeName$s/"+$attributeName$.id+"/delete")}" onClick="return confirm('Are your sure?')">x</a>
      </td>
    </tr>
    #end
  </tbody>
</table>
