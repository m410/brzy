<% attributes("layout") = "/layouts/default.ssp" %>
<% import org.brzy.validator.Violation %>
<% import $fullClassName$ %>
<% val $attributeName$ = attribute[$className$]("$attributeName$")  %>
<% val violations = attributeOrElse[Set[Violation[$shortClassName$]]]("violations", Set.empty[Violation[$shortClassName$]])  %>
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
     $fields:{ field |
     <div>
      <label for="$field$">$field$</label>
      <input type="text" name="$field$" value="\${$attributeName$.$field$}" id="$field$">
     </div>
     }$
    <div>
      <input type="submit" value="Save And Continue">
    </div>
  </fieldset>
</form>