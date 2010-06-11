
  <div id="rightDiv">
    <div id="wrapper">
      <div id="main">
        <div id="account" class="btxt">
          <a href="#">Login</a>|<a href="#">Register</a>
        </div>
        <div id="mac_logo" class="atxt">
          <img src="${createLinkTo(dir: 'images', file: 'mac_logo.png')}"/><br/>
          <b>Macademia</b> connects colleagues who have shared interests.<br/>
        </div>


        <div id="instructions">
          <ul id="instruct_list">
						<li>
							<b>Click </b> on a name or interest to recenter.
						</li>
						<li>
							<b>Hover </b>over a name to show more info.
						</li>
						<li>
							<b>Search </b>below for a person for interest.
						</li>
					</ul>
        </div>


        <div id="searchDiv">
          <g:formRemote id="searchForm"
              name="searchForm"
              url="[action:'search', controller:'search']"
              update="searchResults">
            <br/>
            <input type="text" id="searchBox" name="searchBox" class="clearDefault" value="Search people or interests"/>
            <input type="submit" value="Search"/>
          </g:formRemote>
        </div>

        <div id="searchResults">
          &nbsp;
        </div>
      </div>
    </div>
    <div id="hide" class="btxt">
	  <a href="#">hide-></a>
	</div>
  </div>
