package com.icon.sct.utils;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.awt.Robot;
import java.util.Base64;
import java.awt.Toolkit;
import java.nio.file.Path;
import java.io.FileWriter;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.crypto.Cipher;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import io.qameta.allure.Allure;
import java.time.LocalDateTime;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.crypto.spec.SecretKeySpec;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import io.qameta.allure.model.StepResult;
import java.time.format.DateTimeFormatter;
import com.icon.sct.base.BaseTest;
import java.util.concurrent.ThreadLocalRandom;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Utils {
    private static Loggers logger = new Loggers();
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "MySecretKey12345".getBytes(); // 16-byte key
    private static final List<String> countries = List.of(
"Albania","Algeria","Argentina","Armenia","Australia","Austria","Bahrain","Belarus","Belgium","Bolivia (Plurinational State of)",
"Bosnia and Herzegovina","Brazil","Bulgaria","Burkina Faso","Cameroon","Canada","Chile","China","Colombia","Congo (the Democratic Republic of the)",
"Costa Rica","Croatia","Cyprus","Czechia","Côte d'Ivoire","Denmark","Dominican Republic (the)","Ecuador","Egypt","El Salvador","Estonia",
"Ethiopia","Finland","France","Gabon","Gambia (the)","Georgia","Germany","Ghana","Greece","Guatemala","Guinea","Haiti","Honduras","Hong Kong",
"Hungary","Iceland","India","Indonesia","Iran (Islamic Republic of)","Ireland","Israel","Italy","Jamaica","Japan","Jordan","Kazakhstan",
"Kenya","Korea (the Republic of)","Kuwait","Latvia","Lebanon","Lithuania","Luxembourg","Malawi","Malaysia","Mali","Malta","Mexico",
"Moldova (the Republic of)","Montenegro","Morocco","Mozambique","Netherlands (Kingdom of the)","New Zealand","Nicaragua","Niger (the)",
"Nigeria","North Macedonia","Norway","Oman","Pakistan","Panama","Paraguay","Peru","Philippines (the)","Poland","Portugal","Puerto Rico",
"Qatar","Romania","Russian Federation (the)","Rwanda","Saudi Arabia","Senegal","Serbia","Singapore","Slovakia","Slovenia","South Africa",
"Spain","Sri Lanka","Sweden","Switzerland","Syrian Arab Republic (the)","Taiwan (Province of China)","Tanzania, the United Republic of",
"Thailand","Tunisia","Turkey","Uganda","Ukraine","United Arab Emirates (the)","United Kingdom of Great Britain and Northern Ireland (the)",
"United States of America (the)","Uruguay","Venezuela (Bolivarian Republic of)","Viet Nam","Zambia"
    );

    public static void takeWindowsScreenshot() {
        Allure.getLifecycle().startStep(UUID.randomUUID().toString(), new StepResult().setName("Screenshot"));
        try {
            Path screenshotPath = Utils.getScreenshotPath();
            String imageName = screenshotPath.toString().substring(screenshotPath.toString().lastIndexOf('\\') + 1); 
            Utils.createDirectoryIfNotExist(screenshotPath.getParent());
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            Robot robot = new Robot();
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            File outputFile = new File(screenshotPath.toString());
            ImageIO.write(screenFullImage, "png", outputFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenFullImage, "png", outputStream);
            Allure.addAttachment(imageName.replace(".png", ""), new ByteArrayInputStream(outputStream.toByteArray()));
            logger.objectInfo(String.format("A Windows Screenshot was taken - [%s]", imageName));
        } catch (AWTException | IOException ex) {
            logger.failed("Take Windows Screenshot failed: " + ex.getMessage());
        }
        finally{
            Allure.getLifecycle().stopStep();
        }
    }

    public static String getCurrentTimeWithSpecificFormat(String format){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentTime.format(formatter);
    }

    public static Path getScreenshotPath(){
        String testCaseName = BaseTest.getTestCaseName(); 
        String testClassName = BaseTest.getTestClassName();
        // String testSuiteName = BaseTest.getTestSuiteName();
        int numberOfScreenshots = BaseTest.getNumberOfScreenshots();
        String numberOfScreenshotsString = Integer.toString(numberOfScreenshots);
        if (numberOfScreenshots < 10){
            numberOfScreenshotsString = String.format("0%s", numberOfScreenshots);
        }
        String screenshotName = String.format("SCT_%s-%s_%s_SS%s.png", BaseTest.getREQ(), BaseTest.getNumberOfTestCases(), testCaseName, numberOfScreenshotsString);
        // String screenshotName = String.format("[%s]%s_%s.png", testSuiteName, testClassName, Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd-HHmmssSSS"));
        // String currentPageName = BaseTest.getCurrentPageName();
        // if (!currentPageName.toLowerCase().equals("default")){
        //     screenshotName = String.format("[%s][%s]-%s_%s.png", testSuiteName, currentPageName, testClassName, Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd-HHmmss"));
        // }
        return Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testClassName, "screenshots", testCaseName, screenshotName);
    }

    public static Path getTracePath(String pageName){
        String testCaseName = BaseTest.getTestCaseName();
        String testSuiteName = BaseTest.getTestClassName();
        Path tracePath = Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testSuiteName, "traces", testCaseName, String.format("%s_%s.zip", testSuiteName, testCaseName));
        if (!pageName.toLowerCase().equals("default")){
            tracePath = Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testSuiteName, "traces", testCaseName, String.format("[%s] %s_%s.zip", pageName, testSuiteName, testCaseName));
        }
        logger.info(String.format("Tracing will be saved to [%s]", tracePath.toString()));
        return tracePath;
    }

    public static void createDirectoryIfNotExist(Path path){
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
           logger.error(String.format("Failed to create directory: %s", e.getMessage()), true);
        }
    }

    public static void moveRecordFileToCorrectDirectory(Path recordPath, String pageName){
        String testClassName = BaseTest.getTestClassName();
        String testSuiteName = BaseTest.getTestSuiteName();
        String screenRecordName = String.format("[%s]%s_%s.webm", testSuiteName, testClassName, Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd-HHmmss"));
        if (!pageName.toLowerCase().equals("default")){
            screenRecordName = String.format("[%s][%s]%s_%s.webm", testSuiteName, pageName.trim(), testClassName, Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd-HHmmss"));
        }
        Allure.getLifecycle().startStep(UUID.randomUUID().toString(), new StepResult().setName("Screen Record"));
        Path destinationPath = Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testClassName, "records", screenRecordName).toAbsolutePath();
        if (Files.exists(destinationPath)){
            screenRecordName = String.format("[%s]-%s_%s.webm", BaseTest.getTestSuiteName(), testClassName, Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd-HHmmss"));
            destinationPath = Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testClassName, "records", screenRecordName).toAbsolutePath();
        }
        try {
            if (Files.exists(recordPath)) {
                Files.move(recordPath, destinationPath);
                byte[] byteArr = IOUtils.toByteArray(new FileInputStream(destinationPath.toFile()));
                Allure.addAttachment(screenRecordName, "video/webm", new ByteArrayInputStream(byteArr), "webm");
                logger.info(String.format("Record for [%s] has been saved to [%s]", testClassName, destinationPath.toString()));
            }
            else{
                logger.warn(String.format("Not playwright records found at [%s]", recordPath.toString()));
            }
            
        } catch (Exception e) {
            logger.error(String.format("Move Record File from [%s] to [%s] failed. Details: %s", recordPath.toString(), destinationPath.toString(), e.getMessage()));
        }
        finally{
            Allure.getLifecycle().stopStep();
        }
    }

    public static void setUpEnvironment() {
        try {
            File file = new File(String.format("%s/environment.properties", ConfigReader.getGlobalVariable("allureResultsBaseDir")));
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(String.format("OS = %s\n",  System.getProperty("os.name")));
            writer.write(String.format("Java_Version = %s\n",  System.getProperty("java.version")));
            writer.write(String.format("Browser-Version = %s\n", String.format("%s-%s", ConfigReader.getEnvironmentProperty("browser"), System.getProperty("browserVersion"))));
            writer.write(String.format("Headless = %s\n", ConfigReader.getEnvironmentProperty("headless")));
            writer.write(String.format("Environment = %s\n", ConfigReader.getSystemEnvironment()));
            // writer.write(String.format("Run_Parallel_Classes = %s\n", System.getProperty("parallelClass", "false")));
            writer.close();
        } catch (IOException e) {
            logger.error(String.format("Setup Environment failed. Details : %s", e.getMessage()), true);
        }
    }

    public static void cleanAndPrepareResultsDirectories(){
        String testCaseName = BaseTest.getTestCaseName(); 
        String testSuiteName = BaseTest.getTestClassName();
        List<Path> resultsDirectories = new ArrayList<>();
        try {
            resultsDirectories.add(Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testSuiteName, "screenshots", testCaseName));
            resultsDirectories.add(Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testSuiteName, "records"));
            resultsDirectories.add(Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), testSuiteName, "traces", testCaseName));
            for (Path path : resultsDirectories){
                if (Files.exists(path)) {
                    Files.walk(path)
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .forEach(File::delete);
                }
                Files.createDirectories(path);
            }
            logger.info(String.format("Clean and prepare results directories successfully [%s]", String.join(", ", resultsDirectories.stream().map(Path::toString).toArray(String[]::new))), true);
        } catch (Exception e) {
           logger.error(String.format("Failed to create results directory: %s", e.getMessage()), true);
        }
    }

    public static void deleteFolder(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    public static void cleanupAllureResultsDirectory() {
        String directoryPath = ConfigReader.getGlobalVariable("allureResultsBaseDir");
        try {
            Path path = Paths.get(directoryPath);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted((o1, o2) -> o2.compareTo(o1))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (Exception e) {
                                logger.error("Failed to delete " + p + ": " + e.getMessage());
                            }
                        });
            }
            logger.info("Allure Results directory [" + directoryPath + "] has been cleaned up", true);
        } catch (Exception e) {
            logger.error("Failed to cleanup Allure Results directory " + directoryPath + ": " + e.getMessage(), true);
        }
    }

    public static void copyFolder(String sourceDir, String targetDir) {
        File sourceFolder = new File(sourceDir);
        File targetFolder = new File(targetDir);
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            logger.error("Source directory does not exist or is invalid: " + sourceDir, true);
            return;
        }
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        try {
            Files.walk(Paths.get(sourceDir))
                .forEach(sourcePath -> {
                    Path targetPath = Paths.get(targetDir, sourcePath.toString().substring(sourceDir.length()));
                    try {
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(targetPath);
                        } else {
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        logger.error("Failed to copy file: " + sourcePath);
                        e.printStackTrace();
                    }
                });
           logger.info(String.format("Successfully copied folder from '%s' to '%s'",sourceDir, targetDir), true);
        } catch (IOException e) {
            logger.error("Error during directory copy operation : " + e, true);
        }
    }

    public static String createAllureResultsFolderForSpecificSuite(String suiteName) {
        String allureResultFolder =  String.format("%s_%s", Paths.get(ConfigReader.getGlobalVariable("allureResultsBaseDir")).getFileName().toString(), suiteName.replaceAll("\\s+", "-"));
        String defaultAllureResultsPath = ConfigReader.getGlobalVariable("allureResultsBaseDir");
        String allureResultsPath = String.format("target/%s", allureResultFolder);
        copyFolder(defaultAllureResultsPath, allureResultsPath);
        File folder = new File(allureResultsPath);
        if (!folder.exists() || !folder.isDirectory()) {
            logger.error("The Allure Results directory does not exist or is invalid: " + allureResultsPath, true);
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            logger.error("No JSON files found in the Allure Results directory: " + allureResultsPath, true);
            return null;
        }
        //Delete all json file of other suites (Contains labels but the value of parentSuite is different from current suite) and replace Suite name

        for (File file : files) {
            try {
                ObjectNode rootNode = (ObjectNode) objectMapper.readTree(file);
                JsonNode labelsNode = rootNode.get("labels");
                if (labelsNode == null || !labelsNode.isArray()) {
                    continue;
                }
                boolean deleted = false;
                for (JsonNode label : labelsNode) {
                    if ("parentSuite".equals(label.get("name").asText())) {
                        String actualSuiteName = label.get("value").asText();
                        if (!actualSuiteName.equals(suiteName)) {
                            file.delete();
                            deleted = true;
                        }
                        break;
                    }
                }
                if (!deleted && BaseTest.getNeedReplaceSuiteName()){
                    String newSuiteName = BaseTest.getTestSuiteName();
                    // Replace suite name
                    boolean modified = false;
                    if (rootNode.has("name")) {
                        String originalName = rootNode.get("name").asText();
                        if (originalName.contains("Surefire")) {
                            rootNode.put("name", originalName.replace("Surefire", newSuiteName));
                            modified = true;
                        }
                    }
                    // Replace labels
                    if (labelsNode != null && labelsNode.isArray()) {
                        for (JsonNode labelNode : labelsNode) {
                            if (("parentSuite".equals(labelNode.get("name").asText()) ||"suite".equals(labelNode.get("name").asText())) &&
                                labelNode.get("value").asText().contains("Surefire")) {
                                ((ObjectNode) labelNode).put("value", labelNode.get("value").asText().replace("Surefire", newSuiteName));
                                modified = true;
                            }
                        }
                    }
                    if (modified) {
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
                    }
                }
            } catch (IOException e) {
                logger.error(String.format("Error processing file '%s'. Details : %s", file.getName(), e), true);
            }
        }
        return allureResultFolder;
    }
    
    public static void killTaskByImageName(String imageName) throws InterruptedException {
        try {
            String command = "taskkill /FI \"Imagename eq " + imageName + "\" /F";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to terminate tasks with image name: " + e.getMessage());
        }
    }

    // @Step("Generate Allure report.")
    public static void generateAllureReport(String suiteName) {
        try {
            String allureResultsPath = createAllureResultsFolderForSpecificSuite(suiteName);
            Path reportDirectory = Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), "reports");
            createDirectoryIfNotExist(reportDirectory);
            Path currentReportDirectory = Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"), "reports", getCurrentTimeWithSpecificFormat("yyyy-MM-dd"));
            createDirectoryIfNotExist(currentReportDirectory);
            String reportName = String.format("AllureReport_%s_%s.html", BaseTest.getTestSuiteName(), getCurrentTimeWithSpecificFormat("yy-MM-dd-HHmmss"));
            String projectDir = System.getProperty("user.dir");
            File projectDirectory = new File(projectDir);
            String allureReportPath = Paths.get(System.getProperty("user.dir"), "target",
                            String.format("allure-report_%s", suiteName.replaceAll("\\s+", "-"))).toAbsolutePath().toString();                              
            String command = String.format("mvn allure:report -Dallure.results.directory=\"%s\" -Dallure.report.directory=\"%s\"", allureResultsPath, allureReportPath);
            // System.out.printf("\n------------------- Command is [%s]\n", command);
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.directory(projectDirectory);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Allure report was generated successfully.", true);
                File sourceFile = new File(String.format("%s/index.html", allureReportPath));
                File destinationFile = new File(String.format("%s/%s", currentReportDirectory, reportName));
                File destinationDir = destinationFile.getParentFile();
                if (!destinationDir.exists()) {
                    destinationDir.mkdirs();
                }
                if (sourceFile.exists()) {
                    org.apache.commons.io.FileUtils.copyFile(sourceFile, destinationFile);
                    logger.info(String.format("Allure report was copied to %s\\%s", currentReportDirectory, reportName), true);
                    addExpandAllAndCollapseAllButtons(destinationFile.toPath());
                    deleteFolder(Paths.get("target", allureResultsPath));
                    deleteFolder(Paths.get(allureReportPath));
                }
                else {
                    logger.error("Allure report file not found.", true);
                }
            }
            else {
                logger.error("Failed to generate Allure report.", true);
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.toString(), true);
        }
    }
 
    private static void addExpandAllAndCollapseAllButtons(Path htmlFile) {
        String script = "<script>\n" +
            "function addExpandCollapseButtons() {\n" +
            "    if (window.location.hash.includes('#suites') && !document.querySelector('.expand-collapse-btns')) {\n" +
            "        var container = document.createElement('div');\n" +
            "        container.className = 'expand-collapse-btns';\n" +
            "        container.style.cssText = 'position:fixed;top:10px;right:10px;z-index:9999;display:flex;gap:5px';\n" +
            "        var expandBtn = document.createElement('button');\n" +
            "        expandBtn.innerHTML = 'Expand All';\n" +
            "        expandBtn.style.cssText = 'padding:4px 8px;background:#28a745;color:white;border:none;border-radius:4px;cursor:pointer';\n" +
            "        expandBtn.onclick = function() {\n" +
            "            document.querySelectorAll('.step__title,.attachment__title,.attachment-row__header').forEach(function(s) {\n" +
            "                if (!s.parentElement.classList.contains('step_expanded') &&\n" +
            "                    !s.parentElement.classList.contains('attachment_expanded') &&\n" +
            "                    !s.classList.contains('attachment-row__header_expanded')) {\n" +
            "                    s.click();\n" +
            "                }\n" +
            "            });\n" +
            "            setTimeout(function() {\n" +
            "                document.querySelectorAll('.attachment-row').forEach(function(row) {\n" +
            "                    var arrow = row.querySelector('.block__arrow');\n" +
            "                    if (arrow && !arrow.classList.contains('block__arrow__expanded')) {\n" +
            "                        row.click();\n" +
            "                    }\n" +
            "                });\n" +
            "            }, 500);\n" +
            "        };\n" +
            "        var collapseBtn = document.createElement('button');\n" +
            "        collapseBtn.innerHTML = 'Collapse All';\n" +
            "        collapseBtn.style.cssText = 'padding:4px 8px;background:#dc3545;color:white;border:none;border-radius:4px;cursor:pointer';\n" +
            "        collapseBtn.onclick = function() {\n" +
            "            document.querySelectorAll('.attachment-row').forEach(function(row) {\n" +
            "                var arrow = row.querySelector('.block__arrow__expanded');\n" +
            "                if (arrow) row.click();\n" +
            "            });\n" +
            "            setTimeout(function() {\n" +
            "                document.querySelectorAll('.step__title,.attachment__title,.attachment-row__header').forEach(function(s) {\n" +
            "                    if (s.parentElement.classList.contains('step_expanded') ||\n" +
            "                        s.parentElement.classList.contains('attachment_expanded') ||\n" +
            "                        s.classList.contains('attachment-row__header_expanded')) {\n" +
            "                        s.click();\n" +
            "                    }\n" +
            "                });\n" +
            "            }, 500);\n" +
            "        };\n" +
            "        container.appendChild(expandBtn);\n" +
            "        container.appendChild(collapseBtn);\n" +
            "        document.body.appendChild(container);\n" +
            "    } else if (!window.location.hash.includes('#suites')) {\n" +
            "        var existingBtns = document.querySelector('.expand-collapse-btns');\n" +
            "        if (existingBtns) existingBtns.remove();\n" +
            "    }\n" +
            "}\n" +
            "setTimeout(addExpandCollapseButtons, 2000);\n" +
            "window.addEventListener('hashchange', function() {\n" +
            "    setTimeout(addExpandCollapseButtons, 500);\n" +
            "});\n" +
            "</script>\n</body>";
 
        try {
            String html = Files.readString(htmlFile);
            if (html.contains("</body>")) {
                String modified = html.replace("</body>", script);
                Files.writeString(htmlFile, modified, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            logger.error(String.format("Add Expand All and Collapse All buttons into Allure report failed. Details: %s", e));
        }
    }

    public static String encrypt(String data) throws Exception {
        SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        logger.info(String.format("Your encrypt password is [%s]", Base64.getEncoder().encodeToString(encryptedData)));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] originalData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(originalData);
    }

    public static boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isEncrypted(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty() || !isBase64(encryptedText)) {
            return false;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return decryptedBytes != null && decryptedBytes.length > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static int generateRandomNumber(int from, int to){
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static String convertRGBToHex(String rgbColor) {
        rgbColor = rgbColor.replace(" ", "").toLowerCase();
        if (rgbColor.startsWith("rgba(") || rgbColor.startsWith("rgb(")) {
            String[] values = rgbColor.replaceAll("[^0-9,]", "").split(",");
            int r = Integer.parseInt(values[0]);
            int g = Integer.parseInt(values[1]);
            int b = Integer.parseInt(values[2]);
            String hex = String.format("#%02X%02X%02X", r, g, b);
            logger.objectInfo(String.format("Hex color code of RGB '%s' is '%s'", rgbColor, hex));
            return hex;
        }
        throw new IllegalArgumentException("Invalid color format: " + rgbColor);
    }

    public static void handleSTSAuthentication(String username, String password){
        Robot robot;
        try {
            robot = new Robot();
            typeText(robot, username);
            robot.delay(300);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(300);
            typeText(robot, password);
            robot.delay(500);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        
    }

    public static void typeText(Robot robot, String input) {
        for (char c : input.toCharArray()) {
            typeChar(robot, c);
            robot.delay(100);
        }
    }

    public static void typeChar(Robot robot, char c) {
        try {
            switch (c) {
                case '@':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_2);
                    robot.keyRelease(KeyEvent.VK_2);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '_':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_MINUS);
                    robot.keyRelease(KeyEvent.VK_MINUS);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '-':
                    robot.keyPress(KeyEvent.VK_MINUS);
                    robot.keyRelease(KeyEvent.VK_MINUS);
                    break;
                case '.':
                    robot.keyPress(KeyEvent.VK_PERIOD);
                    robot.keyRelease(KeyEvent.VK_PERIOD);
                    break;
                case ' ':
                    robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);
                    break;
                default:
                    boolean upperCase = Character.isUpperCase(c);
                    int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                    if (upperCase) robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(keyCode);
                    robot.keyRelease(keyCode);
                    if (upperCase) robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Does not support character : " + c);
        }
    }

    public static String getRandomCountry() {
        Random random = new Random();
        int index = random.nextInt(countries.size());
        return countries.get(index);
    }

    public static String generateRandomNumberCharString(int numberOfChar) {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(numberOfChar);

        for (int i = 0; i < numberOfChar; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    public static int compareString(String strA, String strB) {
        if (strA == null || strB == null) {
            return 0;
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            java.util.Date dateA = sdf.parse(strA);
            java.util.Date dateB = sdf.parse(strB);
            return dateA.compareTo(dateB);
        }
        catch (java.text.ParseException e) {
            return strA.compareToIgnoreCase(strB);
        }
    }

    public static String getRandomNumeric(int number) {
		return RandomStringUtils.randomNumeric(number);
	}

}
