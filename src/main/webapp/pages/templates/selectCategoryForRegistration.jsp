<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="selectCategoryForRegistration">
	<fieldset class="fieldSet">
		<legend><strong>Select a Category</strong></legend><br/>
		<table>
			<tr>
				<td><label for="categories">Category: </label></td>
				<td>
					<select id="categories">
						<option value="default"></option>
					    <c:forEach items="${applicationScope.registerableCategories}" var="category">
					        <option value="${category}">${category}</option>
					    </c:forEach>
					</select>
				</td>
			</tr>
		</table>
	</fieldset>
</div>