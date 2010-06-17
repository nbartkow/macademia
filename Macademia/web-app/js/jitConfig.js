var macademia = macademia || {};
macademia.rootId;
macademia.distance = 150;



function makeJsonUrl(type, id) {
    return "/Macademia/" + type + "/json/" + id;
}


function checkBrowser() {
    if (!$.browser.mozilla && !$.browser.safari) {
        alert('This website will not work properly on Internet Explorer.  Please use Firefox or Safari');
    }
}

function init(rootType, id) {
    checkBrowser();
    
    //create our test label
    var testLabel = document.createElement('div');
    testLabel.id = "mytestlabel";
    testLabel.style.visibility = "hidden";
    testLabel.style.position = "absolute";
//    testLabel.style.width = "70px";
//    testLabel.style.height = "70px";
    document.body.appendChild(testLabel);

    macademia.rootId = id;
    if (rootType != 'person' && rootType != 'interest') {
        alert('unknown root type: ' + rootType);
        return false;
    }
    var json = null;
    $.getJSON(makeJsonUrl(rootType, id), function(data) {
        json = data;
        //Create a new canvas instance.
        var canvas = new Canvas('mycanvas', {
            //Where to inject the canvas. Any div container will do.
            'injectInto':'infovis',
            //width and height for canvas. Default's to 200.
            'width': 680,
            'height': 660,
            //draw in some circles to aid the visual connection of same distance nodes
            'backgroundCanvas':{
                'styles':{
                    'strokeStyle':'#6A705D'
                },
                'impl':{
                    'init':function() {
                    },
                    'plot':macademia.drawCircles
                }
            }
        });
        $("#mycanvas").css("margin","auto");
        if(Math.min($("#infovis").width(),$("#infovis").height()) == $("#infovis").width()){
                    var canvasW = 0.95 * $("#infovis").width();
                    var canvasH = 660 * canvasW / 680;


        }else{
                    canvasH = 0.95 * $("#infovis").height();
                    canvasW = 680 * canvasH / 660;


        }
        macademia.mycanvas = canvas;
        $("#mycanvas").css({"width":canvasW, "height": canvasH});
        macademia.distance = 150 / 660 * canvasH;
        canvas.resize(canvasW, canvasH);

        var rgraph = new RGraph(canvas, {
            interpolation : 'polar',
            withLabels : true,
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

            levelDistance: macademia.distance,
            //Set node/edge styles

            onPlaceLabel: function(domElement, node) {
                $(domElement).attr('alt','/Macademia/'+node.data.type+'/tooltip/'+node.data.unmodifiedId);                
                var d = $(domElement);
                var left = parseInt(d.css('left'));

                var w = domElement.offsetWidth;
                d.css('width', '');
                d.css('height', '');
                d.css('left', (left - w /2) + 'px');

            },
            //Add a controller to make the tree move on click.
            onCreateLabel: function(domElement, node) {
                var d = $(domElement);
                d.html(node.name);
                d.css('z-index', 10);
                d.css('opacity', 0.8);
                d.css('white-space', 'nowrap');
                d.css('margin-top', '3px');
                d.css('font-size', '14px');
                $(d).mouseover(function() {
                    $(this).css('opacity', 1);
                    $(this).css('z-index', 50);
                });
                $(d).mouseout(function() {
                    $(this).css('opacity', 0.8);
                    $(this).css('z-index', 10);
                });
                $(d).click(function() {
                    macademia.navInfovis(node);
//                  rgraph.onClick(node.id);

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
                                if (node.data.type == 'person') {
                                    var rootId =rgraph.graph.getNode(rgraph.root).id;
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
                ;
            },
            //morph to new data after anim and if user has clicked a person node
            onAfterCompute:function() {
                if (macademia.nextNode) {
                    macademia.rootId = macademia.nextNode.data.unmodifiedId;
                    var rootType = macademia.nextNode.data.type;
                    $.getJSON(makeJsonUrl(rootType, macademia.rootId), function(data) {
                        checkBrowser();
                        //alert(rgraph.fx.getLabelContainer().innerHTML);
                        //apparently destroying a label here means it doesn't
                        //get recreated after the morph.
                        //rgraph.fx.disposeLabel('p_17');

                        //have we considered summing (rgraph.op.sum()) graphs?
                        //with jquery's draggable plugin, and some clever
                        //zoom-tools, it could be an even more compelling visualization
                        //or...feature bloat?
                        rgraph.op.morph(data, {
                            type:'fade',
                            duration:1000,
                            hideLabels:false,
                            onComplete:function(){
                                macademia.updateSidebar(rgraph.graph.getNode(rgraph.root));
                                
                            }
                        });
                    });
                }
                macademia.nextNode = null;
            }
            
        });
        //load tree from tree data.
        rgraph.loadJSON(json);
        //compute positions and plot
        rgraph.refresh();
        macademia.rgraph = rgraph;
        // $('#infovis').draggable();
    });
}
