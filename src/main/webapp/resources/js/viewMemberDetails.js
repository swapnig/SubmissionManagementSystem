//Track all the parent categories of the category use to create the member
window.parentList = [];

$(document).ready(function() {
	
	// Handle change event selectCategory drop down (selectCategory.jsp)
	$('#categories').change(function() {	
		var category = $('#categories').val();
				
		cleanupOnCategoryReselect(category);
		buildCategoryParentList(category);
		
		var rootCategory = parentList[0];
		getMembersForCategory(rootCategory, null);
	});
	
	// Generic function to track change event on all the category drop downs
	// Based on the (parent)member selected currently decide what to do for child category 
	$("select[id$='List']").change(function() {
		$('#registerUserWithMemberForm').hide();
		$('#registerUserWithMemberForm')[0].reset();
		$('#result').empty();
		
		var categoryListId = $(this).attr('id');
		var parentMemberId = $('#' + categoryListId + ' option:selected').val();
		var parentMemberName = $('#' + categoryListId + ' option:selected').text();
		
		if(!jQuery.isEmptyObject(parentMemberId)) {
			var category = categoryListId.split("List")[0];
			var childCategoryIndex = parentList.indexOf(category) + 1;
			var childCategory = parentList[childCategoryIndex];
			
			// If Parent category for member to view details changed, fetch member attributes
			if ($.inArray(category, parentList) == parentList.length - 1) {
				getMembersAttributes(parentMemberId, parentMemberName);
				
			// Else populate child category dropdown for child members
			} else {
				getMembersForCategory(childCategory, parentMemberId);
				hideChildSelectDropdownLists(childCategoryIndex);
			}
		}
	});
	
	//Attach a submit handler to the view member details form
	$("#memberForm").submit(function(event) {
		// Stop form from submitting normally
        event.preventDefault();
        postFormUsingAjax('UpdateMember', $("#memberForm").serialize(), 'result');
	});
	
	// On change of roles through select drop-down, update the selected role in hidden variable
	$("#roles").change(function() {
		var roleHtmlId = $(this).attr('id');
		var roleId = $('#' + roleHtmlId + ' option:selected').val();
		$("#role").val(roleId);
	});
	
	// Attach a submit handler to the form
    $("#registerUserWithMemberForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        postFormUsingAjax('AddRoleToMember', $("#registerUserWithMemberForm").serialize(), 'result');
    });
});


// Cleanup, Member to be created reselected
function cleanupOnCategoryReselect(category, parentMember) {
	//Reset parents list
	parentList = [];
	
	// Hide all the category drop downs
	$.each(categoryToParentMap, function(category, parentCategory) {
		$("#" + category).hide();
	});
	
	// Empty the old form to create new member
	$('#selectExistingMembers').show();
	$('#memberForm').hide();
	$('#memberFields').empty();
	$('#registerUserWithMemberForm').hide();
	$('#result').hide();
}


//Build Top down hierarchy from root hierarchy to category of member to be created
function buildCategoryParentList (category) {
	var parentCategory = categoryToParentMap[category];
	
	// Push hierarchical parents of selected category, including itself on stack 
	// (bottom up from category of member to be created)
	parentList.push(category);
	while(!jQuery.isEmptyObject(parentCategory)) {
		parentList.push(parentCategory);
		parentCategory = categoryToParentMap[parentCategory];
	}
	
	// Build Top down hierarchy from root hierarchy to category of member to be created
	parentList.reverse();
}


//Hide all the child select drop-downs if parent drop-down is changed
function hideChildSelectDropdownLists(childCategoryIndex){
	var splicedList = parentList.slice(childCategoryIndex + 1, parentList.length);
	$.each(splicedList, function(index, category) {
		$("#" + splicedList[index]).hide();
	});
	$('#memberForm').hide();
}


// Get members for given category and parent_member_id using ajax
function getMembersForCategory(category, parentMemberId) {
	var data = {
		category : category,
		parentMemberId : parentMemberId
	}
	postDataUsingAjax('ReadMembers', data, category + 'List');
	$('#' + category).show();
}


//Get form for new member of given category and parent id using ajax
function getMembersAttributes(memberId, memberName) {
	var data = {
		memberId : memberId
	}
	console.log(data);
	var posting = $.post('ReadMemberDetails', data);
	
	posting.done( function(responseData) {
	    $('#memberFields').html(responseData);
	    attachEditButtonEventHandlers(memberId);
	});
	
	$('#memberLegend').html("<strong>" + memberName + " details </strong>");
	$('#editMemberAttributes').hide();
	$('#memberForm').show();
}

// Explicitly attach event handler for edit button, since it is dynamically added through ajax call
function attachEditButtonEventHandlers(memberId) {
	
	$('#editMemberAttributes').click(function(){
		//event.preventDefault();
		
		// If all the text fields in member are disabled then,
		// Enable all text fields, show Save member button
		// Show form to register user with member
		if( $('#memberForm :text').prop('disabled') ) {
		     $('#memberForm :text').prop('disabled', false);
		     $('#saveMemberAttributes').show();
		     $('#registerUserWithMemberForm').show();
		     $('#memberId').prop("value", memberId);
	    } else{
	         $('#memberForm :text').prop('disabled', true);
	         $('#saveMemberAttributes').hide();
	         $('#registerUserWithMemberForm')[0].reset();
	         $('#registerUserWithMemberForm').hide();
	    }
    }); 
}