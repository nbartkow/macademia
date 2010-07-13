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
    <title>Macalester College - Macademia</title>
  </head>
  <body>
  ${interest.text}       <br/><br/>
  <table>
    <tr>
      <td>People With Interest:</td>
    </tr>
    <g:each in="${peopleWithInterest}" var="person">
      <tr>
        <td>
          <g:link url="#/?nodeId=p_${person.id}&navFunction=person&personId=${person.id}">
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
    <g:if test= "${relatedInterests.isEmpty()}"> <td>No related Interests</td> </g:if>
    <g:each in="${relatedInterests}" var="interest">
      <g:if test= "${interest != null}">
      <tr>
        <td>
          <g:link url="#/?nodeId=i_${interest.id}&navFunction=interest&interestId=${interest.id}">
            ${interest.text}
          </g:link>
        </td>
      </tr>
      </g:if>
    </g:each>
  </table>
</body>
</html>
