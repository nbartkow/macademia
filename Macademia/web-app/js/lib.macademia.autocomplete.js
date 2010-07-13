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


/**
 * Example of usage for the search box
 */
$().ready(
        function () {
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
        }
    );
$().ready(
        function () {
            function split(val) {
                  return val.split(/,\s*/);
              }
              function extractLast(term) {
                  return split(term).pop();
              }
            $("#interests").editAutocomplete(
                {
                  multiple : true,
                  search: function() {
                      // custom minLength
                      var term = extractLast(this.value);
                      if (term.length < 1) {
                          return false;
                      }
                  },
                  focus: function() {
                      // prevent value inserted on focus
                      return false;
                  },
                  select: function(event, ui) {
                      var terms = split( this.value );
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
        }
    );

$().ready(
        function () {
            function split(val) {
                  return val.split(/,\s*/);
              }
              function extractLast(term) {
                  return split(term).pop();
              }
            $("#keywords").editAutocomplete(
                {
                  multiple : true,
                  search: function() {
                      // custom minLength
                      var term = extractLast(this.value);
                      if (term.length < 1) {
                          return false;
                      }
                  },
                  focus: function() {
                      // prevent value inserted on focus
                      return false;
                  },
                  select: function(event, ui) {
                      var terms = split( this.value );
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
        }
    );

$().ready(
        function () {
            $("#collegeSearchAuto").macademiaAutocomplete(
                {
                    multiple : true,
                    select : function (event, ui) {
                        var name = ui.item.data[1];
                        $('#collegeSearchAuto').val(name);
                        window.setTimeout(function () {
                                $("#collegeSearch").blur();
                            }, 100);

                        return false;
                    }
                }, "/Macademia/autocomplete/index?klass=institution");
        }
    );