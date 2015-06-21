/**
 * Set of generic utility functions used by the entire application to make ajax calls
 * @author Swapnil Gupta
 * @date May 30, 2015
 * 
 */


/**
 * Generic function for making a post request using ajax
 * @param url String containing the URL to which the request is sent
 * @param data Data to be sent to the server, Object must be Key/Value pairs.
 * @param responseElementId String containing Html element id of the div in which the response 
 * 		  from the server is shown
 * @param errorElementId String containing Html element id of the div in which the error 
 * 		  from the request to server should be shown
 */
function postDataUsingAjax (url, data, responseElementId, errorElementId) {
	var postRequest = $.post(url, data);
	
	// Populate the server response in the given response element
	postRequest.done( function(responseData) {
	    $('#' + responseElementId).html(responseData);
	});
	
	// Notify user that a request to the server failed
	postRequest.error( function(responseData) {
	    $('#' + errorElementId).html("<font size='4' color='red'>There was issue processing the request</font>");
	});
}


/**
 * Generic function for submitting a html form as a post request using ajax
 * @param url A string containing the URL to which the request is sent
 * @param serializedFormData Data to be sent to the server, Object must be Key/Value pairs.
 * @param responseElementId Html element id of the div in which the response from the server is shown
 */
function postFormUsingAjax (url, serializedFormData, responseElementId) {
    var postRequest = $.post(url, serializedFormData);

    // Populate the server response in the given response element
    postRequest.done( function(data) {
    	$('#' + responseElementId).empty().append(data).show();
    });
    
    // Notify user that a request to the server failed
    postRequest.error( function(responseData) {
		$('#' + responseElementId).empty().append("<font size='4' color='red'>" +
				"There was issue processing the request. Try again or contact administrator</font>").show();
	});
}


/**
 * Generic function for making a get request using ajax
 * @param url A string containing the URL to which the request is sent
 * @param data Data to be sent to the server, Object must be Key/Value pairs.
 * @param responseElementId String containing Html element id of the div in which the response 
 * 		  from the server is shown
 * @param errorElementId String containing Html element id of the div in which the error from the 
 * 		  request to server should be shown
 */
function getDataUsingAjax (requestUrl, data, responseElementId) {
	var getRequest = $.get(requestUrl, data);
	
	// Populate the server response in the given response element
	getRequest.done( function(responseData) {
	    $('#' + responseElementId).html(responseData);
	});
	
	// Notify user that a request to the server failed
	getRequest.error( function(responseData) {
	    $('#' + responseElementId).html(responseData);
	});
}