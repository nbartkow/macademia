<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 21, 2009
  Time: 4:14:16 PM
  To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "css", file: "macademiaJit.css")}">
  <g:include view="/layouts/headers.gsp"/>

  <link rel="stylesheet" href="${resource(dir: 'css', file: 'jqModal.css')}"/>
  <g:javascript>
    $().ready(function() {
        macademia.pageLoad();
    });
  </g:javascript>

</head>
<body>

<g:render template="../layouts/components"/>

<div id="mainContent">

  
  <div id="acknowledgements2">
    <div>generously supported by:</div>
    <div class="logo">
      <a href="http://acm.edu"><img src="${createLinkTo(dir: 'images', file: 'acm_logo.png')}" alt="The Associated Colleges of the Midwest"/></a>
      &nbsp;and</div>
    <div class="logo"><a href="http://mellon.org">The Mellon Foundation</a> </div>
    %{--<div class="logo"><span>The National Science Foundation</span></div>--}%
    %{--<div class="logo"><span>Macalester College</span></div>--}%
    %{--<div id="macLogo"><img src="${createLinkTo(dir: 'images', file: 'mac_crest.png')}"/>Macalester College</div>--}%
  </div>

  <div id="infovis">

    
    &nbsp;
  </div>
  <g:render template="../templates/macademia/tagline"/>
</div>

  <g:render template="../templates/macademia/logo"/>

<div id="extendedInfo">
  <g:render template="../layouts/rightNav"/>
</div>

  <g:javascript>
    $().ready(function() {
        macademia.serverLog('nav', 'initial', {'url' : location.href });  
    });
  </g:javascript>

<div id="aboutJqm" class="jqmWindow padded2 medium btxt">
  <a href="#" class="closeImg"><img src="${createLinkTo(dir: 'images', file: 'close_icon.gif')}"/></a>
  <div class="logo">
    <img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo-black.png')}"/>
    <span class="tagline">Connecting colleagues who share research interests.</span>
  </div>
  <div class="topBorder instructions">Macademia visualizes faculty research interests.  You can:
        <ul class="styledList">
          <li>
            <b>Click</b> on a name or interest to recenter the visualization.
          </li>
          <li>
            <b>Hover</b> over a name or interest to show more information.
          </li>
          <li>
            <b>Search</b> in the upper right for a person or interest.
          </li>
          <li>
            <b>Filter</b> the search results by school.
          </li>
          <li>
            <b>Add</b> your own profile by signing up in the upper right.
          </li>
        </ul>
    </div>
  %{--<div id="acknowledgements" class="topBorder">--}%
    %{--<div>Macademia is generously supported by:</div>--}%
    %{--<div class="acmLogo"><a href="http://acm.edu">The Associated Colleges of the Midwest &nbsp;<img src="${createLinkTo(dir: 'images', file: 'acm_logo.png')}"/></a></div>--}%
    %{--<div class="mellonLogo"><a href="http://mellon.org">The Andrew M. Mellon Foundation</a></div>--}%
    %{--<div id="macLogo"><img src="${createLinkTo(dir: 'images', file: 'mac_crest.png')}"/>Macalester College</div>--}%
  %{--</div>--}%
  <div id="team" class="topBorder">
    Macademia is developed by current and past students at Macalester College:&nbsp;
    Henry Charlton,
    Ryan Kerwin,
    Jeremy Lim,
    Brandon Maus,
    Nathaniel Miller,
    Meg Naminski,
    Ernesto Nunes,
    Alex Schneeman,
    Isaac Sparling,
    Anthony Tran,
    under the direction of Prof. Shilad Sen
  </div> 
  <div class="close">
    <a href="#"><div>Go to Macademia!</div></a>
  </div>
</div>
</body>
</html>
