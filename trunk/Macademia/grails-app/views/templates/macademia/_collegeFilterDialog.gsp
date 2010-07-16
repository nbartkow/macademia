<%--
  Created by IntelliJ IDEA.
  User: henrycharlton
  Date: Jun 15, 2010
  Time: 4:33:59 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="padded medium btxt">
  <div id="closeCollegeFilter">
    <a href ="#">X</a>
  </div>
    <div id="editColleges">

      <div id="collegeSearch" class="center padded2">
        <input id="collegeSearchAuto" type="text" value="Type college name" class="clearDefault"/>
        <input id= "addCollege" type="submit" value="Add"/>
      </div>
      <div id="addClear" class="padded2">
        <input id="add" type="submit" value="Add all"/>
        <input id="clear" type="submit" value="Clear list"/>
      </div>
      <div id="selectedColleges" class="medium lined">
          <div id = "clearMessage" class = "center" style = "display:none"> Visualization cannot display a filter of 0 colleges </div>
          <ul>
            <g:each in="${institutions}" var="c">
              <li id="c_${c.id}" class="college" style = "display:none">${c.name} <a href="#">(x)</a></li>
            </g:each>
          </ul>
      </div>
      <div id="submitColleges">
          <input id ="select" type="submit" value = "Submit"/>
      </div>
    </div>

</div>