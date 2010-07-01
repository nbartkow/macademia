/**
 * Provides the Autocomplete widget for macademia.
 */
(function($) {

    $.fn.macademiaAutocomplete = function(settings) {
        var cache = {};
        
        var config = {
            source : function(request, response) {
                if (request.term in cache) {
                    response(cache[request.term]);
                    return;
                }


				$.ajax({
					url: "/Macademia/autocomplete",
					dataType: "json",
					data: request,
					success: function( data ) {
                        var result = [];
                        for (var i =0; i < data.length; i++) {
                            result.push({
                                label : data[i][1] + " (" + data[i][2] + ")",
                                data : data[i]
                            })
                        }
                        cache[request.term] = result;
						response(result);
					}
				})}
        };

        if (settings) $.extend(config, settings);
        this.autocomplete(config);

        return this;

    };

})(jQuery);


/**
 * Example of usage for the search box
 */
$().ready(
        function () {
            $("#searchBox").macademiaAutocomplete(
                {
                    multiple : false,
                    select : function (event, ui) {
                        console.log("ui.item.data is " + ui.item.data);
                        var id = ui.item.data[0];
                        var name = ui.item.data[1];
                        var type = ui.item.data[2];
                        $.address.parameter('nodeId', type.substring(0, 1) + "_" + id);
                        $.address.parameter('navFunction', type);
                        macademia.sortParameters(type, id);
                        $.address.update();
                        $("#searchBox").val("");
                        window.setTimeout(function () {
                                $("#searchBox").blur();
                            }, 100);

                        return false;
                    }
                });
        }
    );