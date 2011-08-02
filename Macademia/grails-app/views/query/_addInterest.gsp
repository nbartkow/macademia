<%--
  Created by IntelliJ IDEA.
  User: research
  Date: 8/1/11
  Time: 4:49 PM
  To change this template use File | Settings | File Templates.
--%>

<g:javascript>
    $(document).ready(function() {


        var addNewInterest = function(interestName, id) {
            if ($("#queryIds").val() == ""){
                $("#queryIds").val(id);
            } else {
                var ids = $("#queryIds").val().split("_");
                if ($.inArray(id, ids)+1 == 0){
                    $("#queryIds").val($("#queryIds").val() +"_"+ id);
                }
            }
            $("#queryIdForm").submit();
        };

        $("#interestQuery").editAutocomplete({
              multiple : true,
              search: function() {
                  // custom minLength
                  var term = macademia.autocomplete.extractLast(this.value);
                  if (term.length < 1) {
                      return false;
                  }
              },
              focus: function() {
                  // prevent value inserted on focus
                  return false;
              },
              select: function(event, ui) {
                  this.value = "";
                  addNewInterest(ui.item.value, ui.item.data[0]);
              }
            },  macademia.makeActionUrlWithGroup(macademia.retrieveGroup(),'autocomplete', 'index') + "?klass=interest");


        $("a.removeInterest").click(function(){
            $(this).parent().remove();
            var ids = [];
            $(".addedInterestId").each(function(i){
                ids.push($(this).parent().find(".addedInterestId").val());
            });
            var idString = ids[0];
            if (ids.length > 1){
                for (var i=1; i < ids.length; i++){
                    idString += "_"+ids[i]
                }
            }
            $("#queryIds").val(idString);
            $("#queryIdForm").submit();
         });

    });
</g:javascript>
<div>
    <label for="interestQuery">Query Interests</label>

    <div class="addedInterests">
        <g:each var="interest" in="${interests}">
            <div class="addedInterestDiv">
                <div class="addedInterest">${interest[1]}</div>
                <input class="addedInterestId" value="${interest[0]}" type="hidden"/>
                <a href="#" class='removeInterest'>(remove)</a>
            </div>
        </g:each>
        <g:form id="queryIdForm" url="../../${group}/query/show" method="get">
            <input id="queryIds" type="hidden" value="${queryIdsString}" name="queryIds"/>
        </g:form>

    <input id="interestQuery" type="search" name="interestQuery" placeholder="Add new interest"/>
    </div>
</div>


