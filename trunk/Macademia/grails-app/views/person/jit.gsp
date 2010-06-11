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
  <g:javascript>
    $().ready(function(){
       init('person', ${person.id});
       macademia.clearSearch();
       macademia.pageLoad();
       macademia.nav();
    });
  </g:javascript>
</head>
<body>
<g:render template="../layouts/components"/>
<div id="infovis">
  &nbsp;
  <div id="show" class="btxt">
    <a rel="address:/" href="#show"><-show</a>
  </div>
</div>
<div id="extendedInfo">
  <g:render template="../layouts/rightNav"/>
</div>
</body>
</html>
