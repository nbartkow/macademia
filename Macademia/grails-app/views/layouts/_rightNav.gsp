
  <div id="rightDiv">
    <div id="mac_logo" class="medium aside">
    <img src="${createLinkTo(dir: 'images', file: 'mac_logo.png')}"/><br/>
      <b>Macademia</b> connects colleagues who have shared interests.<br/>
    </div>


    <div id="instructions" class="medium">
      <li><b>Click</b> on a name or interest to recenter.</li>
      <li><b>Hover</b> over a name to show more info.</li>
      <li><b>Search</b> below for a person or interest.</li>
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

    <div id="searchResults" class="medium">&nbsp;</div>

    <div id="surveyLink" class="medium aside">
        To update or add to your research interests, please visit the
        <a href="http://141.140.167.130:3000/">research interests survey site</a>.
        After updating your interests, please email <a href="mailto:ssen@macalester.edu">
        Shilad Sen</a> to update this visualization. 
    </div>

    <div id="teamCredits" class="medium aside">
      The Macademia project is led by
      <a href="mailto:brown@macalester.edu">Kendrick Brown</a>,
      <a href="mailto:michelfelder@macalester.edu">Diane Michelfelder</a>,
      <a href="mailto:ssen@macalester.edu">Shilad Sen</a>,
      <a href="mailto:shandy@macalester.edu">Dianna Shandy</a>, and
      <a href="mailto:strauss@macalester.edu">Jaine Strauss</a>
      and supported by the Mellon Foundation.
    </div>


    <div id="webCredits" class="medium aside">
      <a href="mailto:ssen@macalester.edu">Shilad Sen</a> and
      <a href="mailto:esparling@macalester.edu">Isaac Sparling</a>
      developed this website.
    </div>
  </div>
