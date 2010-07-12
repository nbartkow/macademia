var macademia = macademia || {};
//total number of institutions
macademia.totalInstitutions;
macademia.visibleInstitutions;
// calculates the number of properties (keys, values, etc.) for an object or associative array.
macademia.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};
// highlights adjacencies during mouseover
macademia.highlightAdjacenciesOn = function(node){
    var adjacentNodes = [];
    var root = macademia.rgraph.graph.getNode(macademia.rgraph.root);
    root.eachSubnode(function(n){
        n.eachAdjacency(function(adj){
            if (adj.nodeTo.id != node.id && adj.nodeFrom.id != node.id){
                if (adj.data.$color != "#999" && adj.data.$color != undefined){
                    if(adj.data.$color)
                    adj.data.$colorB = adj.data.$color;
                    adj.data.$color = "#999";
                }
            }else if (adj.nodeTo.id != node.id){
                adjacentNodes.push(adj.nodeTo.id);
                adj.data.$lineWidth = 1.8;
            }else{
                adjacentNodes.push(adj.nodeFrom.id);
                adj.data.$lineWidth = 1.8;
            }
        })
    });
    for (var i = 0; i < adjacentNodes.length; i++){
        var adjN = "#" + adjacentNodes[i];
        $(adjN).css('font-weight', 'bold');
        $(adjN).css('opacity', 0.75);
        $(adjN).css('z-index', 30);
        $(adjN).css('background-color', '#A2AB8E');
    }
};
// returns graph to original coloring during mouseout
macademia.highlightAdjacenciesOff = function(node){
    var adjacentNodes = [];
    var root = macademia.rgraph.graph.getNode(macademia.rgraph.root);
    root.eachSubnode(function(n){
        n.eachAdjacency(function(adj){
            if (adj.nodeTo.id != node.id && adj.nodeFrom.id != node.id){
                if(adj.data.$colorB != "#999" && adj.data.$colorB != undefined){
                    adj.data.$color = adj.data.$colorB;
                }
            }else if (adj.nodeTo.id != node.id){
                adjacentNodes.push(adj.nodeTo.id);
                adj.data.$lineWidth = 1;
            }else{
                adjacentNodes.push(adj.nodeFrom.id);
                adj.data.$lineWidth = 1;
            }
        })
    });
    for (var i = 0; i < adjacentNodes.length; i++){
        var adjN = "#" + adjacentNodes[i];
        $(adjN).css('font-weight', 'normal');
        $(adjN).css('opacity', 0.8);
        $(adjN).css('z-index', 10);
        $(adjN).css('background-color','transparent');
    }
};
//holds query string values
macademia.queryString = {nodeId:'p_1', navVisibility:'true', navFunction:'search', institutions:'all', searchBox:null, interestId:null, personId:null, requestId:null, searchPage:null};

//sets the sidebar's visibility according to original status.  Initializes jit visualization
macademia.pageLoad = function() {
    // address only updates manually when a link/node is clicked
    $.address.autoUpdate(false);
    macademia.initialSettings();
    macademia.showHide();
    macademia.initiateGraph();
    macademia.nav();
};
//sets macademia.queryString values and initial page settings
macademia.initialSettings = function(){
        $("#show").hide();
        if($.address.parameter('nodeId')){
            macademia.queryString.nodeId = $.address.parameter('nodeId');
        }else{
            $.address.parameter('nodeId',macademia.queryString.nodeId);
        }
        if(!$.address.parameter('navVisibility')){
            $.address.parameter('navVisibility',macademia.queryString.navVisibility);        
        }if($.address.parameter('navFunction')){
            macademia.queryString.navFunction = $.address.parameter('navFunction');
        }else{
            $.address.parameter('navFunction',macademia.queryString.navFunction);
        }
        if($.address.parameter('institutions')){
            macademia.queryString.institutions = $.address.parameter('institutions');
        }else{
            $.address.parameter('institutions',macademia.queryString.institutions);
        }
        macademia.sortParameters(macademia.queryString.navFunction);
        $.address.update();
};
//calls the init function in jitConfig
macademia.initiateGraph = function() {
    var param = $.address.parameter('nodeId');
    var type = macademia.getType(param);
    var id = parseFloat(param.substr(2));
    macademia.init(type, id);
};
// determines the type according to the node's id (eg p_4)
macademia.getType = function(nodeId) {
    if (nodeId.indexOf('p') >= 0) {
        return'person';
    } else if (nodeId.indexOf('i') >= 0) {
        return 'interest';
    } else if (nodeId.indexOf('r') >= 0) {
        return 'request';
    }
};
//canvas background circles
macademia.drawCircles = function(canvas, ctx) {
    var times = 2, d = macademia.distance;
    var pi2 = Math.PI * 2;
    for (var i = 1; i <= times; i++) {
        ctx.beginPath();
        ctx.arc(0, 0, i * d, 0, pi2, true);
        ctx.stroke();
        ctx.closePath();
    }
};
// clears the value of the text input box when clicked
macademia.clearSearch = function() {
    $(".clearDefault").focus(function() {
        if ($(this).attr('id') == 'searchBox'){
             var textToClear = "Search people or interests";
        }else if ($(this).attr('id') == 'collegeSearchAuto'){
             var textToClear = "Type college name";
        }
        if ($(this).val() == textToClear) {
            $(this).data("clearedText", $(this).val());
            $(this).val('');
        }
    });
    $(".clearDefault").blur(function() {
        var value = $(this).val();
        if (!value && $(this).data("clearedText")) {
            $(this).val($(this).data("clearedText"));
        }
    });
};

