var checkAllAnt = function() {
    $("input[name^='__ant__']:checkbox").prop('checked', true);
};

var uncheckAllAnt = function() {
    $("input[name^='__ant__']:checkbox").prop('checked', false);
};

var checkAllCon = function() {
    $("input[name^='__con__']:checkbox").prop('checked', true);
};

var uncheckAllCon = function() {
    $("input[name^='__con__']:checkbox").prop('checked', false);
};

var checkAttribute = function() {
    var nameLen = $("#attribute").val().length;
    
    if ( nameLen === 0) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkFD = function() {
    var antLen = $("input[name^='__ant__']:checkbox:checked").length;
    var conLen = $("input[name^='__con__']:checkbox:checked").length;
    
    if ( antLen == 0 || conLen == 0) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkFDSet = function() {
    var nameLen = $("#__fdjoint-name__").val().length;
    var fdLen = $(":checkbox:checked").length;
    
    if ( nameLen == 0 || fdLen == 0) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkRelation = function() {
    var nameLen = $("#__relation-name__").val().length;
    var fdSetLen = $(":radio:checked").length;
    
    if ( nameLen == 0 || fdSetLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkNormalFormTest = function() {
    var nfLen = $("input[name^='__nf__']:radio:checked").length;
    var relLen = $("input[name^='__relation-name__']:radio:checked").length;
    
    if ( nfLen == 0 || relLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkFDSetMinimal = function() {
    var fdSetLen = $("input[name^='__fdjoint-name__']:radio:checked").length;
    
    if ( fdSetLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkKeys = function() {
    var attrLen = $("input[name^='__attr__']:checkbox:checked").length;
    var relLen = $("input[name^='__relation-name__']:radio:checked").length;
    
    if ( attrLen == 0 || relLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkDoubleFDSet = function() {
    var fdSetALen = $("input[name^='__fdjoint-a__']:radio:checked").length;
    var fdSetBLen = $("input[name^='__fdjoint-b__']:radio:checked").length;
    
    if ( fdSetALen == 0 || fdSetBLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkInferred = function() {
    var antLen = $("input[name^='__ant__']:checkbox:checked").length;
    var conLen = $("input[name^='__con__']:checkbox:checked").length;
    var fdSetLen = $("input[name^='__fdjoint-name__']:radio:checked").length;
    
    if ( antLen == 0 || conLen == 0 || fdSetLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkRelationKeys = function() {
    var relLen = $("input[name^='__relation-name__']:radio:checked").length;
    
    if ( relLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkProjection = function() {
    var attrLen = $("input[name^='__attr__']:checkbox:checked").length;
    var fdSetLen = $("input[name^='__fdjoint-name__']:radio:checked").length;
    
    if ( attrLen == 0 || fdSetLen == 0 ) {
        alert("Please fill in all necessary data");
    } else {
        $('form').submit();
    }
};

var checkUsername = function() {
    var nameLen = $("#__username__").val().length;
    
    if ( nameLen == 0) {
        alert("Please enter a session name");
    } else {
        $('form').submit();
    }
};


$(".relation:button").click(function(event) {
    var relation = $(this).val();
    
    $.post(
		"/odn/add-relation",
		{ "__relation-name__" : relation },
		function( data ) {
			$(this).prop("disabled", true);
			$(this).text("Added");
		}
	)
	
	$(this).prop("disabled", true);
	$(this).text("Added");
});