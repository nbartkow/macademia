/**
 * Provides the Autocomplete widget for macademia.
 */



var macademia = macademia || {};

/**
 * Example of usage for the search box
 */
macademia.slider = {};

macademia.slider.initSlider = function() {
    $('#slider').slider({
			range: "min",
			value: 25,
			min: 1,
			max: 100,
			slide: function(event, ui) {
                // all actions happen here upon sliding.
				$("#amount").val(ui.value);
                var root = $.address.parameter('nodeId');
                var colleges = $.address.parameter('institutions');
                var rootId = root.slice(2)
                var type = macademia.getType(root);
                var url = '/Macademia/'+type+'/json';
                var params = {maxPerson : ui.value, institutions : colleges, id : rootId};
                $.ajax({
                    url : url,
                    data : params
                });
                macademia.rgraph.refresh();       //I don't think this part is working
			},
            start: function(event, ui) {
                // all actions here upon slide starting.
			},
            change: function(event, ui) {
                // all actions here happen when user stops sliding or value is changed (similiar to stop but more)
                //MEG: This is where the address change should happen
			},
            stop: function(event, ui) {
                // all actions here happen when user stops sliding
			}
	});
    $("#amount").val($("#slider").slider("value"));


         // best case scenario:
          // graph changes as user slides, but address does not. address changes only when slide has stopped.

    /*

    if 1: JUST MAKE THE ADRESS CHANGE FOR CHANGE. that's it. update the address, what i did with autocomplete is below BUT meg should be able to help you int this regard.

    if 2: change "change:" and "slide". Find some way to change finalizeGraph.


    how autocomplete handles     - upon 'selection', everything happens

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
     */


    /*

    WHAT TO DO . THIS IS WHERE WE WANT STUFF TO GO DOWN.


    something calls finalizeGraph. We want to change that upon slide:


     */

};
