var macademia = macademia || {};


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
macademia.nav = function(){
    $.address.change(function(){
         macademia.backSupport();
    });
    $(window).resize(function(){
          if (macademia.mycanvas){
            var originalWidth =  680;
            var originalHeight = 660;
            var originalDistance = 150;
            var currentHeight = $("#infovis").height();
            var currentWidth = $("#infovis").width();
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
                macademia.rgraph.config.levelDistance = macademia.distance;
                macademia.rgraph.refresh();
            }
          }
    });
    $('a').click(function(event) {
        $.address.value($(this).attr('href'));
        $.address.update();

    });
    $("#hide").click(function(event){
        $.address.parameter('navVisibility', 'false');
        $.address.update();
    });
    $("#show").click(function(event){
        $.address.parameter('navVisibility', 'true');
        $.address.update();
    });

};
macademia.backSupport = function(){
          var originalWidth =  680;
          var originalHeight = 660;
          var originalDistance = 150;
          var currentHeight = $("#infovis").height();
          var bodyWidth = $("body").width();
          if ($.address.parameter('navVisibility').indexOf('true')>=0 && !$("#wrapper").is(":visible")){
				$("#rightDiv").animate({width: "320"}, "slow");
				$("#infovis").animate({right: "320"}, "slow", function() {
                    $("#rightDiv > *").show();
                    $("#show").hide();
                });
              // resize visual
                if (macademia.mycanvas){
                    if(Math.min((bodyWidth-320) , currentHeight) == (bodyWidth - 320)){
                        var newWidth = 0.95 * (bodyWidth - 320);
                        var newHeight = originalHeight * newWidth / originalWidth;
                    }else{
                        var newHeight = 0.95 * currentHeight;
                        var newWidth = originalWidth * newHeight / originalHeight;
                    }
                    $("#mycanvas").css({"width":newWidth, "height": newHeight});
                    macademia.distance = originalDistance * newHeight / originalHeight;
                    macademia.mycanvas.resize(newWidth, newHeight);
                    macademia.rgraph.config.levelDistance = macademia.distance;
                    macademia.rgraph.refresh();
                }
          }else if ($.address.parameter('navVisibility').indexOf('false')>=0 && $("#wrapper").is(":visible")){
				$("#rightDiv > *").hide();
				$("#rightDiv").animate({width: "0"}, "slow");
				$("#infovis").animate({right: "0"}, "slow");
				$("#show").show();
              // resize visual
                if (macademia.mycanvas){
                    if(Math.min(bodyWidth , currentHeight) == bodyWidth){
                        var newWidth = 0.95 * bodyWidth;
                        var newHeight = originalHeight * newWidth / originalWidth;
                    }else{
                        var newHeight = 0.95 * currentHeight;
                        var newWidth = originalWidth * newHeight / originalHeight;
                    }
                    $("#mycanvas").css({"width":newWidth, "height": newHeight});
                    macademia.distance = originalDistance * newHeight / originalHeight;
                    macademia.mycanvas.resize(newWidth, newHeight);
                    macademia.rgraph.config.levelDistance = macademia.distance;
                    macademia.rgraph.refresh();
                }
		  }
          if (macademia.rgraph){
              var param = $.address.parameter('nodeId');
              if (macademia.rgraph.graph.getNode(param).data) {
                macademia.rgraph.onClick(param);
              }else{
                 location.reload(); 
              }
          }
}
macademia.navInfovis = function(node) {
    $.address.parameter('nodeId', node.id);
    $.address.update();
}
macademia.updateSidebar = function(node){
    $("#rootInfo").empty();
    $("#rootInfo").html(node.name+" ("+node.data.department+") "+node.data.email);

};


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

macademia.pageLoad = function(){
            if ($.address.parameter('navVisibility').indexOf('true')>=0){
			    $("#show").hide();
            }else if ($.address.parameter('navVisibility').indexOf('false')>=0){
                $("#rightDiv > *").hide();
                $("#rightDiv").css("width", "0");
                $("#infovis").css("right", "0");
            }
            $.address.autoUpdate(false);
            var param = $.address.parameter('nodeId');
            if(param.indexOf("p")>=0){
                var controller = 'person';
            }else{
                var controller = 'interest';
            }
            var id = parseFloat(param.substr(2));
            init(controller,id);
};

macademia.collegeFilter = function() {


    $().ready(function() {
        $('#modalDialog').jqm({trigger: '#collegeFilterTrigger'});
    });
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

macademia.startSearch = function() {
    $("#searchSubmitButton").click(function(event) {
        $(".hidable").hide();
        
    })

}