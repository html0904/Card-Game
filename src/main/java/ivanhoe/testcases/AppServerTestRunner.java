package ivanhoe.testcases;

import ivanhoe.utils.Watchman;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by hyunminlee on 2016-02-04.
 */
public class AppServerTestRunner {

    private static final Logger log = Logger.getLogger(AppServerTestRunner.class);

    @Rule
    public TestWatcher watchman = new Watchman(log);

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AppServerTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
