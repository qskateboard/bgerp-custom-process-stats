package org.bgerp.plugin.custom.pskateboard.action;

import static ru.bgcrm.dao.process.Tables.TABLE_PROCESS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.struts.action.ActionForward;

import ru.bgcrm.dao.process.ProcessDAO;
import ru.bgcrm.dao.process.ProcessTypeDAO;
import ru.bgcrm.dao.process.QueueDAO;
import ru.bgcrm.model.process.Queue;
import ru.bgcrm.model.BGException;
import ru.bgcrm.model.process.Process;
import ru.bgcrm.model.process.ProcessType;
import ru.bgcrm.servlet.ActionServlet.Action;
import ru.bgcrm.struts.action.BaseAction;
import ru.bgcrm.struts.form.DynActionForm;
import ru.bgcrm.util.sql.ConnectionSet;

import org.bgerp.plugin.custom.pskateboard.Plugin;
import org.bgerp.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;


@Action(path = "/user/plugin/custom.pskateboard/stats")
public class StatsAction extends BaseAction  {
    private static final String JSP_PATH = PATH_JSP_USER + "/plugin/" + Plugin.ID;

    public ActionForward unspecified(DynActionForm form, ConnectionSet conSet) throws Exception {
        List<ProcessType> items = new ProcessTypeDAO(conSet.getConnection()).getFullProcessTypeList();
        JSONArray array = new JSONArray();
        for(int i = 0; i < items.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("name", items.get(i).getTitle());
            json.put("id", items.get(i).getId());
            array.put(json);    
        }
        form.setResponseData("list", array);
        return html(conSet, form, JSP_PATH + "/stats.jsp");
    }

    public ActionForward render(DynActionForm form, ConnectionSet conSet) throws Exception {
        ProcessType type = new ProcessTypeDAO(conSet.getConnection()).getProcessType(Integer.parseInt(form.getParam("object")));
        int time = Integer.parseInt(form.getParam("time"));
        int count = 0, closed = 0;
        
        Log.getLog().info(time);
        List<Process> processes = getAllProcessList(conSet.getConnection(), form.getParamInt("object"));
        for(int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i); 
            if(time == 100 || (time != 100 && p.getCreateTime().getTime() >= getDate(time).getTime())){
                Log.getLog().info(p.getCreateTime().getTime());
                Log.getLog().info(getDate(time).getTime());
                count++;
                if(p.getStatusId() == 4 || p.getStatusId() == 6){
                    closed++;
                }
            }
        }

        JSONObject json = new JSONObject();
        json.put("time", form.getParam("time"));
        json.put("name", type.getTitle());
        json.put("count", count);
        json.put("closed", closed);

        JSONArray result = new JSONArray();
        result.put(json);
        form.setResponseData("list", result);
        return html(conSet, form, JSP_PATH + "/stats_result.jsp");
    }

    public List<Process> getAllProcessList(Connection con, int type) throws BGException {
        List<Process> processList = new ArrayList<Process>();
        try {
            String query = "SELECT process.* FROM " + TABLE_PROCESS + " AS process WHERE type_id=" + String.valueOf(type);
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                processList.add(ProcessDAO.getProcessFromRs(rs));
            }
            ps.close();

            return processList;
        } catch (SQLException e) {
            throw new BGException(e);
        }
    }

    public static Date getDate(int days) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        return new Date(System.currentTimeMillis() - (days * DAY_IN_MS));
      }
}
