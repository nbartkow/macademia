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
    <div id="pf_right">
      <h2 id="pf_name">${person.fullName}</h2>
      <h3 id="pf_dept">${person.department}</h3>
    </div>
  </div>
  <div id="pf_interests">
    <h4>Interests</h4>
    <p class="atxt">
      <g:each in="${interests}" var="interest">
        <g:link url="#/?nodeId=i_${interest.id}&navFunction=interest&interestId=${interest.id}">
          ${interest.text}
        </g:link>
      </g:each>
    </p>
  </div>
  <div id="pf_requests">
    <h4>Collaborator Requests</h4>
    <ul class="atxt">
      <g:each in="${collaboratorRequests}" var="collaboratorRequest">
        <g:link url="[controller:'request',action:'show',id:collaboratorRequest.id]">
          <li>${collaboratorRequest.title}</li>
        </g:link>
      </g:each>
    </ul>
  </div>
  <div id="pf_links">
      <h4>Links</h4>
      <ul class="atxt">
          <li><a href="#">Homepage</a></li>
          <li><a href="#">Department website</a></li>
          <li><a href="#">Facebook</a></li>
      </ul>
  </div>
</div>

