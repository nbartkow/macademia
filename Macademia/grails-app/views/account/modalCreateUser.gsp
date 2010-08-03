<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "css", file: "createUser.css")}">
  <g:include view="/layouts/headers.gsp"/>
  <g:javascript src="uploadify/swfobject.js"></g:javascript>
  <g:javascript src="uploadify/jquery.uploadify.v2.1.0.min.js"></g:javascript>
  <g:javascript src="lib.macademia.upload.js"></g:javascript>
  <link type="text/css" rel="stylesheet" href="${createLinkTo(dir: "js", file: "/uploadify/uploadify.css")}">
  <g:javascript>
    $().ready(macademia.initializeRegister);
  </g:javascript>
  
</head>
<body>
<h2 class="center" id="registerTitle">Create New Account</h2>
<div id="center_wrapper">
<div class="instructions"><b>Thanks for joining Macademia!</b>  Macademia will display all the information you enter below, except for your password.</div>
  <form action="#" id="edit_profile" name="edit_profile" method="post">
    <div id="edit_profile_container">
        <div class="halfHeader">Required Information</div>

        <div id="nameErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label for="name">Name<span>Full Name</span></label></div>
        <div class="right topBorder"><input type="text" name="fullName"/></div>
        <div class="clear"></div>

        <div id="passErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label>Password<span>You can change your password here</span></label></div>
        <div class="right topBorder"><input type="password" name="pass" class="clearDefault"/></div>
        <div class="clear"></div>

        <div id="passConfirmErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label>Password<span>Confirm password by retyping</span></label></div>
        <div class="right topBorder"><input type="password" name="passConfirm" class="clearDefault"/></div>
        <div class="clear"></div>

        <div id="emailErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label>Email<span>College email address</span></label></div>
        <div class="right topBorder"><input type="text" name="email" class="clearDefault"/></div>
        <div class="clear"></div>

        <div id="interestErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label>Interests<span>List your research interests, separated by commas.</span></label></div>
        <div class="right topBorder"><textarea id="editInterests" cols="20" rows="3"></textarea></div>
        <div class="clear"></div>
    </div>
    <div id="sidebar">

        <div class="halfHeader">Optional Information</div>

        <div id="titleErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label>Title<span>Job title</span></label></div>
        <div class="right topBorder"><input type="text" name="title" class="clearDefault"/></div>
        <div class="clear"></div>

        <div id="departmentErrors" class="warning">&nbsp;</div>
        <div class="left fieldLabel topBorder"><label>Department<span>Department of college</span></label></div>
        <div class="right topBorder"><input type="text" name="department" class="clearDefault"/></div>
        <div class="clear"></div>


        <div class="left fieldLabel topBorder"><label>Profile image</label></div>
        <div class="right topBorder">
            <g:render template="../templates/macademia/imageUploader"/>
        </div>
      <div class="clear"></div>
      <div class="personLinks topBorder">
        <div class="left fieldLabel"><label>Links</label></div>
        <div class="clear"></div>
        <div class="customLink customLinkTemplate clear">
          <div class="linkField"><input type="text" class="clearDefault" prompt="link name"></div>
          <div class="linkValue"><input type="text" class="clearDefault" prompt="link url"></div>
          <div class="removeLink"><a href="#" class="removeLink">(x)</a></div>
        </div>
  
        <div class="customLink clear example">
          <div class="linkField">Homepage, Facebook, etc.</div>
          <div class="linkValue">http://www.whitehouse.gov</div>
        </div>

        <div class=""></div>
        <div class="center" id="addLinkDiv"><button class="addLink">add other link</button></div>
      </div>
    </div>
    <div class="clear topBorder"></div>
    <div class="progressBar"><span></span></div>
    <div id="submit_edits"><input type="submit" value="Update"> or <a href="/Macademia">Cancel</a></div>
  </form>
</div>
<g:render template="../templates/macademia/tagline"/>
<a href="/Macademia/"><img id="logoImage" src="${createLinkTo(dir: 'images', file: 'macademia-logo.png')}"/></a>
</body>
</html>
