package com.icon.sct.utils;

import org.testng.ITestResult;
import org.testng.IRetryAnalyzer;


public class RetryAnalyzer implements IRetryAnalyzer {
    private Loggers logger = new Loggers();
    private int retryCount = 1;
    private static final int maxRetryCount = Integer.parseInt(ConfigReader.getEnvironmentProperty("retry"));

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.info("Retrying test " + result.getName() + " for the " + retryCount + " time(s).", true);
            return true;
        }
        return false;
    }
}