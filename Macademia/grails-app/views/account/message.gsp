<html>

<head>
    <title>${title.encodeAsHTML()}</title>

  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "css", file: "macademiaJit.css")}">
  <g:include view="/layouts/headers.gsp"/>
  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "css", file: "message.css")}">
</head>

<body>

    <a href="/Macademia/"><img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo.png')}"/></a>
    <div id="main">
      <h2>${title}</h2>

      <g:if test="${error}">
        <p class="alert">${error.encodeAsHTML()}</p>
      </g:if>

      <p>${message.encodeAsHTML()}</p>

      <p>
        <m:ifLoggedIn ><a href="/Macademia/person/jit/#/?nodeId=p_${request.person.id}&institutions=all">Return to Macademia</a></m:ifLoggedIn>
        <m:ifNotLoggedIn><a href="/Macademia">Return to Macademia</a></m:ifNotLoggedIn>
      </p>
    </div>
    <g:render template="/templates/macademia/tagline"/> 
</body>

</html>