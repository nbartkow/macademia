<html>

<head>
  <title>${title.encodeAsHTML()}</title>

  <g:include view="/layouts/headers.gsp"/>
  <link rel='stylesheet' href='/Macademia/css/style.css?v=1' media='all' />

  <g:javascript>
      $(document).ready(function() {
          macademia.initLogoLink();
      });
  </g:javascript>
</head>

<body>

    <header><div id="logo"></div></header>

    <div id="message">
        <div id="main">
          <h2>${title.encodeAsHTML()}</h2>

          <g:if test="${error}">
            <p class="alert">${error.encodeAsHTML()}</p>
          </g:if>

          <p>${message.encodeAsHTML()}</p>

          <p>
            <m:ifLoggedIn ><a href="/Macademia/${params.group}/person/jit/#/?nodeId=p_${request.authenticated.id}&institutions=all">Return to Macademia</a></m:ifLoggedIn>
            <m:ifNotLoggedIn><a href="/Macademia/${params.group}">Return to Macademia</a></m:ifNotLoggedIn>
          </p>
        </div>
    </div>
    <g:render template="../layouts/footer"/>
</body>

</html>