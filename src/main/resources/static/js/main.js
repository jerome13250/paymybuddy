//Sort a form input select by text:
$(document).ready(function() {

	let optionsList = $('#userDestination option');
	let selected = $("#userDestination").val();
	
	optionsList.sort(function(a,b){
	    if (a.text > b.text) return 1;
	    if (a.text < b.text) return -1;
	    return 0
	
	});
	
	$('#userDestination').html(optionsList);
	$("#userDestination").val(selected);

});