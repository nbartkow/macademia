<%@ page import="org.macademia.Person" %>
<div id="sidebar">
    <div id="sidebar_main">
      <div id="login" style="display: block;">
        <div id="local" class="localonlymethod">
          <form id="signin" name="signin" action="#">
            <div id="login_info_div" style="display: block;">
              <label for="email">Email:</label>
              <input type="text" tabindex="1" name="email" class="login_input" id="email"/>
              <label for="password">Password:</label>
              <input type="password" tabindex="2" name="password" class="login_input" id="password">
              <input type="submit" tabindex="3" value="Login" class="login_submit"><g:link params="[group : params.group]" controller="account" action="forgottenpassword" class="forgot_password">forgot password?</g:link>
            </div>
          </form>
        </div>
      </div>
        
      <g:render template="../templates/macademia/searchBar"/>
      <div id="rightContent" class="medium topBorder">
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

      <div id="makeRequestDialog" class="jqmWindow">&nbsp;</div>

      <div id="listRequestDialog" class="jqmWindow">&nbsp;</div>

      <div id="editRequestDialog" class="jqmWindow">&nbsp;</div>

      <div id="filterDialog" class="jqmWindow">&nbsp;</div>

    </div>
</div>
