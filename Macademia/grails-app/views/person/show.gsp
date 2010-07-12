<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 4, 2009
  Time: 2:46:41 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.macademia.AdminsService" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Macalester College - Macademia</title>
  <g:include view="/layouts/headers.gsp"/>
</head>
<body>
${person.fullName}
<br/>
<a href="mailto:${person.email}">${person.email}</a>
<br/>
${person.institution}
<br/>
${person.department}
<br/>
<br/>

<table>
  <tr>
    <td>
      Interests:
    </td>
  </tr>
  <g:each in="${interests}" var="interest">
    <tr>
      <td>
        <g:link url="[controller:'interest',action:'show',id:interest.id]">
          ${interest.text}
        </g:link>
      </td>
    </tr>
  </g:each>
</table>

<br/>
<table>
  <tr>
    <td>
      Requests for Collaboration:
    </td>
  </tr>
  <g:if test= "${collaboratorRequests.isEmpty()}"> <td>No collaborator requests</td> </g:if>
  <g:each in="${collaboratorRequests}" var="collaboratorRequest">
    <tr>
      <td>
        <g:link url="[controller:'request',action:'show',id:collaboratorRequest.id]">
          ${collaboratorRequest.title}
        </g:link>
      </td>
    </tr>
  </g:each>
</table>
<br/>
<g:if test= "${authenticatedUser}">
  <g:if test="${person.id == authenticatedUser.id}">
    <li>
      <g:link url="[controller:'request',action:'manage' ]" ><g:message code="Manage collaborator requests" /></g:link>
    </li>
    <li>
      <g:link url="[controller:'account',action:'editprofile' ]">Edit Profile</g:link>
    </li>
  </g:if>
  <g:else>
    <g:if test="${auth}">
      <li>
      <g:link url="[controller:'request',action:'manage', id:person.owner.id ]" ><g:message code="Manage collaborator requests" /></g:link>
      </li>
      <li>
      <g:link url="[controller:'account',action:'editprofile', id:person.owner.id ]">Edit Profile</g:link>
      </li>
    </g:if>
  </g:else>
</g:if>


</body>
</html>
