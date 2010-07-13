<g:setProvider library="jquery"/>

<div class="searchHeader atxt">
  <g:if test="${people || interests || requests}">
    Results matching <b>${query.encodeAsHTML()}</b>:
  </g:if>
  <g:else>
    No search results found for <b>${query.encodeAsHTML()}.</b>
  </g:else>
</div>

<g:if test="${people}">
  <div class="searchHeader atxt">Matching <b>people:</b> <a href="#/?searchPage=person_0"> (more)</a></div>
  <ul>
    <g:each in="${people}" var="p">
      <li>
        <g:link url = "#/?nodeId=p_${p.id}&navFunction=person&personId=${p.id}&searchBox=">${p.fullName}</g:link>
      </li>
    </g:each>
  </ul>
</g:if>

<g:if test="${interests}">
  <div class="searchHeader atxt">Matching <b>interests:</b> <a href="#/?searchPage=interest_0"> (more)</a></div>
  <ul>
    <g:each in="${interests}" var="i">
      <li>
        <g:link url = "#/?nodeId=i_${i.id}&navFunction=interest&interestId=${i.id}&searchBox=">${i.text}</g:link>
      </li>
    </g:each>
  </ul>
</g:if>

<g:if test="${requests}">
  <div class="searchHeader atxt">Matching <b>collaboration requests:</b> <a href="#/?searchPage=request_0"> (more)</a></div>
  <ul>
    <g:each in="${requests}" var="r">
      <li>
        <g:link url = "#/?nodeId=r_${r.id}&navFunction=request&interestId=${r.id}&searchBox=">${r.title}</g:link>
      </li>
    </g:each>
  </ul>
</g:if>