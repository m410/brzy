<% val $attributeName$ = attribute[$fullClassName$]("$attributeName$") %>
<% val validation = attribute[org.brzy.webapp.validator[_]]("validation") %>
<% attributes("layout") = "/layouts/default.ssp" %>
<form action="\${action("persons/"+person.id+"/update")}" method="post" accept-charset="utf-8">
<fieldset id="edit_$attributeName$">
  <legend>Edit $attributeName$</legend>
   <%if(validation != null) {%>
   <div>
     <ul>
     <%for(error <- validation.violations){%><li><%=error%></li><%} %>
     </ul>
   </div>
   <%}%>
  <div>
    <label for="first_name">First Name</label>
    <input type="text" name="firstName" value="\${$attributeName$.firstName}" id="first_name">
  </div>
  <div>
    <label for="last_name">Last Name</label>
    <input type="text" name="lastName" value="\${$attributeName$.lastName}" id="last_name">
  </div>
  <div>
    <input type="submit" value="Save And Continue">
  </div>
</fieldset>
 </form>
