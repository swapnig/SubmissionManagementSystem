/**
 * Handle all the events on the view member details page.
 *
 * @author Swapnil Gupta
 * @date Jun 4, 2015
 */

/**
 * Maintain a top down hierarchy - from the root category to the category being created.
 * Containing all the parent categories as well as the category being created
 */
window.parentList = [];


/**
 * Attach all the event handlers for the current view, using jquery, when the document is ready to be loaded.
 */
$(document).ready(function() {
	
	// Handle change event for 'category selection for creation' select box (selectCategory.jsp)
	$('#categories').change(function() {	
		var category = $('#categories').val();
				
		cleanupOnCategoryReselect(category);
		buildCategoryParentList(category);
		
		var rootCategory = parentList[0];
		getMembersForCategory(rootCategory, null);
	});
	
	/* 
	 * Generic function to track change event on all the category drop downs
	 * Based on the (parent)member selected currently decide what to do for child category
	 */  
	$("select[id$='List']").change(function() {
		
		// Hide all the forms and result div
		$('#registerUserWithMemberForm').hide();
		$('#registerUserWithMemberForm')[0].reset();
		$('#result').empty();
		
		var categoryListId = $(this).attr('id');
		var parentMemberId = $('#' + categoryListId + ' option:selected').val();
		var parentMemberName = $('#' + categoryListId + ' option:selected').text();
		
		// If default value for the drop down is selected, ignore it
		if (parentMemberId != 'default') {
			var category = categoryListId.split("List")[0];
			var childCategoryIndex = parentList.indexOf(category) + 1;
			var childCategory = parentList[childCategoryIndex];
			
			/*
			 * If category for member to view details changed, fetch member attributes
			 * Here parentMember is actually current is actually the current member
			 */
			if ($.inArray(category, parentList) == parentList.length - 1) {
				currentMemberId = parentMemberId;
				currentMemberName = parentMemberName;
				getMembersAttributes(currentMemberId, currentMemberName);
				
			// Else populate child category dropdown for child members
			} else {
				getMembersForCategory(childCategory, parentMemberId);
				hideChildSelectDropdownLists(childCategoryIndex);
			}
		}
	});
	
	// Attach a submit handler to the add new member form view (memberForm.jsp)
	$("#memberForm").submit(function(event) {
		// Stop form from submitting normally
        event.preventDefault();
        
        postFormUsingAjax('UpdateMember', $("#memberForm").serialize(), 'result');
	});
	
	/* 
	 * Attach the change event for the role select box, which tracks when user selects the 
	 * role they want to register for.
	 * It also update this selected role in hidden variable
	 */
	$("#roles").change(function() {
		var roleHtmlId = $(this).attr('id');
		var roleId = $('#' + roleHtmlId + ' option:selected').val();
		$("#role").val(roleId);
	});
	
	// Attach a submit handler to the register for new member form view (registerOtherUserForMemberForm.jsp)
    $("#registerUserWithMemberForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        postFormUsingAjax('AddRoleToMember', $("#registerUserWithMemberForm").serialize(), 'result');
    });
});


/**
 * Cleanup, Member(Category) to be created reselected
 */ 
function cleanupOnCategoryReselect(category, parentMember) {
	//Reset parents list
	parentList = [];
	
	// Hide select boxes for all the categories and containing members
	$.each(categoryToParentMap, function(category, parentCategory) {
		$("#" + category).hide();
	});
	
	// Empty all the fields from the old form and show select existing members view (selectExistingMembers.jsp)
	$('#registerUserWithMemberForm').hide();
	$('#memberForm').hide();
	$('#result').hide();
	$('#memberFields').empty();
}


/**
 * Build Top down hierarchy from root category to category of member to be created 
 * @param category String containing category for which member is to be created
 */
function buildCategoryParentList (category) {
	var parentCategory = categoryToParentMap[category];
	
	/*
	 * Push hierarchical parents of selected category, including itself on stack
	 * (bottom up from category of member to be created)
	 */
	parentList.push(category);
	while(!jQuery.isEmptyObject(parentCategory)) {
		parentList.push(parentCategory);
		parentCategory = categoryToParentMap[parentCategory];
	}
	
	// Build Top down hierarchy from root hierarchy to category of member to be created
	parentList.reverse();
}


/**
 * Hide all the child select boxes if parent select box is changed
 * @param childCategoryIndex index of the category select box that was changed
 */
function hideChildSelectDropdownLists(childCategoryIndex){
	var splicedList = parentList.slice(childCategoryIndex + 1, parentList.length);
	$.each(splicedList, function(index, category) {
		$("#" + splicedList[index]).hide();
	});
	$('#memberForm').hide();
}


