<div class="dialog">
  <div id="rfcHeader" class="medium padded2 topBorder">
    <h2 id="rfcTitle" class="center">${fieldValue(bean: collaboratorRequest, field: "title")}</h2>
    <p class="center small">collaboration request by: <a href="#"><g:link url="#/?nodeId=p_${collaboratorRequest?.creator?.id}">${collaboratorRequest?.creator?.encodeAsHTML()}</g:link></a></p>
    <p class="center small padded">posted <g:formatDate date="${collaboratorRequest?.dateCreated}"/></p>
  </div>

  <div id="rfcDescription" class="medium padded2">
    <h3 class="padded">Description:</h3>
    <p id="descriptionParagraph">${fieldValue(bean: collaboratorRequest, field: "description")}</p>
  </div>

  <div id="rfcKeywords" class="medium padded2 endBorder">
    <h3 class=css/request_style.css"padded">Keywords:</h3>
    <p class="spacedSmall padded2"><ul>
    <g:each in="${collaboratorRequest.keywords}" var="k">
      <li><g:link controller="interest" action="show" id="${k.id}">${k.text?.encodeAsHTML()}</g:link></li>
    </g:each>
  </ul></p>
  </div>

</div>
