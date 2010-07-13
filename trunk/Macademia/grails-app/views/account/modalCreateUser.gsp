<div id="edit_profile_container">
  <n:errors bean="${user}"/>
  <n:errors bean="${user.profile}"/>
<g:form action="saveuser" id="edit_profile" name="edit_pf" method="post">

<div id="formBox">
<div class="left"><label for="fullname">Name<span>Full Name</span></label></div>
<div class="right value ${hasErrors(bean: user, field: 'profile.fullName', 'errors')}"><input type="text" name="name" id="fullname" class="textInput easyinput" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}"/></div>
<div class="clear"></div>

<div class="left value"><label for="pass">Password<span>You can change your password here.</span></label>	</div>
<div class="right value ${hasErrors(bean: user, field: 'pass', 'errors')}"><input type="password" name="pass" id="pass" class="textInput password easyinput" name="pass" value="${user.pass?.encodeAsHTML()}"/></div>
<div class="clear"></div>

<div class="left"><label for="passConfirm">Password<span>confirm</span></label>	</div>
<div class="right value ${hasErrors(bean: user, field: 'passConfirm', 'errors')}"><input type="password" name="passConfirm" id="passConfirm" class="textInput easyinput" name="pass" value="${user.passConfirm?.encodeAsHTML()}"/></div>
<div class="clear"></div>

<div class="left"><label for="email">Email<span>Address at the institution where you research.</span></label></div>
<div class="right value ${hasErrors(bean: user, field: 'profile.email', 'errors')}"><n:verifyfield id="email" class="easyinput textInput"  name="email" value="${fieldValue(bean: user.profile, field: 'email')}" required="true" controller="account" action="validemail" validmsg="valid" invalidmsg="invalid" /></div>
<div class="clear"></div>

<div class="left"><label for="department">Department<span>lorem ipsum</span></label></div>
<div class="right value ${hasErrors(bean: user, field: 'profile.department', 'errors')}"><input type="text" id="department" class="textInput easyinput" name="department" value="${user.profile?.department?.encodeAsHTML()}" /></div>
<div class="clear"></div>

<div class="left"><label for="editInterests">Interests<span>List your research interests, separated by commas.</span></label></div>
<div class="right"><textarea id="editInterests" class="textInput easyInput" name="interests" value="${user.profile?.interests?.encodeAsHTML()}" cols="20" rows="3"></textarea></div>
<div class="clear"></div>

</div>
  <div id="submit_edits"><button class="button icon icon_user" type="submit"><g:message code="nimble.link.registeraccount" /></button> or <a href="#">Cancel</a></div>
</g:form>
</div>