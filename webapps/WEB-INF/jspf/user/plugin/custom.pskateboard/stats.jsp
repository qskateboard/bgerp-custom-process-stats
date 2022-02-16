<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jspf/taglibs.jsp" %>

<h2>Формирование статистики</h2>
<html:form action="/user/plugin/custom.pskateboard/stats">
    <input type="hidden" name="action" value="render">
    <ui:combo-single hiddenName="object" widthTextValue="8em" prefixText="Тип">
        <jsp:attribute name="valuesHtml">
            <c:forEach begin="0" end="${form.response.data.list.length() - 1 }" var="i">
                <li value="${form.response.data.list.get(i).getInt('id')}">${form.response.data.list.get(i).getString('name')}</li>
            </c:forEach>
        </jsp:attribute>
    </ui:combo-single>
    <br>
    <br>
    <ui:combo-single hiddenName="time" widthTextValue="8em" prefixText="Временной промежуток">
        <jsp:attribute name="valuesHtml">
            <li value="1">1 день</li>
            <li value="3">3 дня</li>
            <li value="7">7 дней</li>
            <li value="30">30 дней</li>
            <li value="100">Всё время</li>
        </jsp:attribute>
    </ui:combo-single>

    <ui:button type="out" styleClass="ml1" onclick="
		$$.ajax.post(this.form, {control: this}).done(() => {
			$$.ajax.load(this.form, '#statsResult');
		});"/>

</html:form> 

<div id="statsResult" class="pl1" style="height: 100%; width: 100%; display: table-row;">
    <%--  сюда вставляются DIV ки --%>
    &#160;
</div>

<shell:title text="Статистика"/>