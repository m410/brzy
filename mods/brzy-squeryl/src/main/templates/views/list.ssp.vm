<% import org.brzy.view.ViewFunctions._  %>
<% import org.brzy.d3.Person %>
<% implicit val req = request %>
<% val persons = request.getAttribute("personsList").asInstanceOf[List[Person]] %>
<% attributes("layout") = "/layouts/default.ssp" %>
<table>
	<caption>Person List</caption>
	<thead>
		<tr>
			<th>ID</th>
			<th>First Name</th>
			<th>Last Name</th>
		</tr>
	</thead>
	<tfoot>
		<tr>
			<td colspan="3">Paging: <a href="${res("persons/create")}">Create</a></td>
		</tr>
	</tfoot>
	<tbody>
		<% for(person <- persons) { %>
			<tr>
				<td><a href="${res("persons/" + person.id)}">${person.id}</a></td>
				<td>${person.firstName}</td>
				<td>${person.lastName}</td>
			</tr>
			<%}%>
	</tbody>
</table>