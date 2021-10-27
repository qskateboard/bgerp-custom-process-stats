<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<c:if test="${uri.startsWith('/open/custom')}">
	<c:import url="/open/plugin/custom/demo.do?action=show"/>
</c:if>