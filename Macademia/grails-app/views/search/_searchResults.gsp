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
      <g:link controller="person" action="jit" params="[id:p.id]">${p.name}</g:link>
    </li>
</g:each>
</g:if>

<g:if test="${interests}">
<div class="searchHeader">Matching <b>interests:</b></div>
<g:each in="${interests}" var="i">
    <li>
      <g:link controller="interest" action="jit" params="[id:i.id]">${i.text}</g:link>
    </li>
</g:each>
</g:if>