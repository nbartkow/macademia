var macademia = macademia || {};

//total number of institutions
macademia.institutions = 26;

//sets the sidebar's visibility according to original status.  Initializes jit visualization
macademia.pageLoad = function(){
            // address only updates manually when a link/node is clicked
            $.address.autoUpdate(false);
            macademia.showHide();
            macademia.initiateGraph();
            macademia.nav();


};
//calls the init function in jitConfig
macademia.initiateGraph = function(){
            var param = $.address.parameter('nodeId');
            var type = macademia.getType(param);
            var id = parseFloat(param.substr(2));
            macademia.init(type,id);
}
// determines the type according to the node's id (eg p_4)
macademia.getType = function(nodeId){
    if(nodeId.indexOf("p")>=0){
        return'person';
    }else{
        return 'interest';
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
macademia.clearSearch = function(){
    var textToClear =  "Search people or interests";
    $(".clearDefault").val(textToClear);
    $(".clearDefault").focus(function(){
        if($(this).val() == textToClear){
            $(this).data("clearedText", $(this).val());
            $(this).val('');
        }
    });
    $(".clearDefault").blur(function(event){
        var value = $(this).val();
        if(!value && $(this).data("clearedText")){
            $(this).val($(this).data("clearedText"));
        }
    });
};
/*old function... does nothing
macademia.updateSidebar = function(node){
    $("#rootInfo").empty();
    $("#rootInfo").html(node.name+" ("+node.data.department+") "+node.data.email);
};*/
// changes the address when a node is clicked
macademia.navInfovis = function(node) {
    $.address.parameter('nodeId', node.id);
    $.address.update();
};
// click navigation for the rightDiv
macademia.nav = function(){
    macademia.collegeFilter();
    $.address.change(function(){
         macademia.showHide();
         macademia.showInstitutions();
         if (macademia.rgraph){
             macademia.changeGraph();
         }
    });
    $(window).resize(function(){
          if (macademia.mycanvas){
            macademia.resizeCanvas($("#infovis").width());
          }
    });
    $("a").address(function() {
        var url = $(this).attr('href');
        if (url.length > 1){
            if (url.indexOf("#")== 0){
                macademia.changeQueryString(url);
            }else{
                $.address.value(url);
            }
                $.address.update();
        }
    });
    $("#select").click(function(event){
        macademia.collegeSelection();
    });
};
// changes the Query string according link's href
macademia.changeQueryString = function(query){
    var queryString = query.substr(3);
    var params = queryString.split('&');
    for (var i = 0; i < params.length; i++){
       var paramValue = params[i].split('=');
       $.address.parameter(paramValue[0],paramValue[1]);
    }
};
// controller for the select colleges filter
macademia.collegeFilter = function() {
    $('#modalDialog').jqm({trigger: '#collegeFilterTrigger', modal: true});
    $(".college a").click(function(event) {
        $(this).parents("li").animate({opacity: "hide" }, "normal")
    });
    $("#clear").click(function(event) {
        $("#selectedColleges > ul > li").hide();
    });
    $("#add").click(function(event) {
        $("#selectedColleges > ul > li").show();
    });
};
// Changes the visualization to new root node
macademia.changeGraph = function(nodeId){
              var param = $.address.parameter('nodeId');
              if (macademia.rgraph.graph.getNode(param).data) {
              // if the node is on the current graph
                macademia.rgraph.onClick(param);
              }else{
                  macademia.initiateGraph();
              }
};
// controls the show and hide options
macademia.showHide = function(){
          if (!$.address.parameter('navVisibility')){
                $.address.parameter('navVisibility','true');
                $.address.update();
          }
          if ($.address.parameter('navVisibility').indexOf('true')>=0 && !$("#wrapper").is(":visible")){
				$("#rightDiv").animate({width: "320"}, "slow");
				$("#infovis").animate({right: "320"}, "slow", function() {
                    $("#rightDiv > *").show();
                    $("#show").hide();
                });
              // resize visual
                if (macademia.mycanvas){
                    macademia.resizeCanvas($("body").width() - 320);
                }
          }else if ($.address.parameter('navVisibility').indexOf('false')>=0 && $("#wrapper").is(":visible")){
				$("#rightDiv > *").hide();
                if (macademia.mycanvas){
				    $("#rightDiv").animate({width: "0"}, "slow");
				    $("#infovis").animate({right: "0"}, "slow");
                }else{
                    // on page load rightDiv will not slide over
                    $("#rigthDiv").css('width','0');
                    $("#infovis").css('right','0');
                }
				$("#show").show();
              // resize visual
                if (macademia.mycanvas){
                    macademia.resizeCanvas($("body").width());
                }
		  }
};
// puts the selected colleges from the college filter into the address bar
macademia.collegeSelection = function(){
        var colleges = new Array();
        $("#selectedColleges li").each(function(){
            if ($(this).is(':visible')){
                colleges.push($(this).attr('id'));
            }
        });
        macademia.createInstitutionString(colleges);
        $.address.update();
        $('#modalDialog').jqmHide();
};
// takes an array of college ids and creates a string to stick in the url
macademia.createInstitutionString = function(collegeArray){
        var colleges = "";
        if (collegeArray.length == 0 || collegeArray.length == macademia.institutions){
            // if no colleges selected, default to all
            colleges = "all";
        }else{
            for(var i = 0; i < collegeArray.length; i++){
                if (i < collegeArray.length - 1){
                    colleges = colleges + collegeArray[i] + '+';
                }else{
                    colleges = colleges + collegeArray[i];
                }
            }
        }
        $.address.parameter('institutions', colleges);
};
// uses url to determine which colleges will be shown in the visualization (incomplete)
macademia.showInstitutions = function(){
       if ($.address.parameter('institutions') == 'all'){
           // do something
       }else{
           var colleges = $.address.parameter('institutions').split('+');
           // do something
       }
};
// resizes canvas according to original dimensions
macademia.resizeCanvas = function(currentWidth){
    if ($("#mycanvas").css('margin')!= 'auto'){
        $("#mycanvas").css('margin','auto');
    }
    var originalWidth =  680;
    var originalHeight = 660;
    var originalDistance = 150;
    var currentHeight = $("#infovis").height();
    if(Math.min(currentWidth , currentHeight) == currentWidth){
        var newWidth = 0.95 * currentWidth;
        var newHeight = originalHeight * newWidth / originalWidth;
    }else{
        var newHeight = 0.95 * currentHeight;
        var newWidth = originalWidth * newHeight / originalHeight;
    }
    if (newWidth != $("mycanvas").css("width")){
        $("#mycanvas").css({"width":newWidth, "height": newHeight});
        macademia.distance = originalDistance / originalHeight * newHeight;
        macademia.mycanvas.resize(newWidth, newHeight);
        if (macademia.rgraph){
            macademia.rgraph.config.levelDistance = macademia.distance;
            macademia.rgraph.refresh();
        }
    }
};
