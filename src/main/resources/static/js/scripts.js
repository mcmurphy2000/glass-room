/**
 * 
 */

/* checkout.html */
function checkOrderBillingAddress() {
	if($("#theSameAsOrderShipping").is(":checked")) {
		$(".orderBilling").prop("disabled", true);
	} else {
		$(".orderBilling").prop("disabled", false);
	}
}

function checkPasswordMatch() {
	var password = $("#txtNewPassword").val();
	var confirmPassword = $("#txtConfirmPassword").val();
	
	if(password == "" && confirmPassword =="") {
		$("#checkPasswordMatch").html("");
		$("#updateUserInfoButton").prop('disabled', false);
		$("#newPasswords").prop('class', 'form-group');
	} else {
		if(password != confirmPassword) {
			$("#checkPasswordMatch").html("Passwords do not match!");
			$("#updateUserInfoButton").prop('disabled', true);
			$("#newPasswords").prop('class', 'form-group has-error');
		} else {
			$("#checkPasswordMatch").html("Passwords match");
			$("#updateUserInfoButton").prop('disabled', false);
			$("#newPasswords").prop('class', 'form-group');
		}
	}
}

/* myAccount.html, shoppingCart.html, checkout.html */
$(document).ready(function(){
	$(".cartItemQty").on('change', function(){
		var id=this.id;
		
		$('#update-item-'+id).css('display', 'inline-block');
	});
	$("#theSameAsOrderShipping").on('click', checkOrderBillingAddress);
	$("#txtConfirmPassword").keyup(checkPasswordMatch);
	$("#txtNewPassword").keyup(checkPasswordMatch);
	
	//$("#addMore").on('click', addMore);
	var max_rows      	= 50;
	var wrapper         = $("#myTBody");
	var addRowButton    = $("#addRowButton");
	var rowClassName	= "myRow";

	var rowList = document.getElementsByClassName(rowClassName);
	var rowCount = rowList.length;	// number of rows
	
	// hide 'Delete' button if there's only one row
	if (rowCount == 1) 
		$('.deleteRow').css('display', 'none');

    $(wrapper).on("click", ".deleteRow", function(e) {
        e.preventDefault();
    	if (rowCount > 1) {	// one row should always remain 
    		$(this).parent('td').parent('tr').remove();
    		rowCount--;
    	}
    	// hide 'Delete' button if there's only one row
    	if (rowCount == 1) 
    		$('.deleteRow').css('display', 'none');
    });
	
    $(addRowButton).click(function(e) {
    	e.preventDefault();
		if (rowCount < max_rows){
			// show 'Delete' button if there was only one row before
	    	if (rowCount == 1) 
	    		$('.deleteRow').css('display', 'inline-block');
		    appendRow(wrapper);
			rowCount++;
		} else {
			alert('You Reached the limits')
		}
	  });
    
	toggle();
    window.onresize = function() { 
    	toggle();
    }
    
    function toggle() {
        if (window.innerWidth < 835) {	// hide 1st column of table and all elements with class=".input-group-addon" if window is narrow 
        	$('.input-group-addon, .firstColumn').css({'display': 'none'});
        } else {						// show them back if window is wide 
        	$('.input-group-addon, .firstColumn').css({'display': ''});
        }    
    }
    
	$('#deleteAll').click(function(e) {
		e.preventDefault();
		/*<![CDATA[*/
	    var path = /*[[@{/}]]*/'/admin/remove';
	    /*]]>*/
		
		var id=$(this).attr('id');
		
		console.log(path);
		
		bootbox.confirm({
			message: "Are you sure to remove this book? It can't be undone.",
			buttons: {
				confirm: {
					label:'<i class="fa fa-check"></i> Confirm'
				},
				cancel: {
					label:'<i class="fa fa-times"></i> Cancel'
				}
			},
			callback: function(confirmed) {
				if(confirmed) {
					$.post(path, {'id':id}, function(res) {
						location.reload();
					});
				}
			}
		});
	});
    
	  
});

