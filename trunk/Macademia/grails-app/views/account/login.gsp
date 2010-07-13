<div class="login">

  <div class="flash">
    <n:flashembed/>
  </div>

  <div id="local" class="localonlymethod">
    <h2>Login</h2>

    <g:form controller = "auth" action="signin" name="signin">
      <input type="hidden" name="targetUri" value="${targetUri}"/>

        <label for="username"><g:message code="nimble.label.username"/></label>
        <input id="username" type="text" name="username" class="title"/>
        <br>
        <label for="password"><g:message code="nimble.label.password"/></label>
        <input id="password" type="password" name="password" class="title"/>
        <br>
        <g:checkBox id="rememberme" name="rememberme"/>
        <label><g:message code="nimble.label.rememberme"/></label>

        <button type="submit" class="button icon icon_user_green"><g:message code="nimble.link.login.basic"/></button>

    </g:form>

    <div class="accountoptions">
      <g:link controller="account" action="forgottenpassword" class="textlink icon icon_flag_purple"><g:message code="nimble.link.forgottenpassword"/></g:link>
      <g:if test="${registration}">
        <g:link controller="account" action="createuser" class="textlink icon icon_user_go"><g:message code="nimble.link.newuser"/></g:link>
      </g:if>
    </div>
  </div>

</div>
