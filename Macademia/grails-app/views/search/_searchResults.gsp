<g:setProvider library="jquery"/>


<div class="searchHeader">
  <g:if test="${people || interests}">
    Results matching <b>${query.encodeAsHTML()}</b>:
  </g:if>
  <g:else>
    No search results found for <b>${query.encodeAsHTML()}.</b>
  </g:else>
</div>

<g:if test="${people}">
<div class="searchHeader">Matching <b>people:</b></div>
<g:each in="${people}" var="p">
    <li>
      <g:link url = "/Macademia/person/jit/#?nodeId=p_${p.id}&navVisibility=true">${p.fullName}</g:link>
    </li>
</g:each>
</g:if>

<g:if test="${interests}">
<div class="searchHeader">Matching <b>interests:</b></div>
<g:each in="${interests}" var="i">
    <li>
      <g:link url = "/Macademia/interest/jit/#?nodeId=i_${i.id}&navVisibility=true">${i.text}</g:link>
    </li>
</g:each>
</g:if>