function appendRow(wrapper) {
	/* 
	 * Method 1
	 * This method uses REGEX replacing innerHTML but that's a wrong way to do it
	 * A better way is to manipulate DOM objects, see Method 2 below
	 * 
	 */
	
	/*
	//document.getElementById vs jQuery $()
	//document.getElementById('myTBody'); //returns a HTML DOM Object
	//var contents = $('#myTBody');  //returns a jQuery Object
	//var contents = $('#myTBody')[0]; //returns a HTML DOM Object
	var formHTML = wrapper[0].innerHTML;
	//var formHTML = document.getElementById('myTBody').innerHTML
	
	// For JavaScript REGEX see:
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/replace
	// /.../g - /g flag means GLOBAL, search/replace all
	
	// search for string below to get the maxId
	// <input type="hidden" id="rectList0.id" name="rectList[0].id" value="1"> 
	var regex = /<input type="hidden" id=[^>]+rectList\[(\d+)\][^>]+value="(\d+)"[^>]*>/g;
	var xArray;
	var maxIndex = 0;
	var maxId = 0;
	while (xArray = regex.exec(formHTML)) {
		maxIndex 	= Math.max(parseInt(xArray[1]), maxIndex);
		maxId 		= Math.max(parseInt(xArray[2]), maxId);
	}
	var nextIndex = maxIndex + 1;
	var nextId = maxId + 1;

	var rowList = document.getElementsByClassName(rowClassName);
	//var rowCount = rowList.length;	// number of rows
	var rowHTML = rowList[0].innerHTML;	// take first row

	// rectList[1]
	// replace to rectList[<nextIndex>]
	var newRowHTML = "<tr class=\"myRow\">\n" + rowHTML.replace(/(rectList\[)(\d*)(\])/g, "$1" + nextIndex + "$3") + "</tr>\n";

	// I think this one doesn't matter but still...
	// rectList1
	// replace to rectList<nextIndex>
	newRowHTML = newRowHTML.replace(/(rectList)(\d+)/g, "$1" + nextIndex);
	
	// <span>1</span> ... <input type="hidden" id="rectList16.id" name="rectList[16].id" value="1"> 
	// replace to <span>nextId</span> ... value="<nextId>"
	newRowHTML = newRowHTML.replace(/(<span>)(\d+)(<\/span>\s*<input type=\"hidden\" id=\"rectList[^>]+value=\")(\d+)/g, "$1" + nextId + "$3" + nextId);

	// name="rectList[1].width" value="578"
	// replace to value=""
	newRowHTML = newRowHTML.replace(/(name=\"rectList\[\d+\]\.)(width|height)(\" value=\")(\d*)(\")/g, "$1$2$3$5");

	// name="rectList[1].label" value="Label1"
	// replace to value="P<nextId>"
	newRowHTML = newRowHTML.replace(/(name=\"rectList\[\d+\]\.label\" value=\")(.*)(\")/g, "$1" + "P" + nextId + "$3");

	// name="rectList[1].quantity" value="8"
	// replace to value="1"
	newRowHTML = newRowHTML.replace(/(name=\"rectList\[\d+\]\.quantity\" value=\")(\d*)(\")/g, "$1" + 1 + "$3");

	// name="rectList[1].color" value="#1D7D85"
	// replace to value="#1D7D85"
	newRowHTML = newRowHTML.replace(/(name=\"rectList\[\d+\]\.color\" value=\")(.*)(\")/g, "$1" + "#1D7D85" + "$3");

	wrapper.append(newRowHTML);
	*/
	
	
	/*
	 * Method 2
	 * Manipulating DOM objects
	 * 
	 */
	// [id^='someId'] will match all ids starting with someId.
	// [id$='someId'] will match all ids ending with someId.
	// [id*='someId'] will match all ids containing someId.
	// see:
	// https://stackoverflow.com/questions/8714090/queryselector-wildcard-element-match
	// https://www.w3.org/TR/selectors/#overview
	// https://developer.mozilla.org/en-US/docs/Web/API/Document/querySelector
	// https://developer.mozilla.org/en-US/docs/Learn/CSS/Introduction_to_CSS/Combinators_and_multiple_selectors
	//
	// <input type="hidden" id="rectList1.id" name="rectList[1].id" value="2" /> 
	var inputList = document.getElementById("myTBody").querySelectorAll("input[id$='.id']");	// select all input-s that have id ending with '.id'

	// Calculate maxIndex and maxId across <input... elements 
	var maxIndex = 0;
	var maxId = 0;
	for (var i = 0; i < inputList.length; i++) {
		//var myString = "rectList[1].id";
		var myString = inputList[i].getAttribute('name');
		var myRegexp = /rectList\[(\d+)\]/;
		var match = myRegexp.exec(myString);
		var index = parseInt(match[1]);
		maxIndex 	= Math.max(index, maxIndex);
		
		var id = parseInt(inputList[i].getAttribute('value'))
		maxId 		= Math.max(id, maxId);
	}
	var nextIndex = maxIndex + 1;
	var nextId = maxId + 1;

	// clone row
	var rowList = document.getElementsByClassName("myRow");	// all rows
	var newRow = rowList[rowList.length - 1].cloneNode(true);	// Copy the last <tr> element and its child nodes

	// replace all "rectList[1]" and "rectList1" in new row with correct index: nextIndex
	var inputList = newRow.querySelectorAll("input");	// select all input-s
	for (var i = 0; i < inputList.length; i++) {
		var input = inputList[i];
		
		// name="rectList[1].label"		-> 		rectList[<nextIndex>].label 
		input.name = input.name.replace(/(rectList\[)(\d+)(\].*)/, "$1" + nextIndex + "$3");
		
		// id="rectList1.label"			-> 		rectList<nextIndex>.label 
		input.id = input.id.replace(/(rectList)(\d+)(\..*)/, "$1" + nextIndex + "$3");
	}
	
	// replace all value= in this row with correct value 
	var defaultId = nextId;
	var defaultLabel = "P" + nextId;
	var defaultWidth = "";
	var defaultHeight = "";
	var defaultQuantity = 1;
	var defaultColor = "#1D7D85";
	newRow.querySelector("input[name$='.id']").value = defaultId;				// select first input that has name ending with '.id'
	newRow.querySelector("input[name$='.label']").value = defaultLabel;			// select first input that has name ending with '.label'
	newRow.querySelector("input[name$='.width']").value = defaultWidth;			// select first input that has name ending with '.width'
	newRow.querySelector("input[name$='.height']").value = defaultHeight;		// select first input that has name ending with '.height'
	newRow.querySelector("input[name$='.quantity']").value = defaultQuantity;	// select first input that has name ending with '.quantity'
	newRow.querySelector("input[name$='.color']").value = defaultColor;			// select first input that has name ending with '.color'
	
	// 1st column of the table
	// replace it with nextId (if it exists) 
	var idValue = newRow.querySelector('tr td span');	// first value inside <span></span> which is inside td which is inside tr
	if (idValue != null) idValue.textContent = nextId;	// can be null if I decide to remove first column of the table later
	
	// Append the new row
	document.getElementById("myTBody").appendChild(newRow);
	//document.getElementById("myTBody").appendChild(document.createElement("br"));	// Append a line break
}

