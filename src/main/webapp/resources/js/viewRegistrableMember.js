/**
 * Handle all the events on the view registrable member view.
 *
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 */

/**
 * Attach all the event handlers for the current view, using jquery, when the document is ready to be loaded.
 */
$(document).ready(function() {
	
	// Attach event handler for view submittables event
	$('#viewSubmittables').click(function(){
		getMemberSubmittables($('#memberID').val());
    });
	
	// Attach event handlers for edit member
	$('#editMemberAttributes').click(function(){
		/*
		 * If all the text fields in member are disabled then, 
		 * 1. Enable all text fields
		 * 2. Show Save member button
		 * 3. Show form to register user with member
		 */
		if( $('#memberForm :text').prop('disabled') ) {
			 $('#memberId').prop("value", $('#memberID').val());
		     $('#memberForm :text').prop('disabled', false);
		     $('#memberForm textarea').attr('disabled', false);
		     $('#saveMemberAttributes').show();
		     $('#registerUserWithMemberForm').show();
	    } else{
	         $('#memberForm :text').prop('disabled', true);
	         $('#memberForm textarea').attr('disabled', true);
	         $('#saveMemberAttributes').hide();
	         $('#registerUserWithMemberForm').hide();
	         $('#registerUserWithMemberForm')[0].reset();
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
 * Get submittables for the given member id
 * @param memberId member id for which submittable members need to be found
 */
function getMemberSubmittables(memberId) {
	var data = {
			memberId : memberId
	}
	
	var postRequest = $.post('ReadSubmittablesForMember', data);
	
	// Populate the server response in the given response element
	postRequest.done( function(responseData) {
	    $('#submittables').html(responseData);
	    $('#submittablesFieldSet').show();
	});
	
	// Notify user that a request to the server failed
	postRequest.error( function(responseData) {
		$('#result').empty().append("<font size='4' color='red'>" +
		"There was issue processing the request. Try again or contact administrator</font>").show();
	});
}
