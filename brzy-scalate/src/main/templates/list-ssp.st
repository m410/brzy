<% escapeMarkup = false %>
<% attributes("layout") = "/layouts/default.ssp" %>
<% attributes("title") = "$attributeName$" %>
<% import com.offer2lead.core.company.Company  %>
<% val $attributeName$sList$ = attribute[List[$fullClassName$]]("$attributeName$sList$") %>
#if(hasFlash()) <div class="success">\${flash()}</div> #end
<table>
  <caption>$attributeName$ List</caption>
  <thead>
    <tr>
    $fields:{ field |     <th>$field$</th>$\n$}$
    </tr>
  </thead>
  <tfoot>
    <tr>
      <td colspan="$fieldsLength$">Paging: <a href="\${link("/$attributeName$s/create")}">Create</a></td>
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
