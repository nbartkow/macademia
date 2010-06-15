<%--
  Created by IntelliJ IDEA.
  User: henrycharlton
  Date: Jun 15, 2010
  Time: 2:29:06 PM
  To change this template use File | Settings | File Templates.
--%>

<g:javascript>
  $().ready(function() {
    macademia.clearSearch();
  });
</g:javascript>

<div id="searchDiv">
  <g:formRemote id="searchForm"
          name="searchForm"
          url="[action:'search', controller:'search']"
          update="searchResults">
    <br/>
    <input type="text" id="searchBox" name="searchBox" class="clearDefault" value="Search people or interests"/>
    <input type="submit" id="searchSubmitButton" value="Search"/>
  </g:formRemote>
</div>
<div id="collegeFilterButton" class="center">
  <input type="submit" class="jqModal" id="collegeFilterTrigger" value="Filter by college"/>
</div>
<div id="searchResults">
  &nbsp;
</div>