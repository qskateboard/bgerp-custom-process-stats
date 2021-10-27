package org.bgerp.plugin.custom;

import org.bgerp.util.Log;

/** May be started in Administration / Run tool. */
public class DemoTask implements Runnable {
    private static final Log log = Log.getLog();

    @Override
    public void run() {
        log.info("Started.");

    }
}
