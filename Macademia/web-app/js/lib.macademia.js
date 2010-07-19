var macademia = macademia || {};
macademia.unfocusedEdgeColor = "#999";
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
                if (adj.data.$color != macademia.unfocusedEdgeColor && adj.data.$color != undefined){
                    if(adj.data.$color)
                    adj.data.$colorB = adj.data.$color;
                    adj.data.$color = macademia.unfocusedEdgeColor;
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
        $(adjN).css('font-weight', 600);
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
                if(adj.data.$colorB != macademia.unfocusedEdgeColor && adj.data.$colorB != undefined){
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
    macademia.loginShowHide();
    macademia.initiateGraph();
    macademia.nav();

    macademia.setupModal("#registerDialog", "#registerButton", "account/modalcreateuser/", 'nimble-login-register', "macademia.initializeModalRegister()");    
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
            if(macademia.queryString.institutions != "all"){
                macademia.initiateCollegeString(macademia.queryString.institutions);
            }
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
    } else if (type == 'request' && $.address.parameter('navFunction') != 'request') {
        $.address.parameter('navFunction','request');
    }
    macademia.sortParameters(type,rootId.substr(2));
    $.address.update();
};


// click navigation for the rightDiv
macademia.nav = function() {
//    macademia.modalLogin();
//    macademia.modalRegister();
//    macademia.setupModal("#loginDialog", "#loginButton", "account", "login", {}, 'nimble-login-register', "macademia.initializeModalLogin()");
    macademia.clearSearch();
    macademia.wireupCollegeFilter();
    $.address.change(function() {
        macademia.showHide();
        macademia.updateNav();
        macademia.changeGraph();
        macademia.changeDisplayedColleges();
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
    $('#loginDialog').jqm({modal: false, ajax:"/Macademia/account/login"});
    $('#loginButton').click(function(){
        $('#loginDialog').jqmShow()
    });
};


// controls the show and hide options
macademia.showHide = function() {
    if ($.address.parameter('navVisibility') != macademia.queryString.navVisibility) {
        var navVisibility = $.address.parameter('navVisibility');
        if (navVisibility == 'true' && !$("#wrapper").is(":visible")) {
            $("#sidebar").animate({width: "320"}, "slow");
            $("#infovis").animate({right: "320"}, "slow", function() {
                $("#sidebar > *").show();
            });
            // resize visual
            if (macademia.rgraph) {
                macademia.resizeCanvas($("body").width() - 320);
            }
        } else if (navVisibility == 'false' && $("#wrapper").is(":visible")) {
            $("#sidebar > *").hide();
            if (macademia.rgraph) {
                $("#sidebar").animate({width: "0"}, "slow");
                $("#infovis").animate({right: "0"}, "slow");
            } else {
                // on page load rightDiv will not slide over
                $("#sidebar").css('width', '0');
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
    if ($.address.parameter('nodeId') != macademia.queryString.nodeId && $.address.parameter('institutions') == macademia.queryString.institutions) {
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
    }else if($.address.parameter('institutions') != macademia.queryString.institutions){
        macademia.initiateGraph();
    }
};
// resizes canvas according to original dimensions
macademia.resizeCanvas = function(currentWidth) {
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
        macademia.rgraph.canvas.resize(currentWidth, currentHeight);
        macademia.rgraph.canvas.scale(newHeight/originalHeight,newWidth/originalWidth);
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
         $('#personIdDiv').load("/Macademia/person/show/" + rootId.slice(2));
     }else if (navFunction == 'request'){
         var rootId = $.address.parameter('nodeId');
         $('#requestIdDiv').load("/Macademia/request/show/" + rootId.slice(2));
         macademia.queryString.requestId = $.address.parameter('requestId');
     }else if (navFunction == 'interest'){
         var rootId = $.address.parameter('nodeId');
         $('#interestIdDiv').load("/Macademia/interest/show/" + rootId.slice(2));
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
    if(($.address.parameter('institutions') != macademia.queryString.institutions || $.address.parameter('searchPage') != macademia.queryString.searchPage || $.address.parameter('searchBox') != macademia.queryString.searchBox || $('#searchResults').is(':empty')) && ($.address.parameter('searchBox') != undefined || macademia.queryString.searchBox != null)){
        if($.address.parameter('searchBox') != undefined){
            var searchBox = $.address.parameter('searchBox');
            var search = searchBox.replace('+', ' ');
            var institutions = $.address.parameter('institutions');
            var page = $.address.parameter('searchPage').split('_');
            var type = page[0];
            var number = page[1];
            var url = '/Macademia/search/search';
            if(type != 'all'){
                url = '/Macademia/search/deepsearch';
            }
            $('#searchBoxDiv').load(
                url,
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

macademia.initializeModalRegister = function() {
  macademia.upload.init();
  macademia.links.init();
  $("#passwordpolicy").hide();
  $("#passwordpolicybtn").bt({
      contentSelector: $("#passwordpolicy"),
      width: '350px',
      closeWhenOthersOpen: true,
      shrinkToFit: 'true',
      positions: ['right', 'top', 'left'],
      margin: 0,
      padding: 6,
      fill: '#fff',
      strokeWidth: 1,
      strokeStyle: '#c2c2c2',
      spikeGirth: 12,
      spikeLength:9,
      hoverIntentOpts: {interval: 100,
                        timeout: 1000}
  });
  if($('#username')) $('#username').blur(function() {$('#pass').focus();});
  if($('#pass')) $('#pass').blur(function() {$('#passConfirm').focus();});
 // $('.password').pstrength();
  $('.password').keyup();
    nimble.createTip('usernamepolicybtn', 'Username Policy', 'Choose a good username');
    nimble.createTip('passwordpolicybtn', 'Password Policy', 'Choose a good password');
  $('#edit_profile_container form').submit(function() {
      macademia.links.serialize();
      var a = new Array();
      var formData = $(this).serialize();
      a = formData.split('&');
      var pass = a[1].substring(5);
      var confirm = a[2].substring(12);
      var formCheck = true;
      if (pass!=confirm) {
          alert("passwords don't match")
          formCheck=false;
      }
      if (pass.length<6) {
          alert("password is not long enough")
          formCheck=false;
      }
      
      alert(pass);
      jQuery.ajax({
          url: '/Macademia/account/saveuser/',
          type: "POST",
          data: $(this).serialize(),
          dataType: "text",
          success: function(data) {
              alert('success: ' + data);


          }
        
      });
      return false;
  });

};

macademia.loginShowHide = function() {
		$("#login").hide();
		$("#login_link").click(function(event) {
			$("#login").slideToggle();
		});
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
                                alert('evaluation of ' + fnString + ' failed: ' + error);
                            }
                         });
                    }
                );
        $(modalDialog).jqmShow();
    });
};

macademia.trim = function(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
};

// Code for managing the links on the edit page.
macademia.links = {};
macademia.links.init = function() {
    var linkHtml = $(".personLinks .linkValues").html();
    $(".personLinks .addLink").click(
            function () {macademia.links.addNewLink("name", "url")}
        );
    macademia.links.unserialize();
};

macademia.links.addNewLink = function(linkName, linkUrl) {
    var newDiv = $(".personLinks .customLinkTemplate").clone();
    newDiv.removeClass("customLinkTemplate");
    $(".personLinks .addLink").before(newDiv);
    newDiv.find(".removeLink").click(
                function () {
                    $(this).parent().parent().remove();
                    return false;
                }
            );
    newDiv.show();
};

macademia.links.serialize = function() {
    var linkStr = "";
    $(".personLinks .standardLink,.customLink").each(function () {
        var name;
        // handles custom and default named fields separately.
        if ($(this).find('.linkField input').length > 0) {
            name = $(this).find('.linkField input').val();
        } else {
            name = $(this).find('.linkField').html();
        }
        var value= $(this).find('.linkValue input').val();
        if (name && name != 'name' && value) {
            linkStr += "<li><a href=\"" + encodeURI(value) + "\">";
            linkStr += macademia.htmlEncode(name) + "</a>\n";
        }
    });
    alert('setting value to ' + linkStr);
    $("#edit_pf input[name='links']").val(linkStr);
};
macademia.links.unserialize = function() {
    var linksStr =$("#edit_pf input[name='links']").val();
    var linksDom = $(linksStr);
};

macademia.htmlEncode = function(value){
  return $('<div/>').text(value).html();
}

macademia.htmlDecode = function(value){ 
  return $('<div/>').html(value).text();
}