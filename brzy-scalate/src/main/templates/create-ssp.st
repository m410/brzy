<% attributes("layout") = "/layouts/default.ssp" %>
<% import org.brzy.validator.Violation %>
<% import $fullClassName$ %>
<% val $attributeName$ = attribute[$className$]("$attributeName$")  %>
<% val violations = attributeOrElse[Set[Violation[$className$]]]("violations", Set.empty[Violation[$className$]])  %>

<h2>Create Person</h2>
<form action="\${action("/$attributeName$s/save")}" method="post" accept-charset="utf-8">
  <fieldset id="create_$attributeName$">
    <legend>Create $attributeName$</legend>
    #if(!violations.isEmpty)
     <div class="error">
       <h5>Please fix the following errors.</h5>
         <ul>
         #for(error <- violations) <li>\${error.getPropertyPath}: \${error.getMessage}</li> #end
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