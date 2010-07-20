<div id="interestPage" class="medium padded2">
  <div id="interest_top_container">
    <div id="interest_info">
      <h2 id="interest_selected"><img id="tagPicture" src="${createLinkTo(dir: 'images', file:'int_tag.png')}"/>${interest.text}</h2>
      <div id="interest_related">
        <h3>Related interests:</h3>
        <ul>
          <g:each in="${relatedInterests}" var="interest">
            <g:if test="${interest != null}">
              <li>
                <g:link url="#/?nodeId=i_${interest.id}&navFunction=interest&interestId=${interest.id}">
                  ${interest.text}
                </g:link>
              </li>
            </g:if>
          </g:each>

        </ul>
      </div>
      <div id="interest_people">
        <h3>People with this interest:</h3>
        <ul>
          <g:each in="${peopleWithInterest}" var="person">
            <li>
              <g:link url="#/?nodeId=p_${person.id}&navFunction=person&personId=${person.id}">
                ${person.fullName}
              </g:link>
            </li>
          </g:each>
        </ul>
      </div>
    </div>
  </div>
</div>
