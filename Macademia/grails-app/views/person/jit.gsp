<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 21, 2009
  Time: 4:14:16 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <g:include view="/layouts/headers.gsp"/>
  <link rel="stylesheet" href="${resource(dir:'css',file:'jqModal.css')}" />
  <g:javascript>
    $().ready(function(){
       init('person', ${person.id});
       macademia.clearSearch();
       macademia.pageLoad();
       macademia.nav();
       macademia.collegeFilter();
    });
  </g:javascript>
</head>
<body>
<div class="jqmWindow" id="modalDialog">
			<div id="colleges">
					<div id="editColleges" class="medium">
						<div id="collegeSearch" class="center padded2">
							<input type="text" value="Type college name"/>
							<input type="submit" value="Add"/>
						</div>
						<div id = "addClear" class="padded2">
							<input id = "add" type="submit" value="Add all"/ >
							<input id = "clear"type="submit" value="Clear list"/>
						</div>
						<div id="selectedColleges" class="medium btxt lined">
							<ul>
								<li>College A <a href="#" class="delete">(x)</a></li>
								<li>College B <a href="#" class="delete">(x)</a></li>
								<li>College C <a href="#" class="delete">(x)</a></li>
								<li>College D <a href="#" class="delete">(x)</a></li>
								<li>College E <a href="#" class="delete">(x)</a></li>
								<li>College F <a href="#" class="delete">(x)</a></li>
								<li>College G <a href="#" class="delete">(x)</a></li>
								<li>College H <a href="#" class="delete">(x)</a></li>
								<li>College I <a href="#" class="delete">(x)</a></li>
								<li>College J <a href="#" class="delete">(x)</a></li>
								<li>College K <a href="#" class="delete">(x)</a></li>
								<li>College L <a href="#" class="delete">(x)</a></li>
								<li>College M <a href="#" class="delete">(x)</a></li>
								<li>College N <a href="#" class="delete">(x)</a></li>
								<li>College O <a href="#" class="delete">(x)</a></li>
								<li>College P <a href="#" class="delete">(x)</a></li>
								<li>College Q <a href="#" class="delete">(x)</a></li>
								<li>College R <a href="#" class="delete">(x)</a></li>
								<li>College S <a href="#" class="delete">(x)</a></li>
								<li>College T <a href="#" class="delete">(x)</a></li>
								<li>College U <a href="#" class="delete">(x)</a></li>
								<li>College V <a href="#" class="delete">(x)</a></li>
								<li>College W <a href="#" class="delete">(x)</a></li>
								<li>College X <a href="#" class="delete">(x)</a></li>
								<li>College Y <a href="#" class="delete">(x)</a></li>
								<li>College Z <a href="#" class="delete">(x)</a></li>
							</ul>
						</div>
					</div>
				</div>

		</div>
<g:render template="../layouts/components"/>
<div id="infovis">
  &nbsp;
  <div id="show" class="btxt">
    <a rel="address:/" href="#show"><-show</a>
  </div>
</div>
<div id="extendedInfo">
  <g:render template="../layouts/rightNav"/>
</div>
</body>
</html>
