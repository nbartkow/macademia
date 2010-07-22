<div id="edit_profile_container">
  <n:errors bean="${user}"/>
  <n:errors bean="${user.profile}"/>
  <form name="edit_pf">

    <div id="formBox">

      <div class="registerRight value ${hasErrors(bean: user, field: 'profile.fullName', 'errors')}"><input type="text" name="fullName" id="fullName" class="textInput easyinput" value="${user.profile?.fullName?.encodeAsHTML()}"/></div>
      <div class="registerLeft"><label for="fullName">Name<span>Full Name</span></label></div>
      <div class="clear"></div>

      <div class="registerRight value ${hasErrors(bean: user, field: 'pass', 'errors')}"><input type="password" name="pass" id="pass" class="textInput password easyinput" value="${user.pass?.encodeAsHTML()}"/></div>
      <div class="registerLeft"><label for="pass">Password<span>You can change your password here.</span></label></div>
      <div class="clear"></div>

      <div class="registerRight value ${hasErrors(bean: user, field: 'passConfirm', 'errors')}"><input type="password" name="passConfirm" id="passConfirm" class="textInput easyinput" value="${user.passConfirm?.encodeAsHTML()}"/></div>
      <div class="registerLeft"><label for="passConfirm">Password<span>confirm</span></label></div>
      <div class="clear"></div>

      <div class="registerRight value ${hasErrors(bean: user, field: 'profile.email', 'errors')}"><n:verifyfield id="email" class="easyinput textInput" name="email" value="${fieldValue(bean: user.profile, field: 'email')}" required="true" controller="account" action="validemail" validmsg="valid" invalidmsg="invalid"/></div>
      <div class="registerLeft"><label for="email">Email<span>Address at the institution where you research.</span></label></div>
      <div class="clear"></div>

      <div class="registerRight value ${hasErrors(bean: user, field: 'profile.department', 'errors')}"><input type="text" id="department" class="textInput easyinput" name="department" value="${user.profile?.department?.encodeAsHTML()}"/></div>
      <div class="registerLeft"><label for="department">Department<span>lorem ipsum</span></label></div>
      <div class="clear"></div>

      <div class="registerRight"><textarea id="editInterests" class="textInput easyInput" name="interests" value="${user.profile?.interests?.encodeAsHTML()}" cols="20" rows="3"></textarea></div>
      <div class="registerLeft"><label for="editInterests">Interests<span>List your research interests, separated by commas.</span></label></div>
      <div class="clear"></div>

    </div>

    <div id="other_info">
      <div class="profileImage">
        <h4>Profile image:</h4>
        <g:render template="../templates/macademia/imageUploader"/>
      </div>
      <input type="hidden" name="links" value="${user.profile?.links?.encodeAsHTML()}">
      <div class="personLinks">
        <h4>Links:</h4>
        <div class="customLink clear example">
          <div class="clear linkField left">Homepage, Facebook, etc.</div>
          <div class="linkValue left">http://www.whitehouse.gov</div>
        </div>
        <div class="addLink clear"><a href="#">add other link</a></div>
        <div class="customLink customLinkTemplate clear">
          <div class="linkField left"><input type="text" class="clearDefault" prompt="link name"></div>
          <div class="linkValue left"><input type="text" class="clearDefault" prompt="link url"></div>
          <div class="left"><a href="#" class="removeLink">(remove)</a></div>
        </div>
      </div>
    </div>
    <br clear=left>
    <br clear=left>
    <div class="clear center" id="submit_edits"><button class="button icon_user" type="submit"><g:message code="nimble.link.registeraccount"/></button> or <a href="#">Cancel</a></div>
  </form>
</div>