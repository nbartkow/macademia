// controller for the select colleges filter
macademia.wireupCollegeFilter = function() {
    macademia.setupModal('#filterDialog', '#collegeFilterTrigger', 'institution/filter', 'none', 'macademia.initCollegeFilter()');
};

macademia.initCollegeFilter = function() {
    macademia.serverLog('dialog', 'show', {'name' : 'collegeFilter'});
    macademia.showColleges();
    $("#editColleges .clearDefault").clearDefault();
    $("#closeCollegeFilter a").click(function(){
        $('#filterDialog').jqmHide();
        macademia.serverLog('dialog', 'cancel', {'name' : 'collegeFilter'});
        return false;
    });
    $(".college a").click(function() {
        $(this).parents("li").hide();
        if($(".college :visible").size() == 0){
            $("#clearMessage").show();
        }
        return false;
    });
    $("#addCollege").click(function() {
        var college = $("#collegeSearchAuto").val();
        $(".college").each(function(){
            if ($(this).text().indexOf($("#collegeSearchAuto").val()) >= 0){
                $(this).show();
                if ($("#clearMessage").is(":visible")){
                    $("#clearMessage").hide();
                }
            }
        });
        return false;
    });
    $("#clearAllColleges").click(function() {
        $(".college").hide();
        $("#clearMessage").show();
        return false;
    });
    $("#addAllColleges").click(function() {
        $(".college").show();
        if ($("#clearMessage").is(":visible")){
            $("#clearMessage").hide();
        }
        return false;
    });
    $("#selectColleges").click(function() {
        macademia.collegeSelection();
        return false;
    });
    macademia.autocomplete.initCollegeSearch();
};

// shows colleges that are currently selected under the filter
macademia.showColleges = function(){
    if ($.address.parameter('institutions') == 'all'){
        $(".college").each(function(){
            $(this).show();
        });
    }else{
        var collegeIds = $.address.parameter('institutions').split("+");
        for (var i = 0; i<collegeIds.length; i++){
            var college = "#" + collegeIds[i];
            $(college).show();
        }
    }

};

// puts the selected colleges from the college filter into the address bar
macademia.collegeSelection = function() {
    var colleges = new Array();
    $("#selectedColleges li").each(function() {
        if ($(this).is(':visible')) {
            colleges.push($(this).attr('id'));
        }
    });
    if (colleges.length >0){
        var collegeString = macademia.createInstitutionString(colleges);
        if(collegeString != $.address.parameter('institutions')){
            $.address.parameter('institutions', collegeString);
            $.address.update();
        }
    }

    $('#filterDialog').jqmHide();

    macademia.serverLog('dialog', 'close', {'name' : 'collegeFilter', count : colleges.length});
};

// takes an array of college ids and creates a string to stick in the url
macademia.createInstitutionString = function(collegeArray) {
        var colleges = "";
        if (collegeArray.length == $(".college").size()) {
            // if no colleges selected, default to all
            colleges = "all";
        } else {
            for (var i = 0; i < collegeArray.length; i++) {
                if (i < collegeArray.length - 1) {
                    colleges = colleges + collegeArray[i] + '+';
                } else {
                    colleges = colleges + collegeArray[i];
                }
            }
        }
        return colleges;
};

macademia.initiateCollegeString = function(ids){
    $.getJSON('/Macademia/institution/idstonames', {ids: ids.replace(/\+/g, " ")}, function(institutionList){
    macademia.changeCollegeString(institutionList);
    });
};

macademia.changeCollegeString = function(institutionNames){
    debug.log(institutionNames);
    var results = "";
    if(institutionNames.length == $(".college").size() || institutionNames[0] == 'all'){
        results = 'Showing <b>all institutions</b>. <div>(<a href="#" id="collegeFilterTrigger">filter by institution</a>)</div>';
    }else if(institutionNames.length == 1){
        results= ('Showing <b>' + institutionNames[0] + '</b>. <div>(<a href="#/?institutions=all" class="showAllInstitutions">show all institutions</a>) | (<a href="#" id="collegeFilterTrigger">change filter</a>)</div>');
    }else if(institutionNames.length == 2){
        results= ('Showing <b>' + institutionNames[0] + " and " + institutionNames[1] + '</b>. <div>(<a href="#/?institutions=all" class="showAllInstitutions">show all institutions</a>)');
    }else if(institutionNames.length == 3){
        results= ('Showing <b>' + institutionNames[0] + ", " + institutionNames[1] + ", and " + institutionNames[2] + '</b>. <div>(<a href="#/?institutions=all" class="showAllInstitutions">show all institutions</a>) | (<a href="#" id="collegeFilterTrigger">change filter</a>)</div>');
    }else if(institutionNames.length > 3){
        results= ('Showing <b>' + institutionNames[0] + ", " + institutionNames[1] + ", and " + (institutionNames.length - 2) + " others" + '</b>. <div>(<a href="#/?institutions=all" class="showAllInstitutions">show all institutions</a>) | (<a href="#" id="collegeFilterTrigger">change filter</a>)</div>');
    }
    if (results != ""){
        $("#collegeFilterButton").html(results);
        macademia.wireupCollegeFilter();
    }
    macademia.queryString.institutions = $.address.parameter('institutions');
};

macademia.changeDisplayedColleges = function(){
    if ($.address.parameter('institutions') != macademia.queryString.institutions){
        if($.address.parameter('institutions') == 'all'){
            macademia.changeCollegeString(['all']);
        }else if ($(".college").size() > 0){
            var collegeNames = new Array();
            $(".college").each(function(){
                if($(this).attr("style")!="display: none;"){
                    var collegeName = $(this).text();
                    var removable = collegeName.indexOf(" (x)");
                    collegeName = collegeName.substr(0, removable);
                    collegeNames.push(collegeName);
                }
            });
            macademia.changeCollegeString(collegeNames);
        }else{
            macademia.initiateCollegeString($.address.parameter('institutions'));
        }

    }
};
