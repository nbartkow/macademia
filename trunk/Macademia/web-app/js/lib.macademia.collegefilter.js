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

    $(document).ready(macademia.initIgFilter());

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
        macademia.hideAllSchools();
        macademia.showSchools($("#consortia").val());
        return false;
    });
    $("#selectColleges").click(function() {
        macademia.collegeSelection();
        return false;
    });
    $("#consortia").change(function(){
          macademia.hideAllSchools();
          macademia.showSchools($("#consortia").val());
    });

    macademia.initCollegeSearch();

};


macademia.initIgFilter = function(){
    macademia.hideAllSchools();
    var igId;
    $.each(macademia.igMap, function(key, value){
        if (value.info.abbrev == macademia.retrieveGroup()){
            igId = key;
            return false;
        }
    });
    for (var i=0; i < document.consortiaForm.consortia.options.length; i++){
        if (document.consortiaForm.consortia.options[i].value == igId){
            document.consortiaForm.consortia.options[i].selected = true;
            break;
        }
    }
    if ($.address.parameter('institutions') == "all"){
      macademia.showSchools(igId);
    }
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
            colleges.push($(this).attr('institutionId'));
        }
    });
    // TODO: make ability to switch namespace

    if (colleges.length >0){
        var collegeString = macademia.createInstitutionString(colleges);
        if(collegeString != $.address.parameter('institutions')){
            $.address.parameter('institutions', collegeString);
            $.address.update();
            macademia.showColleges();
        }
    }


    $('#filterDialog').jqmHide();

    if (!macademia.updateIgInURL()) {
        macademia.serverLog('dialog', 'close',
                {'name' : 'collegeFilter', count : colleges.length});
    }
};

macademia.updateIgInURL = function(){
    var oldIg = macademia.retrieveGroup();
    var newIg = macademia.igMap[$("#consortia").val()].info.abbrev;
    var marker = '/Macademia/';     // string appearing before group
//    var url = window.location.href;
    var url = $.address.baseURL() + '/#/?' + $.address.queryString();
    var i = url.indexOf(marker, 0);
    var j = url.indexOf('/', i + marker.length + 1);
    var newUrl = url.substring(0, i + marker.length) + newIg + url.substring(j, url.length);

    window.location.replace(newUrl);

    return (oldIg != newIg);
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
        //check if all colleges in colleges are in current institution group
        if (macademia.collegesInGroup(collegeArray)) {
            return "all";
        }
        return colleges;
};

macademia.collegesInGroup = function(collegeArray){
        var currentGroup = macademia.igMap[$("#consortia").val()].info.abbrev;
        var igMap = macademia.igMap;
        var institutionGroupPosition;
        for (var igCount in igMap) {
            if (igMap[igCount].info.abbrev == currentGroup){
                institutionGroupPosition = igCount;
                break;
            }

        }

        return (igMap[institutionGroupPosition]["institutions"].length == collegeArray.length)

};


macademia.initiateCollegeString = function(ids){
    $.getJSON(macademia.makeActionUrl('institution', 'idsToNames'), {ids: ids.replace(/\+/g, " ")}, function(institutionList){
    macademia.changeCollegeString(institutionList);
    });
};

macademia.changeCollegeString = function(institutionNames){
    var results = "";

    var group;
    var abbrev = macademia.retrieveGroup();
    if(abbrev != "all") {
        group = " from " + abbrev + ". ";
    } else {
        group = ". ";
    }

    if(institutionNames.length == $(".college").size() || institutionNames[0] == 'all'){
        results = 'Showing all schools' +group+'(<a href="#" class="change">change</a>)';
    }else if(institutionNames.length == 1){
        results= ('Showing ' + institutionNames[0] + group +' (<a href="#/?institutions=all" class="all">show all</a> | <a href="#" class="change">change</a>)');
    }else if(institutionNames.length == 2){
        results= ('Showing ' + institutionNames[0] + " and " + institutionNames[1] + group+' (<a href="#/?institutions=all" class="all">show all</a>)');
    }else if(institutionNames.length == 3){
        results= ('Showing ' + institutionNames[0] + ", " + institutionNames[1] + ", and " + institutionNames[2] + group+' (<a href="#/?institutions=all" class="all">show all</a> | <a href="#" class="change">change</a>)');
    }else if(institutionNames.length > 3){
        results= ('Showing ' + institutionNames[0] + ", " + institutionNames[1] + ", and " + (institutionNames.length - 2) + " others" + group+' (<a href="#/?institutions=all" class="all">show all</a> | <a href="#" class="change">change</a>)');
    }



    if (results != ""){
        $("#collegeFilterButton").html(results);
        $("#collegeFilterButton2").html(results);
        macademia.wireupCollegeFilter();
    }
    macademia.queryString.institutions = $.address.parameter('institutions');
};

macademia.changeDisplayedColleges = function(){

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
        }, macademia.makeActionUrl('autocomplete', 'index') + '?klass=institution');
};

macademia.hideAllSchools = function() {
    $("#selectedColleges .collegeDiv").hide();
};

macademia.showSchools = function(igId){
        var visibleInstitutions = new Array();
        $.address.parameter('institutions', "all");

        for (var i=0; i < macademia.igMap[igId]["institutions"].length; i++) {
            visibleInstitutions.push(macademia.igMap[igId]["institutions"][i]["id"]);
        }
        for (var i=0; i < visibleInstitutions.length; i++) {
            $("#c_"+visibleInstitutions[i]).show();
            $("#c_"+visibleInstitutions[i]+" li").show();
        }
};

