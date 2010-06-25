<html>

<head>
  <meta name="layout" content="userCreate"/>
  %{--<meta name="layout" content="${grailsApplication.config.nimble.layout.application}"/>--}%
  <title><g:message code="nimble.view.account.registeraccount.initiate.title" /></title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'style_editprofile.css')}"/>
  <nh:pstrength />
</head>

<body>
<div id = main>
<div id = page>
  <div id="editProfile"><h1>Create Profile</h1>
  </div>
  <div id = editAccountInformationDiv>
  <h2><g:message code="Account Information" /></h2>
  <n:errors bean="${user}"/>
  <n:errors bean="${user.profile}"/>
<div id = editAccountInformation>
  <g:form action="saveuser">
    <table>
      <tbody>

      <tr id= emailField>
        <td valign="top" class="name"><label for="email"><g:message code="nimble.label.email" /></label></td>
        <td valign="top" class="value ${hasErrors(bean: user, field: 'profile.email', 'errors')}">
        <n:verifyfield id="email" class="easyinput textInput"  name="email" value="${fieldValue(bean: user.profile, field: 'email')}" required="true" controller="account" action="validemail" validmsg="valid" invalidmsg="invalid" />
        <a href="#" id="usernamepolicybtn" rel="usernamepolicy" class="empty icon icon_help"></a>
        </td>
      </tr>

      <tr id= passField>
        <td valign="top" class="name"><label for="pass"><label for="pass"><g:message code="nimble.label.password" /></label></td>
        <td valign="top" class="value ${hasErrors(bean: user, field: 'pass', 'errors')}">
        <input type="password" size="30" id="pass" class="textInput password easyinput" name="pass" value="${user.pass?.encodeAsHTML()}"/> <span class="icon icon_bullet_green">&nbsp;</span><a href="#" id="passwordpolicybtn" rel="passwordpolicy" class="empty icon icon_help"></a>
        </td>
      </tr>

      <tr id = passConfirmField>
        <td valign="top" class="name"><label for="passConfirm"><g:message code="nimble.label.password.confirmation" /></label></td>
        <td valign="top" class="value ${hasErrors(bean: user, field: 'passConfirm', 'errors')}">
        <input type="password" size="30" id="passConfirm" class="textInput easyinput" name="passConfirm" value="${user.passConfirm?.encodeAsHTML()}" /> <span class="icon icon_bullet_green">&nbsp;</span>
        </td>
      </tr>

      <tr id= fullNameField>
        <td valign="top" class="name"><label for="fullName"><g:message code="nimble.label.fullname" /></label></td>
        <td valign="top" class="value ${hasErrors(bean: user, field: 'profile.fullName', 'errors')}">
        <input type="text" size="30" id="fullName" class="textInput easyinput" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}" />
        </td>
      </tr>

      <tr id= departmentField>
        <td valign="top" class="name"><label for="department"><g:message code="nimble.label.department" /></label></td>
        <td valign="top" class="value ${hasErrors(bean: user, field: 'profile.department', 'errors')}">
        <input type="text" size="30" id="department" class="textInput easyinput" name="department" value="${user.profile?.department?.encodeAsHTML()}" />
        </td>
      </tr>

      <tr id= interestField>
        <td valign="top" class="name"><label for="interests"><g:message code="nimble.label.interests" /></label></td>
        <td valign="top" class="value ${hasErrors(bean: user, field: 'interests', 'errors')}">
        <textarea id="interests" class="textInput easyInput" name="interests" value="${user.profile?.interests?.encodeAsHTML()}"></textarea>
        </td>
      </tr>


      <n:recaptcharequired>
        <tr>
          <th><g:message code="nimble.label.captcha" /></th>
          <td>
            <n:recaptcha/>
          </td>
        </tr>
      </n:recaptcharequired>



    </div>
    </div>
  
    <tr>
      <td/>
      <td>
        <button class="button icon icon_user" type="submit"><g:message code="nimble.link.registeraccount" /></button>
      </td>
    </tr>

    </tbody>
    </table>
  </g:form>

  <script type="text/javascript">
    nimble.createTip('usernamepolicybtn','<g:message code="nimble.template.usernamepolicy.title" />','<g:message code="nimble.template.usernamepolicy" encodeAs="JavaScript"/>');
    nimble.createTip('passwordpolicybtn','<g:message code="nimble.template.passwordpolicy.title" />','<g:message code="nimble.template.passwordpolicy" encodeAs="JavaScript"/>');
  </script>

</div>
</div>

</body>

</html>