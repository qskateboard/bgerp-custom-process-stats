package org.bgerp.plugin.custom.bitel.action.open;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.bgerp.plugin.custom.bitel.AntiSpam;
import org.bgerp.plugin.custom.bitel.Plugin;
import org.bgerp.plugin.custom.bitel.Subscription;

import ru.bgcrm.dao.ParamValueDAO;
import ru.bgcrm.dao.message.MessageDAO;
import ru.bgcrm.dao.process.ProcessLinkDAO;
import ru.bgcrm.dao.process.Tables;
import ru.bgcrm.model.BGException;
import ru.bgcrm.model.BGMessageException;
import ru.bgcrm.model.message.Message;
import ru.bgcrm.model.param.ParameterEmailValue;
import ru.bgcrm.model.process.Process;
import ru.bgcrm.model.process.ProcessLinkProcess;
import ru.bgcrm.servlet.ActionServlet.Action;
import ru.bgcrm.struts.action.BaseAction;
import ru.bgcrm.struts.action.ProcessAction;
import ru.bgcrm.struts.form.DynActionForm;
import ru.bgcrm.util.TimeUtils;
import ru.bgcrm.util.Utils;
import ru.bgcrm.util.sql.ConnectionSet;
import ru.bgerp.l10n.Localization;

@Action(path = "/open/plugin/custom.bitel/subscription")
public class SubscriptionAction extends BaseAction {
    private static final String JSP_PATH = PATH_JSP_OPEN_PLUGIN + "/" + Plugin.ID;

    // not more often request from a single IP as one per 10 min.
    private static final AntiSpam orderAntiSpam = new AntiSpam(10 * 60 * 1000L);

    public ActionForward calc(ActionMapping mapping, DynActionForm form, ConnectionSet conSet) throws Exception {
        int currencyId = form.getParamInt("currencyId");
        int sessionsId = form.getParamInt("sessionsId");
        var processIds = Utils.toIntegerSet(form.getParam("processIds"));

        BigDecimal cost = Subscription.getCost(currencyId, sessionsId, processIds);
        form.setResponseData("cost", cost);

        return data(conSet, null, JSP_PATH + "/calculate_result.jsp");
    }

    public ActionForward order(ActionMapping mapping, DynActionForm form, ConnectionSet conSet) throws Exception {
        orderAntiSpam(form); 

        int currencyId = form.getParamInt("currencyId");
        int sessionsId = form.getParamInt("sessionsId");
        var processIds = Utils.toIntegerSet(form.getParam("processIds"));

        var email = form.getParam("email", Utils::isValidEmail);

        var con = conSet.getConnection();

        // process creation with generation all the events
        var process = new Process();
        process.setTypeId(Subscription.PROCESS_TYPE_SUBSCRIPTION_ID);
        ProcessAction.processCreate(form, con, process);

        // may be extracted to a separated file in log4j.properties
        log.info("Created subscription process: %s", process.getId());

        // adding parameters without events
        var paramDao = new ParamValueDAO(con);
        paramDao.updateParamList(process.getId(), Subscription.PARAM_CURRENCY_ID, Set.of(currencyId));
        paramDao.updateParamList(process.getId(), Subscription.PARAM_SESSIONS_ID, Set.of(sessionsId));
        paramDao.updateParamList(process.getId(), Subscription.PARAM_LANG_ID, 
            Set.of(Subscription.langToId(Localization.getLang(form.getHttpRequest()))));
        paramDao.updateParamEmail(process.getId(), Subscription.PARAM_EMAIL_ID, 0, new ParameterEmailValue(email));

        var linkDao = new ProcessLinkDAO(con);
        for (int processId : processIds) {
            linkDao.addLink(new ProcessLinkProcess.Depend(process.getId(), processId));
        }

        // email(conSet, email, process, processIds);

        return status(con, form);
    }

    private void email(ConnectionSet conSet, String email, Process process, Set<Integer> processIds) throws BGException {
        var query = 
            "SELECT description FROM " + Tables.TABLE_PROCESS + " AS p " + 
            "WHERE id IN (" + Utils.toString(processIds) + ")";
        

        var m = new Message()
            .withProcessId(process.getId())
            .withTypeId(Subscription.MESSAGE_TYPE_EMAIL_ID)
            .withDirection(Message.DIRECTION_OUTGOING)
            .withFromTime(new Date())
            .withSubject(l.l("subscription.main.subject"))
            .withText(l.l("subscription.mail.text"))
            .withTo(email);
        new MessageDAO(conSet.getConnection()).updateMessage(m);
    }

    private void orderAntiSpam(DynActionForm form) throws BGMessageException {
        var ip = form.getHttpRequestRemoteAddr();
        if (Utils.isBlankString(ip))
            throw new BGMessageException("IP адрес не определён.");

        var wait = orderAntiSpam.getWaitTimeout(ip);
        if (wait > 0)
            throw new BGMessageException("Следующий заказ с этого IP можно сделать через: %s", 
                TimeUtils.formatDeltaTime(wait));
    }
}
