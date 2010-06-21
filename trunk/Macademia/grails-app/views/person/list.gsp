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
        Name
      </td>
      <td>
        Dept
      </td>
    </tr>
    <g:each in="${personList}" var="person">
      <tr>
        <td>
            <g:link url="/Macademia/interest/jit/#?nodeId=i_${person.id}">
              ${person.name}
            </g:link>
        </td>
        <td>
          ${person.department}
        </td>
      </tr>
    </g:each>
  </table>

</html>