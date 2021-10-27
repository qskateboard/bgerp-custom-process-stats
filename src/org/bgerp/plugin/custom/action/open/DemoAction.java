package org.bgerp.plugin.custom.action.open;

import org.apache.struts.action.ActionForward;
import org.bgerp.plugin.custom.Plugin;

import ru.bgcrm.servlet.ActionServlet.Action;
import ru.bgcrm.struts.action.BaseAction;
import ru.bgcrm.struts.form.DynActionForm;
import ru.bgcrm.util.sql.ConnectionSet;

@Action(path = "/open/plugin/custom/demo")
public class DemoAction extends BaseAction {
    private static final String PATH_JSP = Plugin.PATH_JSP_OPEN;

    public ActionForward subscribe(DynActionForm form, ConnectionSet conSet) throws Exception {


        return json(conSet, form);
    }

    public ActionForward show(DynActionForm form, ConnectionSet conSet) throws Exception {


        return html(conSet, null, PATH_JSP + "/demo.jsp");
    }
}