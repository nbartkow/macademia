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
    $.address.change(function(event){
          var originalWidth =  680;
          var originalHeight = 660;
          var originalDistance = 150;
          var currentHeight = $("#infovis").height();
          var bodyWidth = $("body").width();
          if ($.address.value().indexOf("hide") < 0){
				$("#rightDiv").animate({width: "320"}, "slow");
				$("#infovis").animate({right: "320"}, "slow", function() {
                    $("#rightDiv > *").show();
                    $("#show").hide();
                });
                if (macademia.mycanvas){
                    if(Math.min((bodyWidth-320) , currentHeight) == (bodyWidth - 320)){
                        var newWidth = 0.95 * (bodyWidth - 320);
                        var newHeight = originalHeight * newWidth / originalWidth;
                    }else{
                        newHeight = 0.95 * currentHeight;
                        newWidth = originalWidth * newHeight / originalHeight;
                    }
                    $("#mycanvas").css({"width":newWidth, "height": newHeight});
                    macademia.distance = originalDistance * newHeight / originalHeight;
                    macademia.mycanvas.resize(newWidth, newHeight);
                    macademia.rgraph.config.levelDistance = macademia.distance;
                    macademia.rgraph.refresh();
                }
          }else if ($.address.value().indexOf("hide") >= 0){
				$("#rightDiv > *").hide();
				$("#rightDiv").animate({width: "0"}, "slow");
				$("#infovis").animate({right: "0"}, "slow");
				$("#show").show();
                if (macademia.mycanvas){
                    if(Math.min(bodyWidth , currentHeight) == bodyWidth){
                        var newWidth = 0.95 * bodyWidth;
                        var newHeight = originalHeight * newWidth / originalWidth;
                    }else{
                        newHeight = 0.95 * currentHeight;
                        newWidth = originalWidth * newHeight / originalHeight;
                    }
                    $("#mycanvas").css({"width":newWidth, "height": newHeight});
                    macademia.distance = originalDistance * newHeight / originalHeight;
                    macademia.mycanvas.resize(newWidth, newHeight);
                    macademia.rgraph.config.levelDistance = macademia.distance;
                    macademia.rgraph.refresh();
                }
		  }
          if (macademia.rgraph && $.address.parameter('nodeId')!= undefined) {
                var param = $.address.parameter('nodeId');
                macademia.rgraph.onClick(param);
          }
    });
    $(window).resize(function(){
            
          var originalWidth =  680;
          var originalHeight = 660;
          var originalDistance = 150;
          var currentHeight = $("#infovis").height();
          var currentWidth = $("#infovis").width();
          if(Math.min(currentWidth , currentHeight) == currentWidth){
                    var newWidth = 0.95 * currentWidth;
                    var newHeight = originalHeight * newWidth / originalWidth;
          }else{
                    newHeight = 0.95 * currentHeight;
                    newWidth = originalWidth * newHeight / originalHeight;
          }
          $("#mycanvas").css({"width":newWidth, "height": newHeight});
          macademia.distance = originalDistance / originalHeight * newHeight;
          macademia.mycanvas.resize(newWidth, newHeight);
          macademia.rgraph.config.levelDistance = macademia.distance;
          macademia.rgraph.refresh();
        
    })
    $('a').click(function(event) {
        $.address.value($(this).attr('href'));
        $.address.update();

    });

};
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
			$("#show").hide();
            $.address.autoUpdate(false);
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