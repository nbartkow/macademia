var macademia = macademia || {};

//sets the sidebar's visibility according to original status.  Initializes jit visualization
macademia.pageLoad = function(){
            macademia.initialSettings();
            macademia.nav();


};
//initial settings for page load
macademia.initialSettings = function(){
            // address only updates manually when a link/node is clicked
            $.address.autoUpdate(false);
            if ($.address.parameter('navVisibility').indexOf('true')>=0){
			    $("#show").hide();
            }else if ($.address.parameter('navVisibility').indexOf('false')>=0){
                $("#rightDiv > *").hide();
                $("#rightDiv").css("width", "0");
                $("#infovis").css("right", "0");
            }
            var param = $.address.parameter('nodeId');
            var type = macademia.getType(param);
            var id = parseFloat(param.substr(2));
            macademia.init(type,id);
};
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
macademia.updateSidebar = function(node){
    // does rootInfo exist?
    $("#rootInfo").empty();
    $("#rootInfo").html(node.name+" ("+node.data.department+") "+node.data.email);

};
// changes the address when a node is clicked
macademia.navInfovis = function(node) {
    $.address.parameter('nodeId', node.id);
    $.address.update();
};
// click navigation for the rightDiv
macademia.nav = function(){
    macademia.collegeFilter();
    $.address.change(function(){
         macademia.backSupport();
    });
    $(window).resize(function(){
          if (macademia.mycanvas){
            macademia.resizeCanvas($("#infovis").width());
          }
    });
    $("a").address(function() {
        var url = $(this).attr('href'); 
        if (url.indexOf("#")== 0){
           macademia.changeQueryString(url);
        }else{
            $.address.value(url);
        }
        $.address.update();
    });
    /*$("#hide").click(function(event){
        $.address.parameter('navVisibility', 'false');
        $.address.update();
    });
    $("#show").click(function(event){
        $.address.parameter('navVisibility', 'true');
        $.address.update();
    });*/

};
// changes the Query string according link's href
macademia.changeQueryString = function(query){
    var queryString = query.substr(2);
    var params = queryString.split('&');
    for (var i in params){
       var paramValue = params[i].split('=');
       $.address.parameter(paramValue[0],paramValue[1]);
    }
};
// controller for the select colleges filter
macademia.collegeFilter = function() {
    $('#modalDialog').jqm({trigger: '#collegeFilterTrigger'});
    $(".delete").click(function(event) {
        $(this).parents("li").animate({opacity: "hide" }, "normal")
    });
    $("#clear").click(function(event) {
        $("#selectedColleges > ul > li").hide();
    });
    $("#add").click(function(event) {
        $("#selectedColleges > ul > li").show();
    });
};
/* Supporting address changing functions and toggling functions for the show and hide toggle
*  and signals graph changes when back button or search is used.
*/
macademia.backSupport = function(){
          macademia.showHide();
          if (macademia.rgraph){
              var param = $.address.parameter('nodeId');
              if (macademia.rgraph.graph.getNode(param).data) {
                macademia.rgraph.onClick(param);
              }else{
                var url = ($.address.baseURL() + $.address.path() + "#" + $.address.value());
                location = (url);
                location.reload();
              }
          }
};
// controls the show and hide options
macademia.showHide = function(){
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
				$("#rightDiv").animate({width: "0"}, "slow");
				$("#infovis").animate({right: "0"}, "slow");
				$("#show").show();
              // resize visual
                if (macademia.mycanvas){
                    macademia.resizeCanvas($("body").width());
                }
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