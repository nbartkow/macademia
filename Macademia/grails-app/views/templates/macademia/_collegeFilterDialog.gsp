<%--
  Created by IntelliJ IDEA.
  User: henrycharlton
  Date: Jun 15, 2010
  Time: 4:33:59 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="padded medium btxt" id="filterModal">
  <h3>Select Schools:</h3>
  <div id="closeCollegeFilter">
    <a href ="#"><p:image src="close_icon.gif"/></a>
  </div>
    <div id="editColleges">
      <div id="collegeFilterEditButtons" class="padded2">
        <input id="addAllColleges" type="submit" value="add all"/>
        <input id="clearAllColleges" type="submit" value="remove all"/>
        
        <input id="collegeSearchAuto" type="text" prompt="Type college name" class="clearDefault"/>        
        <input id="addCollege" type="submit" value="add"/>
      </div>
      <div id="selectedColleges" class="medium">
          %{--<div id = "clearMessage" class = "center" style = "display:none"> Visualization cannot display a filter of 0 colleges </div>--}%
          <ul>
            <g:each in="${institutions}" var="c">
              <li id="c_${c.id}" class="college" style = "display:none">
                <a href="#"><p:image src="close_icon.gif"/></a>
                ${c.name}
              </li>
            </g:each>
          </ul>
      </div>
      <div id="submitColleges">
          <input id ="selectColleges" type="submit" value = "save schools and close"/>
      </div>
    </div>

</div>