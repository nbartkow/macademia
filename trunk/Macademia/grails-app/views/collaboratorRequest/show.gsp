<%@ page import="org.macademia.CollaboratorRequest" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'jqModal.css')}"/>
  <g:set var="entityName" value="${message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest')}"/>
  <title><g:message code="Collaboration Request" args="[entityName]"/></title>
  <g:include view="/layouts/headers.gsp"/>
  <g:javascript>
    $().ready(function() {
      macademia.pageLoad();
      macademia.nav();
      macademia.collegeFilter();
      macademia.startSearch();
    });
  </g:javascript>

</head>
<body>
<div class="body">
  <g:render template="../layouts/components"/>
  <g:render template="../templates/macademia/collegeFilterDialog"/>
  <div id="infovis">
    &nbsp;
    <div id="show" class="btxt">
      <a rel="address:/" href="#show"><-show</a>
    </div>
  </div>
  <div id="extendedInfo">
    <div id="rightDiv">
      <div id="wrapper">
        <div id="main">
          <div id="account" class="btxt">
            <a rel="address:/" href="#">Login</a>|<a rel="address:/" href="#">Register</a>
          </div>
          <g:render template="../templates/macademia/logo"/>
          <g:render template="../templates/macademia/searchBar"/>
          <div id="searchDiv">
            <g:formRemote id="searchForm"
                    name="searchForm"
                    url="[action:'search', controller:'search']"
                    update="rightContent">
              <br/>
              <input type="text" id="searchBox" name="searchBox" class="clearDefault" value="Search people or interests"/>
              <input type="submit" id="searchSubmitButton" value="Search"/>
            </g:formRemote>
          </div>
          <div class="dialog hidable">
            <div id="rfcHeader" class="medium padded2 topBorder">
              <h2 id="rfcTitle" class="center">${fieldValue(bean: collaboratorRequestInstance, field: "title")}</h2>
              <p class="center small">collaboration request by: <a href="#"><g:link controller="person" action="show" id="${collaboratorRequestInstance?.creator?.id}">${collaboratorRequestInstance?.creator?.encodeAsHTML()}</g:link></a></p>
              <p class="center small padded">posted <g:formatDate date="${collaboratorRequestInstance?.dateCreated}"/></p>
            </div>

            <div id="rfcDescription" class="medium padded2">
              <h3 class="padded">Description:</h3>
              <p id="descriptionParagraph">${fieldValue(bean: collaboratorRequestInstance, field: "description")}</p>
            </div>

            <div id="rfcKeywords" class="medium padded2 endBorder">
              <h3 class=css/request_style.css"padded">Keywords:</h3>
              <p class="spacedSmall padded2"><ul>
              <g:each in="${collaboratorRequestInstance.keywords}" var="k">
                <li><g:link controller="interest" action="show" id="${k.id}">${k?.encodeAsHTML()}</g:link></li>
              </g:each>
            </ul></p>
            </div>

          </div>
        </div>
      </div>
      <div id="hide" class="btxt">
        <a rel="address:/" href="#hide">hide-></a>
      </div>
    </div>
  </div>
</div>
</body>
</html>
