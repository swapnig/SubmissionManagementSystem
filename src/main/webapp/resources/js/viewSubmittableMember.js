/**
 * Handle all the events on the view member details page.
 *
 * @author Swapnil Gupta
 * @date Jun 12, 2015
 */


/**
 * Attach all the event handlers for the current view, using jquery, when the document is ready to be loaded.
 */
$(document).ready(function() {
	
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
	    } else{
	         $('#memberForm :text').prop('disabled', true);
	         $('#memberForm textarea').attr('disabled', true);
	         $('#saveMemberAttributes').hide();
	    }
    });
	
	// Attach a submit handler to the add new member form view (memberForm.jsp)
	$("#memberForm").submit(function(event) {
		// Stop form from submitting normally
        event.preventDefault();
        
        postFormUsingAjax('UpdateMember', $("#memberForm").serialize(), 'result');
	});
});
