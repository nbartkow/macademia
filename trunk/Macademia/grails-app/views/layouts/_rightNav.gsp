<%@ page import="org.macademia.UserService; grails.plugins.nimble.core.AdminsService" %>
<div id="show" class="btxt">
  <a href="#/?navVisibility=true"><-show</a>
</div>
<div id="rightDiv">
  <div id="wrapper">
    <div id="main">
      <n:notUser>
        <div id="account" class="btxt right">
          <a id="login_link" href="#" class="right">Login</a> | <a id="registerButton" href="#" class="right">Register</a>
        </div>
      </n:notUser>
      <div id="account" class="btxt">
        <n:hasRole name="${UserService.USER_ROLE}">
          <div id="account" class="btxt">
            <g:link controller="auth" action="logout" class="icon icon_cross">Logout</g:link>
          </div>
        </n:hasRole>
        <ul>
          <n:hasRole name="${UserService.USER_ROLE}">
            <li>
              <g:link controller="account" action="edit" class="icon icon_user_go">Edit Profile</g:link>
            </li>
            <li>
              <g:link controller="account" action="changepassword"><g:message code="nimble.link.changepassword"/></g:link>
            </li>
            <li>
              <g:link controller="collaboratorRequest" action="manage"><g:message code="Manage collaborator requests"/></g:link>
            </li>
            <n:hasRole name="${AdminsService.ADMIN_ROLE}">
              <li>
                <g:link controller="user" action="list" class="icon icon_user_go">Admin controls</g:link>
              </li>
            </n:hasRole>
          </n:hasRole>
        </ul>
      </div>
      <div id="login" style="display: block;">
        <div class="flash">
          <n:flashembed/>
        </div>

        <div id="local" class="localonlymethod">
          <g:form controller="auth" action="signin" name="signin">
            <div id="login" style="display: block;">
                <input type="hidden" name="targetUri" value="${targetUri}"/>
				<label for="username">Email Address</label>
				<input type="text" tabindex="1" name="username" class="login_input" id="username" />
				<label for="password"><a href="index.html#" class="forgot_password">forgot?</a>Password</label>
				<input type="text" tabindex="2" name="password" class="login_input" id="password">
				<input type="submit" tabindex="3" value="Login" class="login_submit">
            </div>
          </g:form>

          <div class="accountoptions">
            <g:link controller="account" action="forgottenpassword" class="textlink icon icon_flag_purple"><g:message code="nimble.link.forgottenpassword"/></g:link>
            <g:if test="${registration}">
              <g:link controller="account" action="createuser" class="textlink icon icon_user_go"><g:message code="nimble.link.newuser"/></g:link>
            </g:if>
          </div>
        </div>
      </div>
      <div id="mac_logo" class="atxt center">
        <div id="description"><b>Macademia</b> connects colleagues who have shared interests.<br/></div>
      </div>
      <g:render template="../templates/macademia/searchBar"/>
      <div id="rightContent" class="medium">
        <ul id="instruct_list">
          <li>
            <b>Click</b> on a name or interest to recenter.
          </li>
          <li>
            <b>Hover</b> over a name to show more info.
          </li>
          <li>
            <b>Search</b> below for a person or interest.
          </li>
        </ul>
        <div id="searchBoxDiv">
          &nbsp;
        </div>
        <div id="personIdDiv">
          &nbsp;
        </div>
        <div id="interestIdDiv">
          &nbsp;
        </div>
        <div id="requestIdDiv">
          &nbsp;
        </div>

      </div>
      <img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo.png')}"/>





      <div id="loginDialog" class="jqmWindow">

      </div>
      <div id="registerDialog" class="jqmWindow">
        <div id="registerAjax">

        </div>
        <div id="registerScript">
        </div>
       </div>
      <div class="jqmWindow padded2 medium btxt" id="filterDialog">&nbsp;</div>



    </div>
  </div>
  <div id="hide" class="btxt">
    <a href="#/?navVisibility=false">hide-></a>
  </div>
</div>