/**
 * Get members for given category and parent_member_id using ajax
 * @param category String category for which member is to be created
 * @param parentMemberId Long parent member id for which child member is to be created
 */
function getMembersForCategory(category, parentMemberId) {
	var data = {
		category : category,
		parentMemberId : parentMemberId
	}
	
	var postRequest = $.post('ReadMembers', data);
	
	// Populate the server response in the given response element
	postRequest.done( function(responseData) {
		showElementIfHidden("selectExistingMembers");
	    $('#' + category + 'List').html(responseData);
	    $('#' + category).show();
	});
	
	// Notify user that a request to the server failed
	postRequest.error( function(responseData) {
		$('#result').empty().append("<font size='4' color='red'>" +
		"There was issue processing the request. Try again or contact administrator</font>").show();
	});
}


/**
 * Get details for the member selected by the user to register
 * @param category String category for which member is to be created
 * @param parentMemberId Long parent member id for which child member is to be created
 * @param parentMemberName Sting parent member name for which child member is to be created
 */
function getMembersAttributes(memberId, memberName) {
	
	var data = {
		memberId : memberId
	}
	
	var postRequest = $.post('ReadMemberDetails', data);
	
	postRequest.done( function(responseData) {
	    $('#memberFields').html(responseData);
	    // $('#editMemberAttributes').hide();
	    $('#memberLegend').html("<strong>" + memberName + " details </strong>");
	    attachEventHandlers(memberId);
		$('#memberForm').show();
	});
	
	// Notify user that a request to the server failed
	postRequest.error( function(responseData) {
		$('#result').empty().append("<font size='4' color='red'>" +
		"There was issue processing the request. Try again or contact administrator</font>").show();
	});
}


/**
 * Explicitly attach event handler for edit button, since it is dynamically added through ajax call
 * @param memberId Long member id which needs to be edited
 */
function attachEventHandlers(memberId) {
	
	/*
	 * Attach event handlers for edit member, cannot be added at document ready,
	 * as this element are getting dynamically added from the server response
	 */
	$('#editMemberAttributes').click(function(){
		/*
		 * If all the text fields in member are disabled then, 
		 * 1. Enable all text fields
		 * 2. Show Save member button
		 * 3. Show form to register user with member
		 */
		if( $('#memberForm :text').prop('disabled') ) {
			 $('#memberId').prop("value", memberId);
		     $('#memberForm :text').prop('disabled', false);
		     $('#memberForm textarea').attr('disabled', false);
		     $('#saveMemberAttributes').show();
		     $('#toggleMemberActivation').show();
		     $('#registerUserWithMemberForm').show();
	    } else{
	         $('#memberForm :text').prop('disabled', true);
	         $('#memberForm textarea').attr('disabled', true);
	         $('#saveMemberAttributes').hide();
	         $('#toggleMemberActivation').hide();
	         $('#registerUserWithMemberForm').hide();
	         $('#registerUserWithMemberForm')[0].reset();
	    }
    });
	
	/*
	 * Attach event handlers for edit member, cannot be added at document ready,
	 * as this element are getting dynamically added from the server response
	 */
	$('#toggleMemberActivation').click(function(){
		if (confirm('Toggle Member Activation Status')) {
			toggleMemberActivation($('#memberId').val());
		}
    });
}

/**
 * Toggle state of member activation by making an ajax request to server
 * @param memberId memberId Long member id whoose activation state needs to be toggled
 */
function toggleMemberActivation(memberId) {
	var data = {
			memberId : memberId
	}
	
	var postRequest = $.post('ToggleMemberActivation', data);
	
	// Populate the server response in the given response element
	postRequest.done( function(responseData) {
	    $('#result').html(responseData).show();
	    
	    // Toggle button state
	    if ($('#toggleMemberActivation').val() == "Activate") {
			$('#toggleMemberActivation').prop("value", "Inactivate");
		} else {
			$('#toggleMemberActivation').prop("value", "Activate");
		}
	});
	
	// Notify user that a request to the server failed
	postRequest.error( function(responseData) {
		$('#result').empty().append("<font size='4' color='red'>" +
		"There was issue processing the request. Try again or contact administrator</font>").show();
	});
}

/**
 * Show an html element if it is hidden
 * @param elementId element which needs to be shown if hidden
 */
function showElementIfHidden (elementId) {
	if($('#' + elementId).is(':hidden')){
		$('#' + elementId).show();
	}
}