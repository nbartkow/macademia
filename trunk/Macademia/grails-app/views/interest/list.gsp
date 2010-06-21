<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 5, 2009
  Time: 7:45:59 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Simple GSP page</title></head>
  <table>
    <tr>
      <td>
        Text
      </td>
    </tr>
    <g:each in="${interestList}" var="interest">
      <tr>
        <td>
            <g:link url="/Macademia/interest/jit/#?nodeId=i_${interest.id}">
              ${interest.normalizedText}
            </g:link>
        </td>
      </tr>
    </g:each>
  </table>

</html>