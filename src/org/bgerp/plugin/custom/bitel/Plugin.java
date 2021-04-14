package org.bgerp.plugin.custom.bitel;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import ru.bgcrm.event.EventProcessor;
import ru.bgcrm.event.ParamChangedEvent;
import ru.bgcrm.plugin.Endpoint;
import ru.bgcrm.util.ParameterMap;

/**
 * BiTel's custom plugin.
 * @author Shamil Vakhitov
 */
public class Plugin extends ru.bgcrm.plugin.Plugin {
    public static final String ID = "custom.bitel";

    public Plugin() {
        super(ID);
    }

    @Override
    public boolean isEnabled(ParameterMap config, String defaultValue) {
        return true;
    }

    @Override
    protected Map<String, List<String>> loadEndpoints() {
        return Map.of(Endpoint.JS, List.of(Endpoint.getPathPluginJS(ID)));
    }

    @Override
    public void init(Connection con) throws Exception {
        super.init(con);

        EventProcessor.subscribe((e, conSet) -> {
            var paramId = e.getParameter().getId();
            if (paramId == Subscription.PARAM_PRICE_EUR_ID || paramId == Subscription.PARAM_PRICE_RUB_ID) {
                Subscription.flush();
            }
        }, ParamChangedEvent.class);
    }
}
