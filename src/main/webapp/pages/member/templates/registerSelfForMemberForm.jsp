<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form id="registerUserWithMemberForm" action="${pageContext.request.contextPath}/RegisterUserForMember" method="GET" style="display:none">
	<fieldset id="registerUserWithMemberFieldSet" style="width:400px">
		<legend id="registerUserWithMemberLegend"></legend>  
	    <table id="registerUserWithMemberFields">
	    	<tr>
				<td><label for="roles">Role: </label></td>
				<td>
					<select id="roles">
						<option value=""></option>
						<c:forEach items="${applicationScope.roleKeyToRoles}" var="role">
							<c:if test="${role.key != 'conductor'}">
								 <option value="${role.key}">${role.value}</option>
							</c:if>
					    </c:forEach>
					</select>
				</td>
				<td>
					<input type="submit" id="registerWithMember" value="Register">
				</td>
			</tr>
	    </table>
	    <input type='hidden' id='memberId' name='memberId' style='display:none'/>
	    <input type='hidden' id='role' name='role' style='display:none'/>
	</fieldset>
</form>