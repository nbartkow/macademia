<div id = page>
  <div id="editProfile"><h1>Edit Profile</h1>
  </div>
  <div id = editAccountInformationDiv>
  <h2><g:message code="Account Information" /></h2>
  </div>
  %{--=--}%
<div id = editAccountInformation>
  <!--<g:form action="updateuser">
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
          <textarea id="interests" class="textInput easyInput" name="interests" value="${user.profile?.interests?.encodeAsHTML()}">
            ${allInterests}
          </textarea>
        </td>
      </tr>


    <tr>
      <td/>
      <td>
        <button class="button icon icon_user" type="submit"><g:message code="nimble.link.updateuser" /></button>
      </td>
    </tr>

    </tbody>
    </table>
  </g:form>-->

  <script type="text/javascript">
    nimble.createTip('usernamepolicybtn','<g:message code="nimble.template.usernamepolicy.title" />','<g:message code="nimble.template.usernamepolicy" encodeAs="JavaScript"/>');
    nimble.createTip('passwordpolicybtn','<g:message code="nimble.template.passwordpolicy.title" />','<g:message code="nimble.template.passwordpolicy" encodeAs="JavaScript"/>');
  </script>

</div>
</div>