package utilities;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.StandaloneSoapUICore;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.List;

public class SoapUIUtil {

    protected class ContextPropConstant {
        private static final String RESPONSE = "Response";
        private static final String REQUEST = "Request";
        private static final String ENDPOINT = "Endpoint";
        private static final String DOMAIN = "Domain";
    }

    private TestRunner runner;
    private WsdlProject project;
    private List<TestSuite> suiteList = new ArrayList<TestSuite>();
    private List<TestCase> caseList = new ArrayList<TestCase>();
    private static final String PROJECT_URL = "soapui/PF_SoapUI_Regression.xml";
    private long startTime;
    private long duration;
    private String testCase;
    private String testSuite;
    private String testStep;

    public SoapUIUtil() throws Exception{
        SoapUI.setSoapUICore(new StandaloneSoapUICore(true));
        project = new WsdlProject(PROJECT_URL);
        suiteList = project.getTestSuiteList();
        for(int i = 0; i < suiteList.size(); i++)
            caseList = suiteList.get(i).getTestCaseList();
    }

    public void runTestCase(String testSuite, String testCase) {
        this.testCase = testCase;
        startTime = System.currentTimeMillis();
        runner = project.getTestSuiteByName(testSuite).getTestCaseByName(testCase).run(new PropertiesMap(), false);
        duration = System.currentTimeMillis() - startTime;

        printFullReport();
    }

    public void runTestSuite(String testSuite) {
        this.testSuite = testSuite;
        startTime = System.currentTimeMillis();
        runner = project.getTestSuiteByName(testSuite).run(new PropertiesMap(), false);
        duration = System.currentTimeMillis() - startTime;
        printFullReport();
    }

    public int getTotalTestSuites() {
        return project.getTestSuiteCount();
    }

    public int getTotalTestCases() {
        int total = 0;
        for (TestSuite ts: project.getTestSuiteList())
            total+=ts.getTestCaseCount();
        return total;
    }

    public int getTotalTestSteps() {
        int total = 0;
        for (TestSuite ts: project.getTestSuiteList())
            for (TestCase tc: ts.getTestCaseList())
                total+=tc.getTestStepCount();
        return total;
    }

    public String getTestCaseResponse() {
        return runner.getRunContext().expand(buildContextProperty(testCase, ContextPropConstant.RESPONSE));
    }

    public String getTestCaseRequest() {
        return runner.getRunContext().expand(buildContextProperty(testCase, ContextPropConstant.REQUEST));
    }

    private String buildContextProperty(String testCase, String cpc) {
        String start = "${";
        String join = "#";
        String end = "}";

        return start + testCase + join + cpc + end;

    }

    private String getRunnerStatus() {
        return runner.getStatus().toString();
    }

    private String getRunnerReason() {
        return runner.getReason();
    }

    private void getRunnerEndpoint() {
        runner.getRunContext().expand(buildContextProperty(testCase, ContextPropConstant.REQUEST));
    }

    private void getRunnerDomain() {
        runner.getRunContext().expand(buildContextProperty(testCase, ContextPropConstant.DOMAIN));
    }

    public void runAllTests() {
        for (TestSuite ts: project.getTestSuiteList())
            runner = ts.run(new PropertiesMap(), false);
    }

    //TODO: Finish results as per team suggestions
    public void printFullReport() {
        System.out.println("\nSoapUI 5.4.0 TestCaseRunner Summary");
        System.out.println("-----------------------------");
        System.out.println("Time Taken: " + duration + "ms");
        System.out.println("Total TestSuites: " + getTotalTestSuites());
        System.out.println("Total TestCases: " + getTotalTestCases());
        System.out.println("Total TestSteps: " + getTotalTestSteps());

        if (getRunnerStatus().equalsIgnoreCase("FINISHED"))
            System.out.println("Test Runner Status: PASSED");
        else if (getRunnerStatus().equalsIgnoreCase("FAILED"))
            System.out.println("Test Runner Failed Reason: " + getRunnerReason());
    }

}
