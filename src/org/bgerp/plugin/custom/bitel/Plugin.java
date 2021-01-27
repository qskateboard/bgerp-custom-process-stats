package org.bgerp.plugin.custom.bitel;

import ru.bgcrm.util.ParameterMap;

/**
 * BiTel custom plugin.
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
}
