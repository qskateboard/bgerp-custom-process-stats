package org.bgerp.plugin.custom.pskateboard;

import ru.bgcrm.plugin.Endpoint;
import ru.bgcrm.util.ParameterMap;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class Plugin extends ru.bgcrm.plugin.Plugin {
    public static final String ID = "custom.pskateboard";

    private static final String JSP_PATH = PATH_JSP_USER_PLUGIN + "/" + Plugin.ID;

    public Plugin() {
        super(ID);
    }

    @Override
    public boolean isEnabled(ParameterMap config, String defaultValue) {
        return true;
    }

    @Override
    public void init(Connection con) throws Exception {
    }

    @Override
    protected Map<String, List<String>> loadEndpoints() {
        return Map.of(
            Endpoint.USER_MENU_ITEMS, List.of(JSP_PATH + "/menu_items.jsp")
        );
    }

}
