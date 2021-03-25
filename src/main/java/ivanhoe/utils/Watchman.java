package ivanhoe.utils;

import org.apache.log4j.Logger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Created by lee on 15/02/16.
 */
public class Watchman extends TestWatcher {
    Logger log;

    public Watchman(Logger log) {
        this.log = log;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        log.error(description + " Failed", e);
    }

    @Override
    protected void succeeded(Description description) {
        log.info(description + " Passed");
    }
}
