/**
 * Handle all the events on the add new member view.
 *
 * @author Swapnil Gupta
 * @date Jun 2, 2015
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
		buildParentCategoriesListForCategory(category);
		
		var rootCategory = parentList[0];
		getMembersForCategory(rootCategory, null);
	});
	
	
	/* 
	 * Generic function to track change event on all the category drop downs
	 * Based on the (parent)member selected currently decide what to do for child category
	 */ 
	$("select[id$='List']").change(function() {
		
		// Empty the results div
		$('#result').empty();
		
		// Identify the exact select box, on which this event was fired
		var categoryListId = $(this).attr('id');
		var parentMemberId = $('#' + categoryListId + ' option:selected').val();
		var parentMemberName = $('#' + categoryListId + ' option:selected').text();
		
		// If default value for the drop down is selected, ignore it
		if (parentMemberId != 'default') {
			var category = categoryListId.split("List")[0];
			var childCategoryIndex = parentList.indexOf(category) + 1;
			var childCategoryName = parentList[childCategoryIndex];
	
			// If Parent member of the member to be created was selected, get new child member form
			if ($.inArray(category, parentList) == parentList.length - 2) {
				getNewChildMemberForm(childCategoryName, parentMemberId, parentMemberName);
			
			// Else populate child category drop-down with child members for given parent member
			} else {
				getMembersForCategory(childCategoryName, parentMemberId);
				hideChildSelectDropdownLists(childCategoryIndex);
			}
		}
	});
	
	// Attach a submit handler to the add new member form view (memberForm.jsp)
    $("#memberForm").submit( function(event) {
    	// Stop form from submitting normally
        event.preventDefault();
        
        postFormUsingAjax('CreateMember', $("#memberForm").serialize(), 'result');
    });
});


/**
 * Cleanup, Member(Category) to be created reselected
 */ 
function cleanupOnCategoryReselect(category) {
	// Reset parents list
	parentList = [];
	
	// Hide select boxes for all the categories and containing members
	$.each(categoryToParentMap, function(category, parentCategory) {
		$("#" + category).hide();
	});
	
	// Empty all the fields from the old form and show select existing members view (selectExistingMembers.jsp)
	$('#memberForm').hide();
	$('#result').hide();
	$('#memberFields').empty();
}


/**
 * Build Top down hierarchy from root category to category of member to be created 
 * @param category String containing category for which member is to be created
 */
function buildParentCategoriesListForCategory (category) {
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
	
	var postRequest = $.post('ReadActiveMembers', data);
	
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
 * Get new member form for the category for which member is to be created
 * @param category String category for which member is to be created
 * @param parentMemberId Long parent member id for which child member is to be created
 * @param parentMemberName Sting parent member name for which child member is to be created
 */
function getNewChildMemberForm(category, parentMemberId, parentMemberName) {
	var data = {
		category : category,
		parentMemberId : parentMemberId,
		parentMemberName : parentMemberName
	}
	
	var postRequest = $.post('CreateNewMemberForm', data);
	
	// Populate the server response in the given response element
	postRequest.done( function(responseData) {
		$('#memberForm').show();
	    $('#memberFields').html(responseData);
	    $("#memberLegend").html('<strong>New ' + category + ' form</strong>');
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