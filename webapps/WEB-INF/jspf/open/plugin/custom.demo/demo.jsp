<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<div class="center1020">
	<div id="title"><h1>${l.l('Демо открытый интерфейс')}</h1></div>
	<div>
		<html:form action="/open/plugin/custom/demo" styleClass="mt1 in-mr1">
			<input type="hidden" name="action" value="subscribe"/>
			<input type="text" name="email" placeholder="E-Mail" size="30"/>
			<button class="btn-grey" type="button" onclick="
				$$.ajax
					.post(this.form)
					.done(() => alert('${l.l('На указанный адрес выслана инструкция')}'));
			">${l.l("Подписка")}</button>
		</html:form>
	</div>
</div>