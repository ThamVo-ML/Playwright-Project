package com.icon.sct.tests;
import org.testng.annotations.Test;
import com.icon.sct.base.BaseTest;
import com.icon.sct.utils.RetryAnalyzer;
import com.icon.sct.utils.Utils;

import java.util.List;
import java.util.Map; 

public class SP0151_Test extends BaseTest{
    private static String REQ = "x.x.x.x";
    private static String SUITE = "SP0151-SCT_x.x.x.x_Create_Project";

    String clientNamePrefix = "VTH Test";

    @Test(description = "[SP0151-x] x.x.x.x-TC1_Add Project 1", groups = "SCT2 V3.8", retryAnalyzer = RetryAnalyzer.class)
    void addFirstProjectProtocol() throws Exception{
        // logger.step("Create a Client, Project, Protocol and Initial Case");
        String clientName = clientNamePrefix + " " + Utils.getRandomNumeric(3);
        
        // logger.step("Create Project");
        List<String> projectList = projectsPage.addProject(1, 1, "No", 2, "Yes", null, null, null, clientName);
        System.out.println(projectList);

        projectsPage.clickOnProjectLink(projectList.get(0));

        List<String> protocolList = projectsPage.addProtocol(1, 2, "No", "Yes", null, null, 4, null, 1, null, "disabled", null);
        System.out.println(protocolList);
    }

    @Test(description = "[SP0151-x] x.x.x.x-TC2_Add Project 2", groups = "SCT2 V3.8", retryAnalyzer = RetryAnalyzer.class)
    void addSecondProjectProtocol() throws Exception{
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