// changes the address when a node is clicked
macademia.navInfovis = function(node) {
    var rootId = node.id;
    var type = macademia.getType(rootId);
    $.address.parameter('nodeId', rootId);
    if (type == 'person' && $.address.parameter('navFunction') != 'person') {
        $.address.parameter('navFunction','person');
    } else if (type == 'interest' && $.address.parameter('navFunction') != 'interest') {
        $.address.parameter('navFunction','interest');
    }
    macademia.sortParameters(type,rootId.substr(2));
    $.address.update();
};


// click navigation for the rightDiv
macademia.nav = function() {
    macademia.modalLogin();
    macademia.modalRegister();
    macademia.clearSearch();
    macademia.collegeFilter();
    $.address.change(function() {
        macademia.showHide();
        macademia.updateNav();
        macademia.changeGraph();
    });
    $(window).resize(function() {
        if (macademia.rgraph) {
            macademia.resizeCanvas($("#infovis").width());
        }
    });
    $("a").address(function() {
        var url = $(this).attr('href');
        if (url && url.length > 1) {
            if (url.indexOf("#") == 0) {
                macademia.changeQueryString(url);
            } else {
                $.address.value(url);
            }
            macademia.sortParameters($.address.parameter('navFunction'));
            $.address.update();
        }
    });
    $('#searchForm').submit(function(){
        var search =($('#searchBox').serialize()).split('=');
        if (search[1] != 'Search+people+or+interests' && search[1] != ""){
            $.address.parameter('navFunction','search');
            $.address.parameter('searchPage', 'all_0');
            macademia.sortParameters('search',search[1]);
            $.address.update();
        }
        else {
            $.address.parameter('searchBox', null);
            $.address.update();
        }
        return false;
    });
};
macademia.modalLogin = function() {
    $('#loginDialog').jqm({modal: false});
    $('#loginButton').click(function(){
        $('#loginDialog').jqmShow()
    });
};

