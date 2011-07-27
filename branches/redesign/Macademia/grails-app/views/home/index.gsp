<!DOCTYPE html>
<!--[if lt IE 7]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class='no-js' lang='en'>
  <!--<![endif]-->
  <head>
    <meta charset='utf-8' />
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible' />
    <meta content='width=device-width, initial-scale=1.0' name='viewport' />
    <g:include view="/layouts/headers.gsp"/>
    <link rel='stylesheet' href='css/style.css' media='all' />
    <g:javascript>
      $(document).ready(function() {
          macademia.homePageLoad();
      });
    </g:javascript>
  </head>
  <body>
    <div id="homePage">
      <header>
      	<div id="headerWrapper">
	      	<div id="logo" title="Macademia">Macademia</div>
            <nav>
                <ul>
                    <m:ifNotLoggedIn>
                        <li><g:link params="[group : params.group]" controller="account" action="createuser" href="#">Create Account</g:link></li>
                        <li><a id="login_link" href="#">Login</a></li>
                    </m:ifNotLoggedIn>
                    <m:ifLoggedIn>
                        <li>Logged in as <m:personLink person="${request.authenticated}"/>
                        (<g:link params="[group : params.group]" controller="account" action="logout" class="icon_cross">Logout</g:link>)</li>
                        <li><g:link params="[group : params.group]" controller="account" action="edit">Edit Profile</g:link></li>
                        <li><g:link params="[group : params.group]" controller="account" action="changepassword">Change Password</g:link></li>
                    </m:ifLoggedIn>

                </ul>
            </nav>

            <div id="login">
                <div id="local" class="localonlymethod">
                  <form id="signin" name="signin" action="#">
                    <div id="login_info_div" style="display: block;">
                      <div>
                          <label for="email">Email:</label>
                          <input type="text" tabindex="1" name="email" class="login_input" id="email"/>
                      </div>
                      <div>
                          <label for="password">Password:</label>
                          <input type="password" tabindex="2" name="password" class="login_input" id="password">
                      </div>
                      <input type="submit" tabindex="3" value="Login" class="login_submit"><g:link params="[group : params.group]" controller="account" action="forgottenpassword" class="forgot_password">forgot password?</g:link>
                    </div>
                  </form>
                </div>
            </div>

	      	<h1>Macademia</h1>
	      	<h2>Connecting colleagues who share research interests</h2>
      	</div>
      </header>

      <div id='main' role='main'>

      	<div id="mainSearchBox">
      		<input type="text" id="searchBox" placeholder="Search for researchers or interests" />
      		<div id="searchSubmit" class="customButton"><a id="submitSearch" href="javascript:;">Search</a></div>
      	</div>

      	<div id="slideshow">
      		<div id="slideshowReel">
	      		<div class="slide">
	      			<a href="/Macademia/acm"><img src="images/thumbnailTest01.png" /></a>
	      			<h3>Find collaborators by searching for research interests</h3>
	      			<p>Macademia visualizes your search results by showing connections between research topics and colleagues. Click on names or interests to recenter the visualization.</p>
	      		    <div id="entrancePortal"><div class="customButton"><a href="/Macademia/acm">Go to Macademia</a></div></div>
                </div>
      		</div>
      	</div>



      	<div id="sponsors">
      		<h3>Macademia is generously funded by:</h3>
      		<ul>
      			<li><a href="http://www.acm.edu/index.html"><img src="images/logos_acm.png" alt="The American Colleges of the Midwest" /></a></li>
      			<li><a href="http://www.nsf.gov/"><img src="images/logos_nsf.png" alt="The National Science Foundation" /></a></li>
      			<li><a href="http://www.mellon.org/"><img src="images/logos_andrewMellon.png" alt="the Andrew Mellon Charitable Trust" /></a></li>
      		</ul>
      	</div>

      </div>

      <g:render template="/layouts/footer" />

    </div>

  </body>
</html>
