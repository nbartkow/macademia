<%@ page import="org.macademia.UserService; grails.plugins.nimble.core.AdminsService" %>
<div id="show" class="btxt">
  <a href="#/?navVisibility=true"><-show</a>
</div>
<div id="sidebar">
  <div id="wrapper">
    <div id="main">
      <n:notUser>
        <div id="account" class="btxt right">
          <a id="login_link" href="#" class="right">Login</a> |
          <g:link controller="account" action="createuser2" href="#" class="right">Register</g:link>
        </div>
      </n:notUser>
      <div id="account" class="btxt">
        <n:hasRole name="${UserService.USER_ROLE}">
          <div id="account" class="btxt">
            <g:link controller="auth" action="logout" class="icon_cross">Logout</g:link> | <a href="#" id="toggleControls">More</a>
          </div>
        </n:hasRole>
        <ul id="accountControlList" class="left topBorder bottomBorder styledList">

          <div id="moreDropdown">
          <n:hasRole name="${UserService.USER_ROLE}">
            <li>
              <a href="#" class="editProfile">Edit profile</a>
            </li>
            <li>
              <g:link controller="account" action="changepassword">Change password</g:link>
            </li>
            <li>
              <a href="#" id="makeRequestButton">Create request for collaboration</a>
            </li>
            <n:hasRole name="${AdminsService.ADMIN_ROLE}">
              <li>
                <g:link controller="user" action="list" class="icon_user_go">Admin controls</g:link>
              </li>
            </n:hasRole>
          </n:hasRole>
            </div>
        </ul>
      </div>
      <div id="login" style="display: block;">
        <div class="flash">
          <n:flashembed/>
        </div>

        <div id="local" class="localonlymethod">
          <g:form id="login_form" controller="auth" action="signin" name="signin">
            <div id="login" style="display: block;">
              <input type="hidden" name="targetUri" value="${targetUri}"/>
              <label for="username">Email:</label>
              <input type="text" tabindex="1" name="username" class="login_input" id="username"/>

              <label for="password">Password:</label>
              <input type="password" tabindex="2" name="password" class="login_input" id="password">
              <input type="submit" tabindex="3" value="Login" class="login_submit"><g:link controller="account" action="forgottenpassword" class="forgot_password">forgot password?</g:link>
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
      <g:render template="../templates/macademia/searchBar"/>
      <div id="rightContent" class="medium">
        <ul id="instruct_list" class="topBorder bottomBorder styledList">
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
        <div id="searchBoxDiv" class="atxt">
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





      <div id="loginDialog" class="jqmWindow">

      </div>
      <div id="registerDialog" class="jqmWindow">
        <div id="registerAjax">

        </div>
        <div id="registerScript">
        </div>
      </div>
      <div id="makeRequestDialog" class="jqmWindow">
        
      </div>
      <div class="jqmWindow padded2 medium btxt" id="filterDialog">&nbsp;</div>

    </div>
  </div>
  <div id="hide" class="btxt">
    <a href="#/?navVisibility=false">hide-></a>
  </div>
</div>
