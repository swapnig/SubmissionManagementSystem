function postDataUsingAjax (url, data, responseElementId) {
	console.log(data);
	var posting = $.post(url, data);
	
	posting.done( function(responseData) {
	    $('#' + responseElementId).html(responseData);
	});
}

function postFormUsingAjax (url, serializedFormData, responseElementId) {
	console.log(serializedFormData);
    var posting = $.post(url, serializedFormData);

    posting.done( function(data) {
        $('#' + responseElementId).empty().append(data + ' using ' + url).show();
    });
}

// Build request data object from an array of data request parameters
function buildRequestData(dataArray) {
	console.log(dataArray);
	var dataObject = {};
	for (var index in dataArray) {
		var data = dataArray[index];
		dataObject[index] = data;
	}
	console.log(dataObject);
}

function foo() {
  for (var i = 0; i < arguments.length; i++) {
    alert(arguments[i]);
  }
}

function getDataUsingAjax (requestUrl, data, responseElementId) {
	var getting = $.get(requestUrl, data);
	
	getting.done( function(responseData) {
	    $('#' + responseElementId).html(responseData);
	});
}