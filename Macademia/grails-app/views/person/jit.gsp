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

  <div id="infovis">
    &nbsp;

  </div>
  <div id="tagContainer">
    <div id="tagline">Macademia is developed by students at Macalester College under the direction of Shilad Sen.<br> The project is funded by Macalester College, the National Science Foundation, and The Associated Colleges of the Midwest.</div>
  </div>
</div>
<img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo.png')}"/>

<div id="extendedInfo">
  <g:render template="../layouts/rightNav"/>
</div>
</body>
</html>
