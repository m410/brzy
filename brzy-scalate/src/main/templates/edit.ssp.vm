<% import org.brzy.view.ViewFunctions._  %>
<% import org.brzy.d3.Person %>
<% import org.brzy.mvc.validator.Validation %>
<% implicit val req = request %>
<% val person = request.getAttribute("person").asInstanceOf[Person] %>
<% val validation = request.getAttribute("validation").asInstanceOf[Validation[_]] %>
<% attributes("layout") = "/layouts/default.ssp" %>
<form action="${action("persons/"+person.id+"/update")}" method="post" accept-charset="utf-8">
	<fieldset id="create_person">
		<legend>Edit person</legend>
        <%if(validation != null) {%>
        <div>
            <ul>
            <%for(error <- validation.violations){%><li><%=error%></li><%} %>
            </ul>
        </div>
        <%}%>
		<div>
			<label for="first_name">First Name</label>
			<input type="text" name="firstName" value="${person.firstName}" id="first_name">
		</div>
		<div>
			<label for="last_name">Last Name</label>
			<input type="text" name="lastName" value="${person.lastName}" id="last_name">
		</div>
		<div>
			<input type="submit" value="Save And Continue">
		</div>
	</fieldset>
</form>