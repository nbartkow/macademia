<div>
  <div class="ttName medium">${target.fullName}</div>
  <div class="ttDesc medium aside">
    <b>dept:</b> ${target.department}<br/>
    <b>email:</b> <a href="mailto:${target.email}">${target.email}</a><br/>
    <b>interests:</b>
    <%= target.interests.collect({it.text}).join(', ') %>
  </div>

  <g:if test="${exact || close}">
  <div class="ttRel medium">
    <b>related to ${link.fullName} by:</b>
    <g:if test="${exact}">
      <g:each in="${exact.keySet()}" var="i">
        <li><i>${i.text}</i></li>
      </g:each>
    </g:if>
    <g:if test="${close}">
      <g:each in="${close.keySet()}" var="i">
        <li><i>${close[i]}</i>
        (similar to ${i.text})</li>
      </g:each>
    </g:if>
    </div>
  </g:if>
</div>