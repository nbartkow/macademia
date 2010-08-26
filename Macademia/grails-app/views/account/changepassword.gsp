<html>

<head>
    <title>Change password</title>

  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "css", file: "macademiaJit.css")}">
  <g:include view="/layouts/headers.gsp"/>
  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "css", file: "changePasswd.css")}">
</head>

<body>

    <a href="/Macademia/"><img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo.png')}"/></a>
    <div id="main">
      <h2>Change your password</h2>

      <p>
        Change your password below.  Your new password must be at least six letters long.
      </p>

      <g:if test="${error}">
        <p class="alert">${error.encodeAsHTML()}</p>
      </g:if>

      <g:form action="changepasswordcomplete">
        <table>
          <tbody>

            <tr>
              <td><label for="currentPassword">Current password:</label></td>
              <td>
                <input type="password" size="30" id="currentPassword" name="currentPassword" class="easyinput"/>
              </td>
            </tr>

            <tr>
              <td><label for="password">New password:</label></td>
              <td>
                <input type="password" size="30" id="password" name="password" class="password easyinput"/></a>
              </td>
            </tr>

            <tr>
              <td><label for="passwordConfirm">Confirm new password:</label></td>
              <td>
                <input type="password" size="30" id="passwordConfirm" name="passwordConfirm" class="easyinput"/>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input type="submit" name="submit" value="Change password"/>
              </td>
            </tr>
          </tbody>
        </table>

        <div >
        </div>
      </g:form>
    </div>
    <g:render template="/templates/macademia/tagline"/> 
</body>

</html>