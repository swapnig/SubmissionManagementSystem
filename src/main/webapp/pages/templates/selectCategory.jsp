<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="selectCategory">
	<fieldset class="fieldSet">
		<legend><strong>Select category</strong></legend>
		<table>
			<tr>
				<td><label for="categories">Category: </label></td>
				<td>
					<select id="categories">
						<!-- Disallow creating new root category member -->
						<option value="default"></option>
					    <c:forEach items="${applicationScope.categoryToPropertyKey}" var="category">
					    	<c:if test="${category.value != 'category'}">
					    		<option value="${category.key}">${category.key}</option>
					    	</c:if>
					    </c:forEach>
					</select>
				</td>
			</tr>
		</table>
	</fieldset>
</div>