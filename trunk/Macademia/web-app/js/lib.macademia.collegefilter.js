// controller for the select colleges filter
macademia.collegeFilter = function() {
    macademia.setupModal('#filterDialog', '#collegeFilterTrigger', 'institution/filter?institutions=all', 'none', 'macademia.initCollegeFilter()');
};

macademia.initCollegeFilter = function() {
    macademia.showColleges();
    macademia.clearSearch();
    $("#closeCollegeFilter a").click(function(){
        $('#filterDialog').jqmHide();
    });
    $(".college a").click(function() {
        $(this).parents("li").hide();
        if($(".college :visible").size() == 0){
            $("#clearMessage").show();
        }
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

    });
    $("#clear").click(function() {
        $("#selectedColleges > ul > li").hide();
        $("#clearMessage").show();
    });
    $("#add").click(function() {
        $("#selectedColleges > ul > li").show();
        if ($("#clearMessage").is(":visible")){
            $("#clearMessage").hide();
        }
    });
    $("#select").click(function() {
        macademia.collegeSelection();
    });
};
// shows colleges that are currently selected under the filter
macademia.showColleges = function(){
    if ($.address.parameter('institutions') == 'all'){
        $("#selectedColleges > ul > li").show();
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