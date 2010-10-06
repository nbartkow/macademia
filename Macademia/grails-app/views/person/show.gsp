<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 4, 2009
  Time: 2:46:41 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="medium padded2" id="profile">
  <div id="pf_top_container">
    <div>
    <div id="pf_left">
      <g:if test="${person.imageSubpath}">
        <img src="/Macademia/images/db/large/${person.imageSubpath}"/>
      </g:if>
    </div>
    <div id="pf_identity">
      <h3 id="pf_name">${person.fullName}</h3>
      <g:if test="${person.department && person.title}">
        <h3 id="pf_dept">${person.title.encodeAsHTML()} of ${person.department.encodeAsHTML()}</h3>
      </g:if>
      <g:elseif test="${person.department}">
        <h3 id="pf_dept">${person.department.encodeAsHTML()}</h3>
      </g:elseif>
      <g:elseif test="${person.title}">
        <h3 id="pf_dept">${person.title.encodeAsHTML()}</h3>
      </g:elseif>
      <h3 id="pf_email">
      <g:link url="mailto:${person.email}">${person.email}</g:link>
      </h3>
    </div>
  </div>
  </div>
  <div id="pf_interests">
    <h4>Interests:</h4>
    <p class="atxt">
      <g:set var="counter" value="${0}"/>
      <g:each in="${interests}" var="interest" >

        <g:set var="counter" value="${counter + 1}"/>
        <g:link url="#/?nodeId=i_${interest.id}&navFunction=interest&interestId=${interest.id}">${interest.text}<g:if test="${counter!=interests.size()}">,</g:if></g:link>
      </g:each>
    </p>
  </div>
  <g:if test="${collaboratorRequests}">
  <div id="pf_requests">
    <h4>Collaborator Requests:</h4>
    <ul class="styledList atxt">
      <g:each in="${collaboratorRequests}" var="collaboratorRequest">
        
          <li><g:link url = "#/?nodeId=r_${collaboratorRequest.id}&navFunction=request&requestId=${collaboratorRequest.id}">${collaboratorRequest.title}</g:link></li>

      </g:each>
    </ul>
  </div>
  </g:if>
  <g:if test="${person.links}">
    <div id="pf_links">
        <h4>Links</h4>
        <ul class="styledList atxt">
          ${person.links}
        </ul>
    </div>
  </g:if>

<g:if test= "${request.authenticated && request.authenticated.canEdit(person)}">
  <div>
  <h4>Account:</h4>
      <ul class="styledList atxt">
      <li>
      <g:link url="[controller:'account',action:'modaledituser', id:person.id ]">Edit Profile</g:link>
      </li>
      </ul>
</g:if>
  </div>
</div>

