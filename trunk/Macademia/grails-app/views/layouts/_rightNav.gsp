<div id="show" class="btxt">
  <a href="#/?navVisibility=true"><-show</a>
</div>
<div id="rightDiv">
  <div id="wrapper">
    <div id="main">
      <div id="account" class="btxt right">
        <a id="loginButton" class="right">Login</a> | <a id="registerButton" class="right">Register</a>
      </div>
      <div id="mac_logo" class="atxt center">
        <img id="logoImage" src="${createLinkTo(dir: 'images', file: 'mac_logo.png')}"/><br/>
        <div id="description"><b>Macademia</b> connects colleagues who have shared interests.<br/></div>
      </div>


      <g:render template="../templates/macademia/searchBar"/>
      <div id="rightContent" class="medium">
        <ul id="instruct_list">
          <li>
            <b>Click</b> on a name or interest to recenter.
          </li>
          <li>
            <b>Hover</b> over a name to show more info.
          </li>
          <li>
            <b>Search</b> below for a person or interest.
          </li>
        </ul>

      <g:formRemote id="" class="center"
              name="a"
              url="[action:'show', controller:'collaboratorRequest', id:'1']"
              update="rightContent">
        <br/>
        <input type="submit" id=id value="Collaborator Request View">

      </g:formRemote>

      </div>





      <div id="loginDialog" class="jqmWindow">
        <h3>(to be filled with login page)</h3>
      </div>
      <div id="registerDialog" class="jqmWindow">
        <h3>(to be filled with registration page)</h3>
      </div>
    </div>
  </div>
  <div id="hide" class="btxt">
    <a href= "#/?navVisibility=false">hide-></a>
  </div>
</div>
