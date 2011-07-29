<html>

<head>
  <title>Change password</title>

  <g:include view="/layouts/headers.gsp"/>
  <link rel='stylesheet' href='/Macademia/css/style.css?v=1' media='all' />

  <g:javascript>
      $(document).ready(function() {
          macademia.initLogoLink();
      });
  </g:javascript>
</head>

<body>

    <header><div id="logo"></div></header>

    <div id="passwordForgot">
        <div id="main">
          <h2>Reset your password.</h2>

          <p>
            Enter your email address below to reset your password.
          </p>

          <g:if test="${error}">
            <p class="alert">${error.encodeAsHTML()}</p>
          </g:if>

          <g:form params="[group : params.group]" action="changepasswordcomplete">
            <table id="resetPass">
              <tbody>

                <tr>
                  <td><label for="email">Email address:</label></td>
                  <td>
                    <input type="input" size="30" id="email" name="email" class="easyinput"/>
                  </td>
                </tr>

                <tr>
                  <td></td>
                  <td>
                    <input type="submit" name="submit" value="Reset password"/>
                    <input type="button" value="Cancel" onclick="location.href = '/Macademia/${params.group}'" />
                  </td>
                </tr>
              </tbody>
            </table>

            <div >
            </div>
          </g:form>
        </div>
    </div>

    <g:render template="../layouts/footer"/>

</body>

</html>