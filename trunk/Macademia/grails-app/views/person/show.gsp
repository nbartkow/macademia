<%--
  Created by IntelliJ IDEA.
  User: isparling
  Date: Aug 4, 2009
  Time: 2:46:41 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="medium padded2" id="profile">
  <div id="pf_top_container">
    <div id="pf_left">
      <g:if test="${person.imageSubpath}">
        <img src="/Macademia/images/db/large/${person.imageSubpath}"/>
      </g:if>
    </div>
    <div id="pf_top_container_no_img">
      <h2 id="pf_name_no_img">${person.fullName}</h2>
      <h3 id="pf_dept_no_img">${person.department}</h3>
      <h4 id="pf_email">
      <g:link url="mailto:${person.email}">${person.email}</g:link>
      </h4>
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
    <ul class="atxt">
      <g:each in="${collaboratorRequests}" var="collaboratorRequest">
        
          <li><g:link url = "#/?nodeId=r_${collaboratorRequest.id}&navFunction=request&requestId=${collaboratorRequest.id}&searchBox=">${collaboratorRequest.title}</g:link></li>

      </g:each>
    </ul>
  </div>
  </g:if>
  <g:if test="${person.links}">
    <div id="pf_links">
        <h4>Links</h4>
        <ul class="atxt">
          ${person.links}
        </ul>
    </div>
  </g:if>
</div>

