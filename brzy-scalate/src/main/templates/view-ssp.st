<% val $attributeName$ = attribute[$fullClassName$]("$attributeName$") %>
<% attributes("layout") = "/layouts/default.ssp" %>
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
