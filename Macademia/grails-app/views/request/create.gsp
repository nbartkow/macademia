
<%@ page import="org.macademia.CollaboratorRequest" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
      <!-- Jquery-UI for autocomplete, etc. -->
        <g:javascript library="jquery"/>
        <link type="text/css" rel="stylesheet" href="${createLinkTo(dir:"css",file:"ui-lightness/jquery-ui-1.8.2.custom.css")}">
        <g:javascript src="jquery/jquery-ui-1.8.2.custom.min.js"/>
        <g:javascript src="lib.macademia.js"/>
        <g:javascript src="lib.macademia.autocomplete.js"/>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${collaboratorRequest}">
            <div class="errors">
                <g:renderErrors bean="${collaboratorRequest}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save">
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title"><g:message code="collaboratorRequest.title.label" default="Title" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequest, field: 'title', 'errors')}">
                                    <g:textField name="title" value="${collaboratorRequest?.title}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="expiration"><g:message code="collaboratorRequest.expiration.label" default="Expiration" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequest, field: 'expiration', 'errors')}">
                                    <g:datePicker name="expiration" precision="day" value="${collaboratorRequest?.expiration}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="description"><g:message code="collaboratorRequest.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequest, field: 'description', 'errors')}">
                                    <g:textArea name="description" value="${collaboratorRequest?.description}" />
                                </td>
                            </tr>

                            <tr id= keywordsField>
                              <td valign="top" class="name"><label for="keywords"><g:message code="KEYWAoRDS" /></label></td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequest, field: 'keywords', 'errors')}">
                                <textarea id="keywords" class="textInput easyInput" name="keywords" value="${collaboratorRequest?.keywords}"></textarea>
                              </td>
                            </tr>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
