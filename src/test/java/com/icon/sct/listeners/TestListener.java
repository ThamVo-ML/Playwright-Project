package com.icon.sct.listeners;

import java.util.Set;
import java.util.Stack;
import java.util.Arrays;
import java.util.HashSet;
import org.testng.ITestResult;
import java.time.LocalDateTime;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Status;
import com.icon.sct.utils.Utils;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import com.icon.sct.utils.Loggers;
import java.time.format.DateTimeFormatter;
import com.icon.sct.base.BaseTest;
import com.icon.sct.utils.ConfigReader;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.listener.TestLifecycleListener;


public class TestListener implements StepLifecycleListener, TestLifecycleListener, ITestListener {
    private static final ThreadLocal<String> THREAD_LOCAL_TC_NAME = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isCapturingScreenshot = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Stack<String>> stepStack = ThreadLocal.withInitial(Stack::new);
    private static final Set<String> AUTO_TAKE_SCREENSHOT_PACKAGES = new HashSet<>(Arrays.asList(
        "com.icon.sct.pages"
    ));
    private static String[] groups = new String[0];
        

    @Override
    public void beforeTestSchedule(TestResult result) {
        THREAD_LOCAL_TC_NAME.set(result.getName());
        addStartTimeToDescription(result);
    }

    @Override
    public void onTestStart(ITestResult result) {
        groups = result.getMethod().getGroups();
    }

    @Override
    public void afterTestStart(TestResult result) {
        // Set groups to feature and tag
        Arrays.stream(groups).forEach(group -> {
            result.getLabels().add(new Label().setName("feature").setValue(group));
            result.getLabels().add(new Label().setName("tag").setValue(group));
        });
        groups = new String[0];
    }

    @Override
    public void beforeTestStop(TestResult result){
        if (result.getStatus() == Status.FAILED){
            Loggers.stopStep(true);
        }
        else{
            Loggers.stopStep();
        }
    }

    @Override
    public void afterStepStart(StepResult result) {
        String stepName = result.getName();
        boolean isStepMethod = !isAllureStepMethod(result);
        if (isStepMethod && !stepName.toLowerCase().equals("screenshot") && !stepName.toLowerCase().equals("screen record")) {
            if (!stepStack.get().isEmpty()){
                String previousStepName = stepStack.get().peek();
                Loggers.addStepLogs(String.format("[%s EXECUTE] - %s", Utils.getCurrentTimeWithSpecificFormat("HH:mm:ss"), stepName), previousStepName);
            }
            stepStack.get().push(stepName);
            Loggers.threadLocalStepLogs.get().put(stepName, new StringBuilder());
            Loggers.threadLocalCurrentSteps.set(stepName);
        }
    }
   
    @Override
    public void beforeStepStop(StepResult result) {
        if (isCapturingScreenshot.get()) {
            return;
        }
        String stepName = result.getName();
        boolean isStepMethod = !isAllureStepMethod(result);
        if (isStepMethod && !stepName.toLowerCase().equals("screenshot") && !stepName.toLowerCase().equals("screen record")) {
            boolean isFromPageObject = Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(stackTraceElement ->
                    AUTO_TAKE_SCREENSHOT_PACKAGES.stream().anyMatch(stackTraceElement.getClassName()::startsWith));
            if (isFromPageObject) {
                isCapturingScreenshot.set(true);
                try {
                    if (Boolean.parseBoolean(ConfigReader.getGlobalVariable("takeScreenshotByPlaywright", "true")) && BaseTest.getPlaywrightActionsObject() != null) {
                        BaseTest.getPlaywrightActionsObject().takeFullPageScreenshot();
                    }
                    else{
                        Utils.takeWindowsScreenshot();
                    }
                } finally {
                    isCapturingScreenshot.set(false);
                }
            }

            if (!stepStack.get().isEmpty()) {
                stepName = stepStack.get().peek();
                String logContent = Loggers.threadLocalStepLogs.get().get(stepName).toString();
                if (!logContent.isEmpty() && logContent.split("\\n").length > 1) {
                    Allure.addAttachment("Step Execution Logs - " + stepName, "text/plain", logContent);
                }
                stepStack.get().pop();
                if (!stepStack.get().isEmpty()) {
                    Loggers.threadLocalCurrentSteps.set(stepStack.get().peek());
                } else {
                    Loggers.threadLocalCurrentSteps.remove();
                }
            }
        }   
    }

    public static synchronized String getTestCaseName() {
        return THREAD_LOCAL_TC_NAME.get();
    }

    private boolean isAllureStepMethod(StepResult result) {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getMethodName().equals("step")) {
                return true;
            }
        }
        return false;
    }

    private void addStartTimeToDescription(TestResult result) {
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String originalDescription = result.getDescription() != null ? result.getDescription() : "";
        String updatedDescription = String.format("[Test started at: %s]%s", startTime, (originalDescription.isEmpty() ? "" : String.format("\n\n%s",originalDescription, null)));
        result.setDescription(updatedDescription);
    }
}
