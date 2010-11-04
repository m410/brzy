<% attributes("layout") = "/layouts/default.ssp" %>
<%@ import val $attributeName$:$fullClassName$ %>
<dl>
  <dt>id</dt>
  <dd>\${$attributeName$.id}</dd>
  <dt>first name</dt>
  <dd>\${$attributeName$.firstName}</dd>
  <dt>last name</dt>
  <dd>\${$attributeName$.lastName}</dd>
  <dt></dt>
  <dd><a href="\${res("$attributeName$s/"+$attributeName$.id+"/edit")}">Edit</a></dd>
</dl>
