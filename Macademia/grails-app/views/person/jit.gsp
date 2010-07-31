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
    <div id="taglineLinks">
      <a href="#">about</a> &middot;
      <a href="mailto:ssen@macalester.edu">feedback</a> &middot;
      <a href="#">privacy</a> &middot;
      <a href="http://code.google.com/p/macademia">code</a><br>
    </div>
    <div id="tagline">
Macademia is developed by students at Macalester College under the direction of <a href="http://www.shilad.com">Shilad Sen</a>, and <br>funded by <a href="http://www.macalester.edu">Macalester College</a>, the <a href="http://www.nsf.gov">National Science Foundation</a>, and <a href="http://www.acm.edu">The Associated Colleges of the Midwest</a>.</div>
  </div>
</div>
<a href="/Macademia/"><img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo.png')}"/></a>

<div id="extendedInfo">
  <g:render template="../layouts/rightNav"/>
</div>
</body>
</html>
