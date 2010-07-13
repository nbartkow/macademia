<%--
  Created by IntelliJ IDEA.
  User: jeremy
  Date: Jun 22, 2010
  Time: 10:13:53 AM
  To change this template use File | Settings | File Templates.
--%>

<html>

<head>
  <meta name="layout" content="userCreate"/>
  %{--<meta name="layout" content="${grailsApplication.config.nimble.layout.application}"/>--}%
  <title><g:message code="Edit Profile" /></title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'style_editprofile.css')}"/>
  <g:javascript library="jquery"/>

<!-- Jquery-UI for autocomplete, etc. -->
<link type="text/css" rel="stylesheet" href="${createLinkTo(dir:"css",file:"ui-lightness/jquery-ui-1.8.2.custom.css")}">
  <g:javascript src="jquery/jquery-ui-1.8.2.custom.min.js"/>
  <g:javascript src="lib.macademia.js"/>
  <g:javascript src="lib.macademia.autocomplete.js"/>
</head>

<body>
<div id = main>
<div id = page>
  <div id="editProfile"><h1>Edit Profile</h1>
  </div>
  <div id = editAccountInformationDiv>
  <h2><g:message code="Account Information" /></h2>
  <n:errors bean="${user}"/>
  <n:errors bean="${user.profile}"/>
<div id = editAccountInformation>
  <g:form action="updateuser" id="${user.id}">
    <table>
      <tbody>


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
          <g:textArea id="interests" class="textInput easyInput" name="interests">${allInterests}</g:textArea>
            
        </td>
      </tr>


    <tr>
      <td></td>

      <td>
        <g:link controller="account" action="changepassword" ><g:message code="nimble.link.changepassword" /></g:link>
        <br/>
        <button class="button icon icon_user" type="submit"><g:message code="nimble.link.updateuser" /></button>
      </td>
    </tr>

    </tbody>
    </table>

  </g:form>
    </div>
    </div>

  <script type="text/javascript">
    nimble.createTip('usernamepolicybtn','<g:message code="nimble.template.usernamepolicy.title" />','<g:message code="nimble.template.usernamepolicy" encodeAs="JavaScript"/>');
    nimble.createTip('passwordpolicybtn','<g:message code="nimble.template.passwordpolicy.title" />','<g:message code="nimble.template.passwordpolicy" encodeAs="JavaScript"/>');
  </script>

</div>
</div>

</body>

</html>