<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 4, 2009
  Time: 2:46:41 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Macalester College - Macademia</title>
  <g:include view="/layouts/headers.gsp"/>
</head>
<body>
${personInstance.name}
<br/>
<br/>

<table>
  <tr>
    <td>
      Interests
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
<br/>


<table>
  <tr>
    <td>Similar Interests</td>
  </tr>
  <g:each in="${simInterestsList}" var="simInterest">
  <tr>
    <td>
       ${simInterest}
    </td>
  </tr></g:each>
</table>

<br/>
<br/>
<table>
  <tr>
    <td>
      ${neighbors.size} Neighbors
    </td>
  </tr>
  <g:each in="${neighbors}" var="neighbor">
  <tr>
    <td>
      ${neighbor.first}
      |
      <g:link url="[action:'show',controller:'person',id:neighbor.second.id]">${neighbor.second}</g:link>
      |
      Shared Interests:
      <br/>
      <g:each in="${neighbor.sharedInterests}" var="sharedInterest">
        ${"__"+sharedInterest.encodeAsHTML()}
        <br/>
      </g:each>
      
    </td>
  </tr></g:each>
</table>
<g:link url="[controller:'person',action:'show',id:(personInstance.id+1)]">Next</g:link>


</body>
</html>
