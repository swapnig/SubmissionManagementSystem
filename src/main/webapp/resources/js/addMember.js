//Track all the parent categories of the category use to create the member
window.parentList = [];

/**
 * Creates an instance of Circle.
 *
 * @author Swapnil Gupta
 * @constructor
 * @this {Circle}
 * @param {number} r The desired radius of the circle.
 */
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
		$('#result').empty();
		
		var categoryListId = $(this).attr('id');
		var parentMemberId = $('#' + categoryListId + ' option:selected').val();
		var parentMemberName = $('#' + categoryListId + ' option:selected').text();
		
		if(!jQuery.isEmptyObject(parentMemberId)) {
			var category = categoryListId.split("List")[0];
			var childCategoryIndex = parentList.indexOf(category) + 1;
			var childCategoryName = parentList[childCategoryIndex];
	
			// If Parent category for member to be created changed, get new child member form
			if ($.inArray(category, parentList) == parentList.length - 2) {
				getNewChildMemberForm(childCategoryName, parentMemberId, parentMemberName);
			
			// Else populate child category drop-down for child members
			} else {
				getMembersForCategory(childCategoryName, parentMemberId);
				hideChildSelectDropdownLists(childCategoryIndex);
			}
		}
	});
	
	// Attach a submit handler to the add new member form
    $("#memberForm").submit( function(event) {
    	// Stop form from submitting normally
        event.preventDefault();
        postFormUsingAjax('CreateMember', $("#memberForm").serialize(), 'result');
    });
});


// Cleanup, Member(Category) to be created reselected
function cleanupOnCategoryReselect(category) {
	//Reset parents list
	parentList = [];
	
	// Hide drop downs for all the categories and containing members
	$.each(categoryToParentMap, function(category, parentCategory) {
		$("#" + category).hide();
	});
	
	// Empty all the fields from the old form
	$('#selectExistingMembers').show();
	$('#memberForm').hide();
	$('#memberFields').empty();
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

/**
 * Creates an instance of Circle.
 *
 * @author Swapnil Gupta
 * @constructor
 * @this {Circle}
 * @param {number} r The desired radius of the circle.
 */
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
function getNewChildMemberForm(category, parentMemberId, parentMemberName) {
	var data = {
		category : category,
		parentMemberId : parentMemberId,
		parentMemberName : parentMemberName
	}
	postDataUsingAjax('CreateNewMemberForm', data, 'memberFields');
	$("#memberLegend").html('<strong>New ' + category + ' form</strong>');
	$('#memberForm').show();
}