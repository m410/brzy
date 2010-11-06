<% attributes("layout") = "/layouts/default.ssp" %>
<%@ import val $attributeName$:$fullClassName$ %>
<dl>
  $fields:{ field |  <dt>$field$</dt> <dd>\${$attributeName$.$field$}</dd>$\n$}$
  <dt></dt>
  <dd><a href="\${res("$attributeName$s/"+$attributeName$.id+"/edit")}">Edit</a></dd>
</dl>
