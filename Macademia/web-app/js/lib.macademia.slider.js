/**
 * Provides the Autocomplete widget for macademia.
 */



var macademia = macademia || {};

/**
 * Example of usage for the search box
 */
macademia.slider = {};

macademia.slider.initSlider = function() {
    var density = $.address.parameter('density') || 3;
    $('#slider .widget').slider({
			value: density,
			min: 1,
			max: 5,
			slide: function(event, ui) {
			},
            start: function(event, ui) {
                // all actions here upon slide starting.
			},
            change: function(event, ui) {
                $.address.parameter('density', ui.value);
                $.address.update();
			},
            stop: function(event, ui) {
                // all actions here happen when user stops sliding
			}
	});
};

macademia.slider.changeSlider = function(value) {
    $('#slider .widget').slider('value', value);
}
