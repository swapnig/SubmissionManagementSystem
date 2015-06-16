<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form id="registerUserWithMemberForm" action="${pageContext.request.contextPath}/AddRoleToMember" method="POST" style="display:none">
	<fieldset id="registerUserWithMemberFieldSet" style="width:400px">
		<legend id="registerUserWithMemberLegend"><strong>Add conductor for ${memberName}</strong></legend>  
	    <table id="registerUserWithMemberFields">
	    	<tr>
				<td><label for="roles">Role: </label></td>
				<td>
					<select id="roles">
						<option value=""></option>
						<c:forEach items="${applicationScope.roleKeyToRoles}" var="role">
							<option value="${role.key}">${role.value}</option>
					    </c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td><label for="userEmailId">User email id:</label></td>
				<td><input name="userEmailId" type="text" value=""/></td>
			</tr>
			<tr>
				<td>
					<input type="submit" id="registerWithMember" value="Make Conductor">
				</td>
			</tr>
	    </table>
	    <input type='hidden' id='memberId' name='memberId' value='' style='display:none'/>
	    <input type='hidden' id='role' name='role' style='display:none'/>
	</fieldset>
</form>