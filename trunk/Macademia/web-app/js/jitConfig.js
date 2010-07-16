var macademia = macademia || {};
macademia.rootId;
macademia.distance = 150;
macademia.refreshNeeded = true;



macademia.makeJsonUrl = function(type, id) {
    return "/Macademia/" + type + "/json/" + id + "?institutions="+$.address.parameter('institutions');
};


macademia.checkBrowser = function() {
    if (!$.browser.mozilla && !$.browser.safari) {
        alert('This website will not work properly on Internet Explorer.  Please use Firefox or Safari');
    }
};

macademia.init = function(rootType,id){
    macademia.checkBrowser();

    if(macademia.rgraph){
        $("#infovis").empty();
    }
    macademia.rootId = id;
    if (rootType != 'person' && rootType != 'interest' && rootType != 'request'){
        alert('unknown root type: ' + rootType);
        return false;
    }
    var json = null;
    $.getJSON(macademia.makeJsonUrl(rootType,id),function(data){
        json = data;
            macademia.rgraph = new $jit.RGraph({
                  'injectInto': 'infovis',
                  'width': 680,
                  'height': 660,
                withLabels : true,
                levelDistance: macademia.distance,
                background: {
                          levelDistance: macademia.distance,
                          numberOfCircles: 2,
                          CanvasStyles: {
                            strokeStyle: '#6A705D'
                          }
                        },

            Node: {
                'overridable': true,
                'type': 'circle',
                'color': '#777777', /*'#ccddee'*/
                'width' : '4px'
            },
            Edge: {
                'overridable':true,
                'color': 'blue'//'#772277'
            },
            //interpolation type, can be linear or polar
            interpolation: 'polar',
            //parent-children distance


            //Set node/edge styles

            onPlaceLabel: function(domElement, node) {
                //alert('here 1');
                $(domElement).attr('alt','/Macademia/'+node.data.type+'/tooltip/'+node.data.unmodifiedId);
                var d = $(domElement);
                var left = parseInt(d.css('left'));

                var w = domElement.offsetWidth;
                d.css('width', '');
                d.css('height', '');
                d.css('left', (left - w /2) + 'px');
                //alert('here 2');

            },
            //Add a controller to make the tree move on click.
            onCreateLabel: function(domElement, node) {
                //alert('here 3');
                var d = $(domElement);
                d.html(node.name);
                d.css('z-index', 10);
                d.css('opacity', 0.8);
                d.css('white-space', 'nowrap');
                d.css('margin-top', '3px');
                d.css('font-size', '14px');
                d.css('background-color','transparent');
                $(d).mouseover(function() {
                    $(this).css('font-weight', 600);
                    $(this).css('opacity', 0.75);
                    $(this).css('z-index', 50);
                    $(this).css('background-color', '#A2AB8E');
                    macademia.highlightAdjacenciesOn(node);
                    macademia.rgraph.refresh();

                });
                $(d).mouseout(function() {
                    $(this).css('font-weight', 'normal');
                    $(this).css('opacity', 0.8);
                    $(this).css('z-index', 10);
                    $(this).css('background-color','transparent');
                    if (macademia.refreshNeeded){
                        macademia.highlightAdjacenciesOff(node);
                        macademia.rgraph.refresh();
                    }
                });
                $(d).click(function() {
                    if(macademia.refreshNeeded){
                        if(macademia.rootId != parseFloat(node.id.substr(2))){
                            macademia.refreshNeeded = false;
                            macademia.highlightAdjacenciesOff(node);
                        }
                        macademia.navInfovis(node);
//                      rgraph.onClick(node.id);
                    }

                });



                d.qtip({
                    content:{
                        text:'loading...'
                    },
                    api: {
                        beforeShow:function() {                   
                            var url = this.elements.target.attr('alt');
                            if (url != '') {
                                var params = {};
                                if (node.data.type == 'person'|| node.data.type == 'request') {
                                    var rootId =macademia.rgraph.graph.getNode(macademia.rgraph.root).id;
                                    params = {node : node.id, root: rootId};
                                }
                                this.loadContent(url, params, 'post');
                            }
                        },
                        onContentLoad:function() {
                            this.elements.target.attr('alt', '');
                        }
                    },
                    style:{
                        tip:{
                            corner:'topLeft',
                            size:{
                                x:'700',
                                y:'300'
                            }

                        }
                    },
                    position:{
                        adjust : {
                            screen : true
                        }
                    }

                });


            },
            onBeforeCompute:function(node) {
                if (node.data.unmodifiedId) {
                    macademia.nextNode = node;
                }
            },
            
            //morph to new data after anim and if user has clicked a person node
            onAfterCompute:function() {
                if (macademia.nextNode) {
                    macademia.rootId = macademia.nextNode.data.unmodifiedId;
                    var rootType = macademia.nextNode.data.type;
                    $.getJSON(macademia.makeJsonUrl(rootType, macademia.rootId), function(data) {
                        macademia.checkBrowser();
                        macademia.rgraph.op.morph(data, {
                            type:'replot',
                            duration:100,
                            hideLabels:false
                        });
                    });
                }
                macademia.nextNode = null;
                macademia.refreshNeeded = true;
            }

        });
        //load tree from tree data.
        macademia.rgraph.loadJSON(json);
        //compute positions and plot
        macademia.resizeCanvas($("#infovis").width());
        // $('#infovis').draggable();
        
    })
};