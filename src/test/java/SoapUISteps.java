import org.junit.Test;
import utilities.SoapUIUtil;

public class SoapUISteps {

        private SoapUIUtil util;

    @Test
    //@Given("the user runs the testcase <string>")
    public void runTestSuite() throws Exception {
        util = new SoapUIUtil();
        util.runTestSuite("ProviderRules");
    }

}
