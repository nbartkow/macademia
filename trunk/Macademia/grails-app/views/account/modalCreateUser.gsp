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

<div class="clear" id="other_info">

     %{--Warning: the upload javascript relies on the class and id names below.--}%
     %{--Change them at your own risk--}%
     <div class="editPicture">
      <div>
        <g:if test="${user?.profile?.imageSubpath}">
          <img src="/Macademia/images/db/large/${user.profile.imageSubpath}" alt="" defaultImage="/Macademia/images/scholar_cat.gif"/>
        </g:if>
        <g:else>
          <img src="/Macademia/images/scholar_cat.gif" width="50"  alt="" defaultImage="/Macademia/images/scholar_cat.gif"/>
        </g:else>
      </div>
      <div class="links">
          %{--These elements must appear in exactly this order for the upload functionality to work--}%
          <a href="#" class="change">change picture</a> <span class="separator">|</span><a href="#" class="delete">delete</a>
          <div id="imgUploader">&nbsp;</div>
      </div>
      <input type="hidden" name="imageSubpath" value=""/>
    </div>
</div>
  
<div id="submit_edits"><button class="button icon icon_user" type="submit"><g:message code="nimble.link.registeraccount" /></button> or <a href="#">Cancel</a></div>
</g:form>
</div>

   
