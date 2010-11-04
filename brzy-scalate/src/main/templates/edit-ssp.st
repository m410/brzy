<% attributes("layout") = "/layouts/default.ssp" %>
<%@ import val $attributeName$: $fullClassName$ %>
<%@ import org.brzy.validator.Violation %>
<% val violations = attributeOrElse[Set[Violation[Person]]]("violations", Set.empty[Violation[Person]])  %>
<h2>Edit Person</h2>
<form action="\${action("/$attributeName$s/"+$attributeName$.id+"/update")}" method="post" accept-charset="utf-8">
  <fieldset id="create_$attributeName$">
    <legend>Edit $attributeName$</legend>
    #if(!violations.isEmpty)
     <div class="error">
       <h5>Please fix the following errors.</h5>
         <ul>
         #for(error <- violations)<li>\${error.getPropertyPath}: \${error.getMessage}</li> #end
         </ul>
     </div>
     #end
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
