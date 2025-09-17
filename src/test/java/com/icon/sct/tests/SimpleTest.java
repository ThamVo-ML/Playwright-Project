package com.icon.sct.tests;

import org.testng.annotations.Test;
import com.icon.sct.base.BaseTest;
import com.icon.sct.pages.ProjectsPage;
import com.icon.sct.utils.RetryAnalyzer;
import com.icon.sct.utils.Utils;
import java.util.List;


public class SimpleTest extends BaseTest {
    public static String REQ = "3.x.x";

    String clientNamePrefix = "VTH Test";

    @Test(description = "[SP0151-xxxx] 3.x.x - Simple Test", groups = {"SCT v3.6" , "3.x.x"}, retryAnalyzer = RetryAnalyzer.class)
    void simpleTest() throws Exception{
        // logger.step("Create a Client, Project, Protocol and Initial Case");
        String clientName = clientNamePrefix + " " + Utils.getRandomNumeric(3);
        
        // logger.step("Create Project");
        List<String> projectList = projectsPage.addProject(1, 1, "No", 2, "Yes", null, null, null, clientName);
        System.out.println(projectList);

        projectsPage.clickOnProjectLink(projectList.get(0));

        List<String> protocolList = projectsPage.addProtocol(1, 2, "No", "Yes", null, null, 4, null, 1, null, "disabled", null);
        System.out.println(protocolList);
    }
}