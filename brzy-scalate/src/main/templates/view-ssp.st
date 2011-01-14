<% escapeMarkup = false %>
<% attributes("layout") = "/layouts/default.ssp" %>
<% attributes("title") = "$attributeName$" %>
<% import $fullClassName$ %>
<% val $attributeName$sList$ = attribute[List[$fullClassName$]]("$attributeName$sList$") %>
#if(hasFlash()) <div class="success">\${flash()}</div> #end
<dl>
  $fields:{ field |  <dt>$field$</dt> <dd>\${$attributeName$.$field$}</dd>$\n$}$
  <dt>&nbsp;</dt> <dd><a href="\${res("$attributeName$s/"+$attributeName$.id+"/edit")}">Edit</a></dd>
</dl>
