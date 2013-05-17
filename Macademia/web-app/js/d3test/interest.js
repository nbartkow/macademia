/**
 * An interest is a single labeled sphere.
 */

var MC = (window.MC = (window.MC || {}));

MC.interest = function() {
    function hueToColor(h) {
        return d3.hsl(h * 359, 0.8, 0.8);
    }

    function interest(g) {

        var g = g.append('g')
            .attr('class', interest.getCssClass())
            .attr('transform', function (d, i) {
                var cx = interest.getOrCallCx(d, i);
                var cy = interest.getOrCallCy(d, i);
                return 'translate(' + cx + ', ' + cy + ')';
            });

        var c = g.append('circle')
                .attr('fill', function (d) { return hueToColor(d.color); })
                .attr('r', interest.getR());

        interest.getOrCallOnHover().forEach(function (v) {
                g.on('mouseover', v[0]);
                g.on('mouseout', v[1]);
            });

        var l = MC.label()
            .setText(interest.getText())
            .setAlign('middle');

        g.call(l);

        return g;
    }

    MC.options.register(interest, 'text', function (d) { return d.name; });
    MC.options.register(interest, 'cx', 100);
    MC.options.register(interest, 'cy', 100);
    MC.options.register(interest, 'r', 10);
    MC.options.register(interest, 'onHover', [], MC.options.TYPE_LIST);
    MC.options.register(interest, 'cssClass', 'interest');

    return interest;
};