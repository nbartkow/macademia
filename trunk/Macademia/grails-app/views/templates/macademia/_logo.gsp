<%--
  Created by IntelliJ IDEA.
  User: henrycharlton
  Date: Jun 15, 2010
  Time: 4:16:27 PM
  To change this template use File | Settings | File Templates.
--%>


<div id="logo">
  <a href="/Macademia/${params.group}">
    <p:image id="logoImage" src='macademia-logo.png'/>
    <div class="tagline">Connecting colleagues who share research interests.</div>
  </a>
    <div class="about"><a href="#">About / help...</a></div>
    <g:if test="${params.group == 'glca'}">
      <div class="inst_logo">
        <a href="http://glca.org"><p:image src="glca_logo.jpg" alt="The Great Lakes Colleges Assocation"/></a>
      </div>
    </g:if>
</div>
