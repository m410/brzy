<% attributes("layout") = "/layouts/default.ssp" %>
<%@ import val $attributeName$sList: List[$fullClassName$]  %>
#if(hasFlash()) <div class="success">\${flash()}</div> #end
<table>
  <caption>Person List</caption>
  <thead>
    <tr>
    $fields:{ field |     <th>$field$</th>$\n$}$
    </tr>
  </thead>
  <tfoot>
    <tr>
      <td colspan="$fields.length$">Paging: <a href="\${link("/$attributeName$s/create")}">Create</a></td>
    </tr>
  </tfoot>
  <tbody>
    #for($attributeName$ <- $attributeName$sList)
    <tr>
      <td><a href="\${link("/$attributeName$s/" + $attributeName$.id)}">\${$attributeName$.id}</a></td>
      $fields:{ field |     <td>\${$attributeName$.$field$}</td>$\n$}$
    </tr>
    #end
  </tbody>
</table>
