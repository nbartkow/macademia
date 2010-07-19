/**
 * Provides the Autocomplete widget for macademia.
 */
(function($) {

    $.fn.macademiaAutocomplete = function(settings, url) {
        var cache = {};
        
        var config = {
            source : function(request, response) {
                if (request.term in cache) {
                    response(cache[request.term]);
                    return;
                }


				$.ajax({
					url: url,
					dataType: "json",
					data: request,
					success: function( data ) {
                        var result = [];
                        for (var i =0; i < data.length; i++) {

                            // change the type of Collaborator Request from "collaboratorrequest" to "request" (for aesthetic purposes)
                            if (data[i][2] == "collaboratorrequest"){
                                data[i][2] = "request"    
                            }
                            if (data[i][2] == "institution"){
                                result.push({

                                    label : data[i][1],
                                    data : data[i]
                                })
                            } else {
                                result.push({

                                    label : data[i][1] + " (" + data[i][2] + ")",
                                    data : data[i]
                                })
                            }
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

    $.fn.editAutocomplete = function(settings, url) {
        function split(val) {
                  return val.split(/,\s*/);
              }
        function extractLast(term) {
                  return split(term).pop();
              }
        var cache = {};

        var config = {
            source: function(request, response) {
                    if (request.term in cache) {
                      response(cache[request.term]);
                      return;
                    }
                      $.getJSON(url, {
                          term: extractLast(request.term)
                      }, function( data ) {
                        var result = [];
                        for (var i =0; i < data.length; i++) {
                            result.push({
                                label : data[i][1],
                                data : data[i]
                            })
                        }
                        cache[request.term] = result;
						response(result);
					});
                  }
        };

        if (settings) $.extend(config, settings);
        this.autocomplete(config);

        return this;

    };

})(jQuery);


var macademia = macademia || {};      

/**
 * Example of usage for the search box
 */
macademia.autocomplete = {};

macademia.autocomplete.initSearch = function() {
    $("#searchBox").macademiaAutocomplete(
        {
            multiple : false,
            select : function (event, ui) {
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
        }, "/Macademia/autocomplete");
};

macademia.autocomplete.split = function(val) {
    return val.split(/,\s*/);
};

macademia.autocomplete.extractLast = function(term) {
    return split(term).pop();
};

macademia.autocomplete.initEditProfile = function() {
    $("#interests").editAutocomplete(
        {
          multiple : true,
          search: function() {
              // custom minLength
              var term = macademia.autocomplete.extractLast(this.value);
              if (term.length < 1) {
                  return false;
              }
          },
          focus: function() {
              // prevent value inserted on focus
              return false;
          },
          select: function(event, ui) {
              var terms = macademia.autocomplete.split( this.value );
              // remove the current input
              terms.pop();
              // add the selected item
              terms.push( ui.item.value );
              // add placeholder to get the comma-and-space at the end
              terms.push("");
              this.value = terms.join(", ");
              return false;
          }
        }, "/Macademia/autocomplete/index?klass=interest");
    };

macademia.autocomplete.initEditRequest = function() {
    $("#keywords").editAutocomplete(
        {
          multiple : true,
          search: function() {
              // custom minLength
              var term = macademia.autocomplete.extractLast(this.value);
              if (term.length < 1) {
                  return false;
              }
          },
          focus: function() {
              // prevent value inserted on focus
              return false;
          },
          select: function(event, ui) {
              var terms = macademia.autocomplete.split( this.value );
              // remove the current input
              terms.pop();
              // add the selected item
              terms.push( ui.item.value );
              // add placeholder to get the comma-and-space at the end
              terms.push("");
              this.value = terms.join(", ");
              return false;
          }
        }, "/Macademia/autocomplete/index?klass=interest");
};

macademia.autocomplete.initCollegeSearch = function() {
    $("#collegeSearchAuto").macademiaAutocomplete(
        {
            multiple : true,
            select : function (event, ui) {
                var name = ui.item.data[1];
                $('#collegeSearchAuto').val(name);
                window.setTimeout(function () {
                        $("#collegeSearch").blur();
                    }, 100);
                // simulate a click of the add college button
                $("#addCollege").click();
                return false;
            }
        }, "/Macademia/autocomplete/index?klass=institution");
};