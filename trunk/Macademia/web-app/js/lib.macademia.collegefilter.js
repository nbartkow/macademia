// controller for the select colleges filter
macademia.wireupCollegeFilter = function() {
    macademia.setupModal(
            '#filterDialog',
            '.collegeFilterTrigger .change',
            'institution/filter',
            'none',
            'macademia.initCollegeFilter()'
        );
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
        return false;
    });
    $("#addCollege").click(function() {
        var college = $("#collegeSearchAuto").val();
        $("#filterModal .college").each(function(){
            if ($(this).text().indexOf(college) >= 0){
                $(this).show();
            }
        });
        return false;
    });
    $("#clearAllColleges").click(function() {
        $(".college").hide();
        return false;
    });
    $("#addAllColleges").click(function() {
        $(".college").show();
        return false;
    });
    $("#selectColleges").click(function() {
        macademia.collegeSelection();
        return false;
    });
    macademia.initCollegeSearch();
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
            macademia.showColleges();
            $.address.update();
        }
    }
    $('#filterDialog').jqmHide();

    macademia.serverLog('dialog', 'close',
                {'name' : 'collegeFilter', count : colleges.length});
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
    var results = "";
    if(institutionNames.length == $(".college").size() || institutionNames[0] == 'all'){
        results = 'Showing all schools. (<a href="#" class="change">change</a>)';
    }else if(institutionNames.length == 1){
        results= ('Showing ' + institutionNames[0] + '. (<a href="#/?institutions=all" class="all">show all</a> | <a href="#" class="change">change</a>)');
    }else if(institutionNames.length == 2){
        results= ('Showing ' + institutionNames[0] + " and " + institutionNames[1] + '. (<a href="#/?institutions=all" class="all">show all</a>)');
    }else if(institutionNames.length == 3){
        results= ('Showing ' + institutionNames[0] + ", " + institutionNames[1] + ", and " + institutionNames[2] + '. (<a href="#/?institutions=all" class="all">show all</a> | <a href="#" class="change">change</a>)');
    }else if(institutionNames.length > 3){
        results= ('Showing ' + institutionNames[0] + ", " + institutionNames[1] + ", and " + (institutionNames.length - 2) + " others" + '. (<a href="#/?institutions=all" class="all">show all</a> | <a href="#" class="change">change</a>)');
    }
    if (results != ""){
        $("#collegeFilterButton").html(results);
        $("#collegeFilterButton2").html(results);
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
                if($(this).is(":visible")){
                    var collegeName = $(this).text();
                    collegeNames.push(collegeName);
                }
            });
            macademia.changeCollegeString(collegeNames);
        }else{
            macademia.initiateCollegeString($.address.parameter('institutions'));
        }

    }
};



macademia.initCollegeSearch = function() {
    $("#collegeSearchAuto").macademiaAutocomplete(
        {
            multiple : true,
            select : function (event, ui) {
                var college = ui.item.data[1];
                $('#collegeSearchAuto').val(college);
                $('#addCollege').click();
                window.setTimeout(function () {
                        $("#collegeSearchAuto").val("");
                        $("#collegeSearchAuto").blur();
                    }, 100);

                return false;
            }
        }, "/Macademia/autocomplete/index?klass=institution");
};