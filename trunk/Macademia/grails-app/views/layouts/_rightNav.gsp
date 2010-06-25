<%@ page import="org.macademia.UserService; grails.plugins.nimble.core.AdminsService" %>
<div id="show" class="btxt">
  <a href="#/?navVisibility=true"><-show</a>
</div>
<div id="rightDiv">
  <div id="wrapper">
    <div id="main">
     <n:notUser>
      <div id="account" class="btxt right">
        <a id="loginButton" class="right">Login</a> | <a id="registerButton" class="right">Register</a>
      </div>
      </n:notUser>
            <div id="account" class="btxt">
        <ul>
        <n:hasRole name="${UserService.USER_ROLE}">
          <div id="account" class="btxt">
          <g:link controller="auth" action="logout" class="icon icon_cross">Logout</g:link>
          </div>
          <li>
          <g:link controller="account" action="edit" class="icon icon_user_go">Edit Profile</g:link>
		  </li>
          <li>
          <g:link controller="account" action="changepassword" ><g:message code="nimble.link.changepassword" /></g:link>
          </li>
          <li>
          <g:link controller="collaboratorRequest" action="manage" ><g:message code="Manage collaborator requests" /></g:link>
          </li>
          <n:hasRole name="${AdminsService.ADMIN_ROLE}">
            <li>
              <g:link controller="user" action="list" class="icon icon_user_go">Admin controls</g:link>
            </li>
          </n:hasRole>
          </n:hasRole>
      </ul>
      </div>
      <div id="mac_logo" class="atxt center">
        <img id="logoImage" src="${createLinkTo(dir: 'images', file: 'mac_logo.png')}"/><br/>
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

      <g:formRemote id="" class="center"
              name="a"
              url="[action:'show', controller:'collaboratorRequest', id:'1']"
              update="rightContent">
        <br/>
        <input type="submit" id=id value="Collaborator Request View">

      </g:formRemote>

      </div>





      <div id="loginDialog" class="jqmWindow">
        <h3>(to be filled with login page)</h3>
      </div>
      <div id="registerDialog" class="jqmWindow">
        <h3>(to be filled with registration page)</h3>
      </div>
    </div>
  </div>
  <div id="hide" class="btxt">
    <a href= "#/?navVisibility=false">hide-></a>
  </div>
</div>
