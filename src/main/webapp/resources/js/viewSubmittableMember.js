$(document).ready(function() {
	
	$('#editMemberAttributes').click(function(){
		
		// If all the text fields in member are disabled then,
		// Enable all text fields, show Save member button
		// Show form to register user with member
		if( $('#memberForm :text').prop('disabled') ) {
		     $('#memberForm :text').prop('disabled', false);
		     $('#saveMemberAttributes').show();
		     $('#registerUserWithMemberForm').show();
		     $('#memberId').prop("value", $('#memberID').val());
	    } else{
	         $('#memberForm :text').prop('disabled', true);
	         $('#saveMemberAttributes').hide();
	         $('#registerUserWithMemberForm')[0].reset();
	         $('#registerUserWithMemberForm').hide();
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
        postFormUsingAjax('RegisterUserForMember', $("#registerUserWithMemberForm").serialize(), 'result');
    });
});
