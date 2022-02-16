<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jspf/taglibs.jsp" %>

<html:form action="/user/plugin/custom.pskateboard/stats" style="width: 100%;">
    <h1>Статистика '${form.response.data.list.get(0).getString("name")}'</h1>

    <table style="width: 100%;" class="data mt1">
        <tr>
            <td>&#160;</td>
        </tr>
        <tr>
            <td> Всего процессов: ${form.response.data.list.get(0).getInt("count")}</td>
        </tr>
        <tr>
            <td> Закрыто: ${form.response.data.list.get(0).getInt("closed")}</td>
        </tr>
    </table>
</html:form> 