macademia.modalRegister = function() {
    $('#registerDialog').jqm({modal: false});
    $('#registerButton').click(function(){
        $('#registerDialog').jqmShow()
    });
};
// controls the show and hide options
macademia.showHide = function() {
    if ($.address.parameter('navVisibility') != macademia.queryString.navVisibility) {
        var navVisibility = $.address.parameter('navVisibility');
        if (navVisibility == 'true' && !$("#wrapper").is(":visible")) {
            $("#rightDiv").animate({width: "320"}, "slow");
            $("#infovis").animate({right: "320"}, "slow", function() {
                $("#rightDiv > *").show();
                $("#show").hide();
            });
            // resize visual
            if (macademia.rgraph) {
                macademia.resizeCanvas($("body").width() - 320);
            }
        } else if (navVisibility == 'false' && $("#wrapper").is(":visible")) {
            $("#rightDiv > *").hide();
            if (macademia.rgraph) {
                $("#rightDiv").animate({width: "0"}, "slow");
                $("#infovis").animate({right: "0"}, "slow");
            } else {
                // on page load rightDiv will not slide over
                $("#rigthDiv").css('width', '0');
                $("#infovis").css('right', '0');
            }
            $("#show").show();
            // resize visual
            if (macademia.rgraph) {
                macademia.resizeCanvas($("body").width());
            }
        }
        macademia.queryString.navVisibility = navVisibility;
    }
};
// Changes the visualization to new root node
macademia.changeGraph = function(nodeId){
    if ($.address.parameter('nodeId') != macademia.queryString.nodeId) {
        if (macademia.rgraph){
              var param = $.address.parameter('nodeId');
              if (macademia.rgraph.graph.getNode(param)) {
              // if the node is on the current graph
                macademia.rgraph.onClick(param);
                //macademia.rgraph.refresh();
              }else{
                  macademia.initiateGraph();
              }
              macademia.queryString.nodeId = param;
        }
    }if($.address.parameter('institutions') != macademia.queryString.institutions){
        macademia.initiateGraph();
        macademia.queryString.institutions = $.address.parameter('institutions');
    }
};
// resizes canvas according to original dimensions
macademia.resizeCanvas = function(currentWidth) {
//    if ($("#infovis-canvaswidget").css('margin') != 'auto') {
//        $("#infovis-canvaswidget").css('margin', 'auto');
//        $('#infovis-canvas, #infovis-bkcanvas, #infovis-label').css('position','fixed');
//    }
    var originalWidth = 680;
    var originalHeight = 660;
    var originalDistance = 150;
    var currentHeight = $("#infovis").height();
    if (Math.min(currentWidth, currentHeight) == currentWidth) {
        var newWidth = 0.95 * currentWidth;
        var newHeight = originalHeight * newWidth / originalWidth;
    } else {
        var newHeight = 0.95 * currentHeight;
        var newWidth = originalWidth * newHeight / originalHeight;
    }
    if (newWidth != $("#infovis-canvaswidget").css("width")) {
        $("#infovis-canvaswidget").css({"width":newWidth, "height": newHeight});
        //macademia.distance = originalDistance / originalHeight * newHeight;
        //macademia.rgraph.config.levelDistance = macademia.distance;
        //macademia.rgraph.config.background.levelDistance = macademia.distance;
        macademia.rgraph.canvas.resize(currentWidth, currentHeight);
        macademia.rgraph.canvas.scale(newHeight/originalHeight,newWidth/originalWidth);
        //macademia.rgraph.refresh();
    }
};
// changes the Query string according link's href
macademia.changeQueryString = function(query) {
    var queryString = query.substr(3);
    var params = queryString.split('&');
    for (var i = 0; i < params.length; i++) {
        var paramValue = params[i].split('=');
        $.address.parameter(paramValue[0], paramValue[1]);
    }
};
// controller for the select colleges filter
macademia.collegeFilter = function() {
    macademia.setupModal('#filterDialog', '#collegeFilterTrigger', 'institution/filter?institutions=all', 'none', 'macademia.initCollegeFilter()');
//    $('#collegeFilterTrigger').click(function(){
//        $('#filterDialog').jqmShow();
//        var colleges = $.address.parameter('institutions');
//        $('#selectedColleges').load(
//                '/Macademia/institution/filter',
//                {institutions:colleges}
//            );
//    });

};

