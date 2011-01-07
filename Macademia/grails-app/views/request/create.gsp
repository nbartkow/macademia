<div id="createRequestContainer">
  <g:form action="save">
    <div id="rfcForm">
      <g:if test="${collaboratorRequest.id}">
        <g:hiddenField name="requestId" value="${collaboratorRequest.id}"/> 
      </g:if>

      <div class="registerLeft"><label for="title">Title<span>Name for collaborator request</span></label></div>
      <div class="registerRight value ${hasErrors(bean: collaboratorRequest, field: 'title', 'errors')}">
        <g:textField name="title" value="${collaboratorRequest?.title}"/>
      </div>
      <div class="clear"></div>

      <div class="registerLeft"><label for="expiration">Due Date<span>Date by which collaborators are needed</span></label></div>
      <div class="registerRight value ${hasErrors(bean: collaboratorRequest, field: 'expiration', 'errors')}">
        <g:datePicker name="expiration" precision="day" value="${collaboratorRequest?.expiration}"/>
      </div>
      <div class="clear"></div>

      <div class="registerLeft"><label for="description">Description<span>Description of request</span></label></div>
      <div class="registerRight value ${hasErrors(bean: collaboratorRequest, field: 'description', 'errors')}">
        <g:textArea id="requestDescriptionBox" name="description" rows="10" cols="80" value="${collaboratorRequest?.description}"/>
      </div>
      <div class="clear"></div>

      <div class="registerLeft"><label for="keywords">Keywords<span>keywords related to the request</span></label></div>
      <div class="registerRight value ${hasErrors(bean: collaboratorRequest, field: 'keywords', 'errors')}">
        <g:textArea id="requestKeywordsBox" name="keywords" value="${keywords?.encodeAsHTML()}"/>
      </div>
      <div class="clear"></div>
    </div>
    <div id="submitRequest" class="center">
      <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </div>
  </g:form>
  <g:javascript >
    $().ready(function () {
        macademia.autocomplete.initEditRequest();
        $("#listRequestDialog").jqmHide();
    });
</g:javascript>
</div>