/* ****************************************************
 * 
 * /admin scripts 
 * 
 * */

$(document).ready(function() {
	$('.delete-book').on('click', function (){
		/*<![CDATA[*/
	    var path = /*[[@{/}]]*/'/admin/remove';
	    /*]]>*/
		
		var id=$(this).attr('id');
		
		console.log(path);
		
		bootbox.confirm({
			message: "Are you sure to remove this book? It can't be undone.",
			buttons: {
				cancel: {
					label:'<i class="fa fa-times"></i> Cancel'
				},
				confirm: {
					label:'<i class="fa fa-check"></i> Confirm'
				}
			},
			callback: function(confirmed) {
				if(confirmed) {
					$.post(path, {'id':id}, function(res) {
						location.reload();
					});
				}
			}
		});
	});
	
	
	
//	$('.checkboxBook').click(function () {
//        var id = $(this).attr('id');
//        if(this.checked){
//            bookIdList.push(id);
//        }
//        else {
//            bookIdList.splice(bookIdList.indexOf(id), 1);
//        }
//    })
	
	$('#deleteSelected').click(function() {
		var idList= $('.checkboxBook');
		var bookIdList=[];
		for (var i = 0; i < idList.length; i++) {
			if(idList[i].checked==true) {
				bookIdList.push(idList[i]['id'])
			}
		}
		
		console.log(bookIdList);
		
		/*<![CDATA[*/
	    var path = /*[[@{/}]]*/'/admin/removeList';
	    /*]]>*/
	    
	    bootbox.confirm({
			message: "Are you sure to remove all selected books? It can't be undone.",
			buttons: {
				cancel: {
					label:'<i class="fa fa-times"></i> Cancel'
				},
				confirm: {
					label:'<i class="fa fa-check"></i> Confirm'
				}
			},
			callback: function(confirmed) {
				if(confirmed) {
					$.ajax({
						type: 'POST',
						url: path,
						data: JSON.stringify(bookIdList),
						contentType: "application/json",
						success: function(res) {
							console.log(res); 
							location.reload()
							},
						error: function(res){
							console.log(res); 
							location.reload();
							}
					});
				}
			}
		});
	});
	
	$("#selectAllBooks").click(function() {
		if($(this).prop("checked")==true) {
			$(".checkboxBook").prop("checked",true);
		} else if ($(this).prop("checked")==false) {
			$(".checkboxBook").prop("checked",false);
		}
	})
});