macademia.initCollegeFilter = function() {
    macademia.showColleges();
    macademia.clearSearch();
    $("#closeCollegeFilter a").click(function(){
        $('#filterDialog').jqmHide();
    });
    $(".college a").click(function() {
        $(this).parents("li").animate({opacity: "hide" }, "normal")
        macademia.visibleInstitutions--;
        if(macademia.visibleInstitutions == 0){
            $("#clearMessage").show();
        }
    });
    $("#addCollege").click(function() {
        var college = $("#collegeSearchAuto").val();
        $(".college").each(function(){
            if ($(this).text().indexOf($("#collegeSearchAuto").val()) >= 0){
                $(this).show();
                macademia.visibleInstitutions++;
                if ($("#clearMessage").is(":visible")){
                    $("#clearMessage").hide();
                }
            }
        });

    });
    $("#clear").click(function() {
        macademia.visibleInstitutions = 0;
        $("#selectedColleges > ul > li").hide();
        $("#clearMessage").show();
    });
    $("#add").click(function() {
        $("#selectedColleges > ul > li").show();
        macademia.visibleInstitutions = macademia.totalInstitutions;
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
    var visible = 0;
    if (macademia.totalInstitutions == undefined){
        macademia.getTotalInstitutions();
    }
    if ($.address.parameter('institutions') == 'all'){
        $("#selectedColleges > ul > li").show();
        visible = macademia.totalInstitutions;
    }else{
        var collegeIds = $.address.parameter('institutions').split("+");
        for (var i = 0; i<collegeIds.length; i++){
            var college = "#" + collegeIds[i];
            $(college).show();
            visible ++;
        }
    }
    macademia.visibleInstitutions = visible;

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
        if (collegeArray.length == macademia.totalInstitutions) {
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
//sets total number of institutions
macademia.getTotalInstitutions = function(){
     var colleges = 0;
     $(".college").each(function(){
            colleges ++;
     });
     macademia.totalInstitutions = colleges;
};
// controls view of right nav (incomplete)
macademia.updateNav = function(){
     var navFunction = $.address.parameter('navFunction');
     macademia.showDivs(navFunction);
     if ($("#instruct_list").is(":visible")){
        macademia.clearInstructions();
     }
     if (navFunction == 'search'){
            macademia.submitSearch();
            macademia.queryString.searchPage = $.address.parameter('searchPage');
         // go to search page
     }else if (navFunction == 'person' && $.address.parameter('personId') != macademia.queryString.personId){
         var rootId = $.address.parameter('nodeId');
         document.getElementById('personIdDiv').innerHTML = '<p>to show user profile</p>';
         macademia.queryString.personId = $.address.parameter('personId');
     }else if (navFunction == 'request'){
         var rootId = $.address.parameter('nodeId');
         document.getElementById('requestIdDiv').innerHTML = '<p>to show collaboration request page</p>';
         macademia.queryString.requestId = $.address.parameter('requestId');
     }else if (navFunction == 'interest'){
         var rootId = $.address.parameter('nodeId');
         document.getElementById('interestIdDiv').innerHTML = '<p>to show interest page</p>';
         macademia.queryString.interestId = $.address.parameter('interestId');
     }//else if etc...
     macademia.queryString.navFunction = navFunction;
};
// removes unused parameters and updates used parameters
macademia.sortParameters = function(type,value){
    var queries = ['searchBox','interestId','personId','requestId'];
    for(var i = 0; i < queries.length; i++){
        if (queries[i].indexOf(type) < 0){
            if ($.address.parameter(queries[i]) || macademia.queryString[queries[i]]){
                $.address.parameter(queries[i],null);
                macademia.queryString[queries[i]] = null;
            }
        }else if (value){
                $.address.parameter(queries[i],value);
        }
    }
    if (type != 'search'){
        $.address.parameter('searchPage', null);
        macademia.queryString.searchPage = null;
    }
};
// hides and shows appropriate divs in right content div
macademia.showDivs = function(type){
    var queries = ['searchBox','interestId','personId','requestId'];
    for(var i = 0; i < queries.length; i++){
        if (queries[i].indexOf(type) < 0){
            var divName = "#" + queries[i] + "Div";
            $(divName).hide();
        }else{
            var divName = "#" + queries[i] + "Div";
            if ($.address.parameter(queries[i])){
                $(divName).show();
            }else{
                $(divName).hide();
            }

        }
    }
};
// clears the instructions after the page has been changed by user (or if user enters exact url)
macademia.clearInstructions = function(){
    if($.address.parameter('searchBox') || $.address.parameter('personId') || $.address.parameter('interestId') || $.address.parameter('requestId')){
        $("#instruct_list").hide();
    }
};
// submits the search query from the url
macademia.submitSearch = function(){
    console.log(1);
    if(($.address.parameter('searchPage') != macademia.queryString.searchPage || $.address.parameter('searchBox') != macademia.queryString.searchBox || $('#searchResults').is(':empty')) && ($.address.parameter('searchBox') != undefined || macademia.queryString.searchBox != null)){
        if($.address.parameter('searchBox') != undefined){
            var searchBox = $.address.parameter('searchBox');
            var search = searchBox.replace('+', ' ');
            var institutions = $.address.parameter('institutions');
            var page = $.address.parameter('searchPage').split('_');
            var type = page[0];
            var number = page[1];
            $('#searchBoxDiv').load(
                '/Macademia/search/search',
                {searchBox:search,
                institutions: institutions,
                type: type,
                pageNumber: number}
            );
        }else{
            $('#searchBoxDiv').empty();
        }
        macademia.queryString.searchBox = searchBox;
    }
};

macademia.makeActionUrl = function(controller, action) {
    return "/Macademia/" + controller + "/" + action;
};

macademia.setupModal = function(modalDialog, trigger, url, depModule, fnString) {
    $(modalDialog).jqm({modal: false});
    $(trigger).click(function(){
        $(modalDialog).load(
                "/Macademia/" + url,
                    function(responseText, textStatus, xmlHttpRequest) {
                        $.deps.load(depModule, function() {
                            try {
                                eval(fnString);
                            } catch (error) {
                                debug.log('evaluation of ' + fnString + ' failed: ' + error);
                            }
                         });
                    }
                );
        $(modalDialog).jqmShow();
    });
};