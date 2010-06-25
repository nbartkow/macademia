<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
  <title><g:layoutTitle default="Grails"/></title>

  <nh:nimblecore/>
  
  <nh:nimbleui/>

  <nh:growl/>

  <nh:admin/>

  <g:layoutHead/>

%{--<g:if test="${grailsApplication.config.nimble.layout.customcss != ''}">--}%
%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'style_editprofile.css')}"/>--}%
%{--</g:if>--}%

</head>

<body>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'style_editprofile.css')}"/>
<div id="doc">
  
  <div id="hd">
	<g:render template='/templates/nimble/nimbleheader' model="['navigation':true]"/>
  </div>

  <div id="bd" class="clear">
    <g:layoutBody/>
  </div>

  <div id="ft">

  </div>
</div>


<g:render template="/templates/sessionterminated" contextPath="${pluginContextPath}"/>

  <div id="submitBottomDiv">
  </div>

</body>
</html>