var macademia = macademia || {};

//total number of institutions
macademia.institutions = 26;

//holds query string values
macademia.queryString = {nodeId:'p_1', navVisibility:'true', navFunction:'search', institutions:'all', searchBox:null};

//sets the sidebar's visibility according to original status.  Initializes jit visualization
macademia.pageLoad = function() {
    // address only updates manually when a link/node is clicked
    $.address.autoUpdate(false);
    macademia.initialSettings();
    macademia.showHide();
    macademia.initiateGraph();
    macademia.nav();


};
//sets macademia.queryString values and initial page settings
macademia.initialSettings = function() {
    $("#show").hide();
    if ($.address.parameter('nodeId')) {
        macademia.queryString.nodeId = $.address.parameter('nodeId');
    } else {
        $.address.parameter('nodeId', macademia.queryString.nodeId)
    }
    if ($.address.parameter('navVisibility')) {
        macademia.queryString.navVisibility = $.address.parameter('navVisibility');
    } else {
        $.address.parameter('navVisibility', macademia.queryString.navVisibility)
    }
    if ($.address.parameter('navFunction')) {
        macademia.queryString.navFunction = $.address.parameter('navFunction');
    } else {
        $.address.parameter('navFunction', macademia.queryString.navFunction)
    }
    if ($.address.parameter('institutions')) {
        macademia.queryString.institutions = $.address.parameter('institutions');
    } else {
        $.address.parameter('institutions', macademia.queryString.institutions)
    }
    if ($.address.parameter('searchBox')) {
        macademia.queryString.searchBox = $.address.parameter('searchBox');
    }
};
//calls the init function in jitConfig
macademia.initiateGraph = function() {
    var param = $.address.parameter('nodeId');
    var type = macademia.getType(param);
    var id = parseFloat(param.substr(2));
    macademia.init(type, id);
};
// determines the type according to the node's id (eg p_4)
macademia.getType = function(nodeId) {
    if (nodeId.indexOf("p") >= 0) {
        return'person';
    } else {
        return 'interest';
    }
};
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
// clears the value of the text input box when clicked
macademia.clearSearch = function() {
    var textToClear = "Search people or interests";
    $(".clearDefault").val(textToClear);
    $(".clearDefault").focus(function() {
        if ($(this).val() == textToClear) {
            $(this).data("clearedText", $(this).val());
            $(this).val('');
        }
    });
    $(".clearDefault").blur(function(event) {
        var value = $(this).val();
        if (!value && $(this).data("clearedText")) {
            $(this).val($(this).data("clearedText"));
        }
    });
};
/*old function... does nothing
 macademia.updateSidebar = function(node){
 $("#rootInfo").empty();
 $("#rootInfo").html(node.name+" ("+node.data.department+") "+node.data.email);
 };*/
