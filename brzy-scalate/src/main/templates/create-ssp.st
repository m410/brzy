<% escapeMarkup = false %>
<% attributes("layout") = "/layouts/default.ssp" %>
<% attributes("title") = "$shortClassName$" %>
<% import javax.validation.ConstraintViolation %>
<% import $fullClassName$ %>
<% val $attributeName$ = attribute[$shortClassName$]("$attributeName$")  %>
<% val violations = attributeOrElse[Set[ConstraintViolation[$shortClassName$]]]("violations", Set.empty[ConstraintViolation[$shortClassName$]])  %>
#if(hasFlash()) <div class="success">\${flash()}</div> #end
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