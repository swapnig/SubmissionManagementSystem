<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="selectExistingMembers" style="display:none">
	<fieldset style="width:400px">
		<legend><strong>Select parent members</strong></legend>
		<!-- Dynamically generate id <category-name>List for drop down, 
		     listing all available members for current category (populated using ajax)-->
		<table>
			<c:forEach items="${applicationScope.categoryToPropertyKey}" var="category">
		        <tr id="${category.key}" style="display:none; margin-top:15px">
		        	<td><label for="${category.key}List" style='text-transform: capitalize'>${category.key}: </label></td>
		        	<td><select id="${category.key}List" ></select></td>
		        </tr>
	    	</c:forEach>
	    </table>
	</fieldset>
</div>