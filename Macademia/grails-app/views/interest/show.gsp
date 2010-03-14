<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 4, 2009
  Time: 2:46:33 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
    <g:include view="/layouts/headers.gsp"/>
    <title>Macalester College - Macademia</title>
  </head>
  <body>
  ${interestInstance.normalizedText}       <br/><br/>
  <table>
    <tr>
      <td>People With Interest</td>
    </tr>
    <g:each in="${peopleWithInterest}" var="person">
      <tr>
        <td>
          <g:link url="[controller:'person',action:'show',id:person.id]">
            ${person.name}
          </g:link>
        </td>
      </tr>
    </g:each>
  </table>
</body>
</html>
