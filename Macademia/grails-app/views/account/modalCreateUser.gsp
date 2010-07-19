<div id="edit_profile_container">
  <n:errors bean="${user}"/>
  <n:errors bean="${user.profile}"/>
<g:form action="#" name="edit_pf" method="post">

<div id="formBox">
<div class="left"><label for="fullName">Name<span>Full Name</span></label></div>
<div class="right value ${hasErrors(bean: user, field: 'profile.fullName', 'errors')}"><input type="text" name="fullName" id="fullName" class="textInput easyinput"  value="${user.profile?.fullName?.encodeAsHTML()}"/></div>
<div class="clear"></div>

<div class="left value"><label for="pass">Password<span>You can change your password here.</span></label>	</div>
<div class="right value ${hasErrors(bean: user, field: 'pass', 'errors')}"><input type="password" name="pass" id="pass" class="textInput password easyinput" value="${user.pass?.encodeAsHTML()}"/></div>
<div class="clear"></div>

<div class="left"><label for="passConfirm">Password<span>confirm</span></label>	</div>
<div class="right value ${hasErrors(bean: user, field: 'passConfirm', 'errors')}"><input type="password" name="passConfirm" id="passConfirm" class="textInput easyinput" value="${user.passConfirm?.encodeAsHTML()}"/></div>
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

<div class="clear left" id="other_info">
  <div class="profileImage">
    <h4>Profile image:</h4>
    <g:render template="../templates/macademia/imageUploader"/>
  </div>
  <input type="hidden" name="links" value="${user.profile?.links?.encodeAsHTML()}">
  <div class="personLinks left"><h4>Links:</h4>
    <div class="standardLink clear">
      <div class="clear linkField left">Homepage</div>
      <div class="linkValue left"><input type="text"></div>
    </div>
    <div class="standardLink clear">
      <div class="clear linkField left">Department website</div>
      <div class="linkValue left"><input type="text"></div>
    </div>
    <div class="addLink clear"><a href="#">add other link</a></div>  
    <div class="customLink customLinkTemplate clear">
      <div class="clear linkField left"><input type="text" value="name"></div>
      <div class="linkValue left"><input type="text" value="url"></div>
      <div class="left"><a href="#" class="removeLink">(remove)</a></div>
    </div>
  </div>
  <div><a href="#" onclick="macademia.links.serialize(); return false;">unserialze</a></div>
</div>
  
<div class="clear" id="submit_edits"><button class="button icon icon_user" type="submit"><g:message code="nimble.link.registeraccount" /></button> or <a href="#">Cancel</a></div>
</g:form>
</div>

   
