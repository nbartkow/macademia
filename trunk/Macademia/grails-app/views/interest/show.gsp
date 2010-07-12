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
  ${interest.text}       <br/><br/>
  <table>
    <tr>
      <td>People With Interest:</td>
    </tr>
    <g:if test= "${peopleWithInterest.isEmpty()}"> <td>No people with interest</td> </g:if>
    <g:each in="${peopleWithInterest}" var="person">
      <tr>
        <td>
          <g:link url="[controller:'person',action:'show',id:person.id]">
            ${person.fullName}
          </g:link>
        </td>
      </tr>
    </g:each>
  </table>
  <table>
    <tr>
      <td>Related Interests:</td>
    </tr>
    <g:if test= "${relatedInterests.isEmpty()}"> <td>No related interests</td> </g:if>
    <g:each in="${relatedInterests}" var="interest">
      <g:if test= "${interest != null}">
      <tr>
        <td>
          <g:link url="[controller:'interest',action:'show',id:interest.id]">
            ${interest.text}
          </g:link>
        </td>
      </tr>
      </g:if>
    </g:each>
  </table>

</body>
</html>
