
<%@ page import="org.macademia.CollaboratorRequest" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
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
            <g:hasErrors bean="${collaboratorRequestInstance}">
            <div class="errors">
                <g:renderErrors bean="${collaboratorRequestInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title"><g:message code="collaboratorRequest.title.label" default="Title" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequestInstance, field: 'title', 'errors')}">
                                    <g:textField name="title" value="${collaboratorRequestInstance?.title}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="expiration"><g:message code="collaboratorRequest.expiration.label" default="Expiration" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequestInstance, field: 'expiration', 'errors')}">
                                    <g:datePicker name="expiration" precision="day" value="${collaboratorRequestInstance?.expiration}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="description"><g:message code="collaboratorRequest.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequestInstance, field: 'description', 'errors')}">
                                    <g:textField name="description" value="${collaboratorRequestInstance?.description}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="creator"><g:message code="collaboratorRequest.creator.label" default="Creator" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: collaboratorRequestInstance, field: 'creator', 'errors')}">
                                    <g:select name="creator.id" from="${org.macademia.Person.list()}" optionKey="id" value="${collaboratorRequestInstance?.creator?.id}"  />
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
