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
          if ($.address.value().indexOf("show") >= 0){
				$("#rightDiv").animate({width: "320"}, "slow");
				$("#infovis").animate({right: "320"}, "slow", function() {
                    $("#rightDiv > *").show();
                    $("#show").hide()
                });
          }else if ($.address.value().indexOf("hide") >= 0){
				$("#rightDiv > *").hide();
				$("#rightDiv").animate({width: "0"}, "slow");
				$("#infovis").animate({right: "0"}, "slow");
				$("#show").show();
		  }
    });
    $('a').click(function() {
        $.address.value($(this).attr('href'));
    });
};
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

    $(".clearDefault").blur(function(){
        var value = $(this).val();
        if(!value && $(this).data("clearedText")){
            $(this).val($(this).data("clearedText"));
        }

    });
};

macademia.pageLoad = function(){
			$("#show").hide();
			
};

macademia.collegeFilter = function(){
            $(".delete").click(function(event){
				$(this).parents("li").animate({opacity: "hide" }, "normal")
			});
			$("#editColleges").hide();
			$("#selectButton").click(function(event) {
				$("#editColleges").slideToggle();
				$("#selectButton").toggle();
			});
			$("#close > a").click(function(event) {
				$("#editColleges").slideToggle();
				$("#selectButton").slideToggle();
			});
			$("#clear").click(function(event) {
				$("#selectedColleges > ul > li").hide();
			});
			$("#add").click(function(event) {
				$("#selectedColleges > ul > li").show();
			});
}