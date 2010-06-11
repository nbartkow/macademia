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

macademia.showHide = function(){
			$("#show").hide();
			$("#hide").click(function(event) {
				$("#rightDiv > *").toggle()
				$("#rightDiv").animate({width: "0"}, "slow")
				$("#infovis").animate({right: "0"}, "slow")
				$("#show").show();
			});
			$("#show").click(function(event) {
				$("#rightDiv").animate({width: "320"}, "slow");
				$("#infovis").animate({right: "320"}, "slow", function() {$("#rightDiv > *").toggle();$("#show").hide()});
			});
};