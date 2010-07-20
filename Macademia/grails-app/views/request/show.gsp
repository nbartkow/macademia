<div class="medium padded2" id="coll_request">
  <div id="coll_top_container">
    <h2 id="coll_title">
      ${fieldValue(bean: collaboratorRequest, field: "title")}
    </h2>
    <p id="coll_kw"><span>Keywords:</span>
      <g:set var="counter" value="${0}"/>
      <g:each in="${collaboratorRequest.keywords}" var="k">
        <g:set var="counter" value="${counter + 1}"/>
      <g:link url="#/?nodeId=i_${k.id}&navFunction=interest&interestId=${k.id}">${k.text}<g:if test="${counter!=collaboratorRequest.keywords.size()}">,</g:if></g:link>
    </g:each>
    </p>
    <p id="coll_created"><span>Created: </span> on <g:formatDate format="MMMMMMMM d, yyyy" date="${collaboratorRequest?.dateCreated}"/> by <g:link url="#/?nodeId=p_${collaboratorRequest?.creator?.id}">${collaboratorRequest?.creator?.encodeAsHTML()}</g:link></p>
    <p id="coll_due"><span>Due: </span><g:formatDate format="MMMMMMMM d, yyyy" date="${collaboratorRequest?.expiration}"/></p>
    <p id="coll_desc"><span>Description: </span>${fieldValue(bean: collaboratorRequest, field: "description")}</p>
  </div>

</div>