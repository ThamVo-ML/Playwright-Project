package com.icon.sct.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class ExcelUtils {

    private static Loggers logger = new Loggers();

    public static List<String> getHeaderExcelFile(String excelPath, String sheetName) throws IOException {
        InputStream fs = ExcelUtils.class.getClassLoader().getResourceAsStream(excelPath);
        Workbook wb = new XSSFWorkbook(fs);
        Sheet sheet = wb.getSheet(sheetName);
        List<String> headers = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                headers.add(cell.getStringCellValue());
            } else {
                headers.add("");
            }
        }
        wb.close();
        fs.close();
        return headers;
    }

    public static List<Map<String, String>> getAllDataFromFile(String excelPath, int sheetNumber) throws IOException {
        InputStream fs = ExcelUtils.class.getClassLoader().getResourceAsStream(excelPath);
        Workbook wb = new XSSFWorkbook(fs);
        Sheet sheet = wb.getSheetAt(sheetNumber);
        List<String> listHeader = getHeaderExcelFile(excelPath, sheet.getSheetName());
        List<Map<String, String>> listData = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Map<String, String> newRowData = new HashMap<>();
            for (int j = 0; j < listHeader.size(); j++) {
                Cell cellValue = sheet.getRow(i).getCell(j);
                if (cellValue != null && cellValue.getCellType() != CellType.BLANK) {
                    switch (cellValue.getCellType()) {
                        case STRING:
                            newRowData.put(listHeader.get(j), cellValue.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cellValue)) {
                                Date dateValue = cellValue.getDateCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
                                String formattedDate = dateFormat.format(dateValue);
                                newRowData.put(listHeader.get(j), formattedDate);
                            } else {
                                newRowData.put(listHeader.get(j), String.valueOf(cellValue.getNumericCellValue()));
                            }
                            break;
                        case BOOLEAN:
                            newRowData.put(listHeader.get(j), Boolean.toString(cellValue.getBooleanCellValue()));
                            break;
                    }
                } else {
                    newRowData.put(listHeader.get(j), "");
                }
            }
            listData.add(newRowData);
        }
        
        wb.close();
        fs.close();
        
        return listData;
    }

    public static List<String> getAllSheetName(String excelPath) throws IOException {
        InputStream fs = ExcelUtils.class.getClassLoader().getResourceAsStream(excelPath);
        Workbook wb = new XSSFWorkbook(fs);
        List<String> listSheetName = new ArrayList<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            listSheetName.add(wb.getSheetName(i));
        }
        wb.close();
        fs.close();
        logger.info("List of all sheet names: " + listSheetName);
        return listSheetName;
    }

    //======================================READ FILE===============================================
    public static Map<String, List<String>> getMapValuesBySheetName(String file_Name, String sheet_Name) throws IOException {
        // if(overrideFilePath) filePath = fileName;
        String Path_File=ConfigReader.getGlobalVariable("dataFilePath")+"\\"+file_Name;
    System.out.println(Path_File);
    Path absolutePath=Paths.get(Path_File);
    String absolutePathStr=absolutePath.toString();
    FileInputStream file=new FileInputStream(new File(absolutePathStr));
    try (Workbook workbook = new XSSFWorkbook(file)) {
        Sheet sheet = workbook.getSheet(sheet_Name);
        DataFormatter formatter= new DataFormatter();
        Map<String,List<String>> sheet_map= new LinkedHashMap<>();
        Row headers=sheet.getRow(0);
        int numcolumns=headers.getPhysicalNumberOfCells();
        for (int columnIndex=0;columnIndex<numcolumns;columnIndex++){
            String headerName=null;
            List<String> listValues=new ArrayList<>();
            for (int rowIndex=0;rowIndex < sheet.getPhysicalNumberOfRows();rowIndex++){
                Row row = sheet.getRow(rowIndex);
                Cell cell =row.getCell(columnIndex);
                String cellValue=null;
                if(cell !=null){
                    cellValue=formatter.formatCellValue(cell);
                    cellValue=StringEscapeUtils.unescapeHtml3(cellValue.trim());
                    if(rowIndex>0){
                        listValues.add(cellValue);
                    } else {
                        headerName=cellValue;
                    }
                }
            }
            sheet_map.put(headerName, listValues);
        } 
        return sheet_map;
    }
    }

}