// changes the address when a node is clicked
macademia.navInfovis = function(node) {
    $.address.parameter('nodeId', node.id);
    $.address.update();
    // updates the sidebar to match the center node
    if (macademia.getType(node.id) == 'person') {
        document.getElementById('rightContent').innerHTML = '<p>to show user profile</p>';
    } else {
        document.getElementById('rightContent').innerHTML = '<p>to show interest page</p>';
    }
};
// click navigation for the rightDiv
macademia.nav = function() {
    macademia.modalLogin();
    macademia.modalRegister();
    macademia.collegeFilter();
    $.address.change(function() {
        if ($.address.parameter('navVisibility') != macademia.queryString.navVisibility) {
            macademia.showHide();
        }
        if ($.address.parameter('navFunction') != macademia.queryString.navFunction) {
            macademia.updateNav();
        }
        if ($.address.parameter('searchBox') != macademia.queryString.searchBox) {
            macademia.submitSearch();
        }
        if ($.address.parameter('institutions') != macademia.queryString.institutions) {
            macademia.showInstitutions();
        }
        if ($.address.parameter('nodeId') != macademia.queryString.nodeId) {
            macademia.changeGraph();
        }
    });
    $(window).resize(function() {
        if (macademia.mycanvas) {
            macademia.resizeCanvas($("#infovis").width());
        }
    });
    $("a").address(function() {
        var url = $(this).attr('href');
        if (url.length > 1) {
            if (url.indexOf("#") == 0) {
                macademia.changeQueryString(url);
            } else {
                $.address.value(url);
            }
            $.address.update();
        }
    });
    $("#select").click(function(event) {
        macademia.collegeSelection();
    });
    $('#searchForm').submit(function(event) {
        var search = ($('#searchBox').serialize()).split('=');
        if (search[1] != 'Search+people+or+interests') {
            $.address.parameter('searchBox', search[1]);
            $.address.update();
        }
        else {
            $.address.parameter('searchBox', null);
            $.address.update();
        }
        return false;
    });
};
// changes the Query string according link's href
macademia.changeQueryString = function(query) {
    var queryString = query.substr(3);
    var params = queryString.split('&');
    for (var i = 0; i < params.length; i++) {
        var paramValue = params[i].split('=');
        $.address.parameter(paramValue[0], paramValue[1]);
    }
};
// controller for the select colleges filter
macademia.collegeFilter = function() {
    $('#filterDialog').jqm({trigger: '#collegeFilterTrigger', modal: true});
    $(".college a").click(function(event) {
        $(this).parents("li").animate({opacity: "hide" }, "normal")
    });
    $("#clear").click(function(event) {
        $("#selectedColleges > ul > li").hide();
    });
    $("#add").click(function(event) {
        $("#selectedColleges > ul > li").show();
    });
};
// Changes the visualization to new root node
macademia.changeGraph = function(nodeId) {
    if (macademia.rgraph) {
        var param = $.address.parameter('nodeId');
        if (macademia.rgraph.graph.getNode(param).data) {
            // if the node is on the current graph
            macademia.rgraph.onClick(param);
        } else {
            macademia.initiateGraph();
        }
        macademia.queryString.nodeId = param;
    }
};
// controls the show and hide options
macademia.showHide = function() {
    var navVisibility = $.address.parameter('navVisibility');
    if (navVisibility == 'true' && !$("#wrapper").is(":visible")) {
        $("#rightDiv").animate({width: "320"}, "slow");
        $("#infovis").animate({right: "320"}, "slow", function() {
            $("#rightDiv > *").show();
            $("#show").hide();
        });
        // resize visual
        if (macademia.mycanvas) {
            macademia.resizeCanvas($("body").width() - 320);
        }
    } else if (navVisibility == 'false' && $("#wrapper").is(":visible")) {
        $("#rightDiv > *").hide();
        if (macademia.mycanvas) {
            $("#rightDiv").animate({width: "0"}, "slow");
            $("#infovis").animate({right: "0"}, "slow");
        } else {
            // on page load rightDiv will not slide over
            $("#rigthDiv").css('width', '0');
            $("#infovis").css('right', '0');
        }
        $("#show").show();
        // resize visual
        if (macademia.mycanvas) {
            macademia.resizeCanvas($("body").width());
        }
    }
    macademia.queryString.navVisibility = navVisibility;
};
// puts the selected colleges from the college filter into the address bar
macademia.collegeSelection = function() {
    var colleges = new Array();
    $("#selectedColleges li").each(function() {
        if ($(this).is(':visible')) {
            colleges.push($(this).attr('id'));
        }
    });
    macademia.createInstitutionString(colleges);
    $.address.update();
    $('#filterDialog').jqmHide();
};
// takes an array of college ids and creates a string to stick in the url
macademia.createInstitutionString = function(collegeArray) {
    var colleges = "";
    if (collegeArray.length == 0 || collegeArray.length == macademia.institutions) {
        // if no colleges selected, default to all
        colleges = "all";
    } else {
        for (var i = 0; i < collegeArray.length; i++) {
            if (i < collegeArray.length - 1) {
                colleges = colleges + collegeArray[i] + '+';
            } else {
                colleges = colleges + collegeArray[i];
            }
        }
    }
    $.address.parameter('institutions', colleges);
};
// uses url to determine which colleges will be shown in the visualization (incomplete)
macademia.showInstitutions = function() {
    var institutions = $.address.parameter('institutions');
    if (institutions == 'all') {
        // do something
    } else {
        var colleges = institutions.split('+');
        // do something
    }
    macademia.queryString.institutions = institutions;
};
// controls view of right nav (incomplete)
macademia.updateNav = function() {
    var navFunction = $.address.parameter('navFunction');
    if (navFunction == 'search' /*and search page is not visible*/) {
        // go to search page
    } else if (navFunction == 'profile' /*and profile page is not visible*/) {
        var rootId = $.address.parameter('nodeId');
        // go to profile page
    } else if (navFunction == 'request' /*and request page is not visible*/) {
        // go to collaboration request page
    }//else if etc...
    if (navFunction != 'search') {
        $.address.parameter('searchBar', null);
        $.address.update();
    }
    macademia.queryString.navFunction = navFunction;
}
// submits the search query from the url
macademia.submitSearch = function() {
    var searchBox = $.address.parameter('searchBox');
    if (searchBox) {
        $('#rightContent').show();
        var search = searchBox.replace('+', ' ');
        $('#rightContent').load(
                '/Macademia/search/search',
        {searchBox:search}
                );
        return false;
    } else {
        $('#rightContent').hide();
    }
    macademia.queryString.searchBox = searchBox;
};
// resizes canvas according to original dimensions
macademia.resizeCanvas = function(currentWidth) {
    if ($("#mycanvas").css('margin') != 'auto') {
        $("#mycanvas").css('margin', 'auto');
    }
    var originalWidth = 680;
    var originalHeight = 660;
    var originalDistance = 150;
    var currentHeight = $("#infovis").height();
    if (Math.min(currentWidth, currentHeight) == currentWidth) {
        var newWidth = 0.95 * currentWidth;
        var newHeight = originalHeight * newWidth / originalWidth;
    } else {
        var newHeight = 0.95 * currentHeight;
        var newWidth = originalWidth * newHeight / originalHeight;
    }
    if (newWidth != $("mycanvas").css("width")) {
        $("#mycanvas").css({"width":newWidth, "height": newHeight});
        macademia.distance = originalDistance / originalHeight * newHeight;
        macademia.mycanvas.resize(newWidth, newHeight);
        if (macademia.rgraph) {
            macademia.rgraph.config.levelDistance = macademia.distance;
            macademia.rgraph.refresh();
        }
    }
};

macademia.modalLogin = function() {
    $('#loginDialog').jqm({modal: false});
    $('#loginButton').click(function(event) {
        $('#loginDialog').jqmShow()
    });
}

macademia.modalRegister = function() {
    $('#registerDialog').jqm({modal: false});
    $('#registerButton').click(function(event) {
        $('#registerDialog').jqmShow()
    });
}