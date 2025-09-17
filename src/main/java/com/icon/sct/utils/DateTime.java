package com.icon.sct.utils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateTime {
    static Loggers logger = new Loggers();
    // List of supported input formats
    private static final List<String> knownFormats = Arrays.asList(
        "dd-MM-yyyy",
        "dd/MM/yyyy",
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "dd-MMM-yyyy",
        "MM-yyyy",
        "yyyyMM",
        "MMM-yyyy",
        "d-MMM-yyyy",
        "d-MM-yyyy",
        "yyyy-MM",
        "yyyy-MM-d",
        "yyyy-MMM",
        "yyyy-MMM-d",
        "yyyy-MMM-dd",
        "d",
        "MMM",
        "yyyy"
    );

    /**
     * Converts a date string from one format to another.
     *
     * @param inputDate   the original date string
     * @param inputFormat the format of the input date string (e.g. "yyyy-MM-dd HH:mm:ss")
     * @param outputFormat the desired output format (e.g. "dd/MM/yyyy")
     * @return formatted date string or error message
     */
    public static String convertDateFormat(String inputDate, String inputFormat, String outputFormat) {
        try {
            
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
            LocalDate date = LocalDate.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Invalid date or format: " + e.getMessage();
        }
    }

    /**
     * Gets the date after n days or months from today and returns it in the specified format.
     *
     * @param daysOffset   number of days to add (use 0 if not adding days)
     * @param monthsOffset number of months to add (use 0 if not adding months)
     * @param format       desired output format (e.g. "dd-MM-yyyy")
     * @return formatted future date string
     */
    public static String getFutureDate(int daysOffset, int monthsOffset, String format) {
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate.plusDays(daysOffset).plusMonths(monthsOffset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return futureDate.format(formatter);
    }

    // Function to get current date in a specified format
    public static String getCurrentDateFormatted(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }

    /**
     * Detects the format of a date string from a list of known formats.
     *
     * @param dateStr the input date string
     * @return the format string if matched, or null if no match
     */
    public static String detectDateFormat(String dateStr) {
        for (String pattern : knownFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate.parse(dateStr, formatter);
                return pattern;
            } catch (DateTimeParseException ignored) {
            }
        }

        return null; // no matching format found
    }

    // Detect Date format
    public static String detectFormat(String dateStr) {
        for (String format : knownFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
                if (format.contains("dd")) {
                    LocalDate.parse(dateStr, formatter);
                } else {
                    LocalDate.parse("01-" + dateStr, DateTimeFormatter.ofPattern("dd-" + format));
                }
                return format;
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    // Convert any date to MM-yyyy format
    public static String toMonthYearFormat(String dateStr, String format) {
        DateTimeFormatter inputFormatter;
        LocalDate date;

        if (format.contains("dd")) {
            inputFormatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
            date = LocalDate.parse(dateStr, inputFormatter);
        } else {
            inputFormatter = DateTimeFormatter.ofPattern("dd-" + format, Locale.ENGLISH);
            date = LocalDate.parse("01-" + dateStr, inputFormatter);
        }

        return date.format(DateTimeFormatter.ofPattern("MM-yyyy"));
    }

    // Get range of months between two MM-yyyy formatted strings
    public static List<String> getMonthRange(String fromMMYYYY, String toMMYYYY) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

        LocalDate start = LocalDate.parse("01-" + fromMMYYYY, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate end = LocalDate.parse("01-" + toMMYYYY, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        List<String> months = new ArrayList<>();
        while (!start.isAfter(end)) {
            months.add(start.format(formatter));
            start = start.plusMonths(1);
        }
        return months;
    }

    // List all the month (in format MM-yyyy) from viewFrom to viewTo
    public static List<String> generateMonthList(String viewFrom, String viewTo) {
        String fromFormat = detectFormat(viewFrom);
        String toFormat = detectFormat(viewTo);

        if (fromFormat == null || toFormat == null) {
            throw new IllegalArgumentException("Unrecognized date format in input.");
           
        }

        String normalizedFrom = toMonthYearFormat(viewFrom, fromFormat);
        String normalizedTo = toMonthYearFormat(viewTo, toFormat);

        return getMonthRange(normalizedFrom, normalizedTo);
    }

    public static String[] splitDateTime(String datetime){
        if (datetime.contains("-")){
            return datetime.split("-");  
        }
        else{
            return datetime.split("/"); 
        }
    }

    public static String convertDateStringToAnotherFormat(String dateString, String fromFormat, String toFormat) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        if (fromFormat == null || fromFormat.isEmpty()) {
            fromFormat = "dd-MMM-yyyy";
        }
        if (toFormat == null || toFormat.isEmpty()) {
            toFormat = "dd-MMM-yyyy";
        }
        if (fromFormat.equals(toFormat)) {
            return dateString;
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(fromFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(toFormat);
            LocalDate date = LocalDate.parse(dateString, inputFormatter);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Error parsing date: " + e.getMessage();
        }
    }

     /**
     * Returns a date string based on "current", "future", or "past".
     *
     * @param type    "current", "future", or "past"
     * @param days    Number of days to add/subtract for future/past
     * @param format  Date format pattern (e.g., "yyyy-MM-dd")
     * @return        Formatted date string
     */
    public static String getDateString(String type, int days, String format) {
        LocalDate date = LocalDate.now();
        if (format == null || format.isEmpty()) {
            format = "dd-MMM-yyyy";
        }
        switch (type.toLowerCase()) {
            case "future":
                date = date.plusDays(days);
                break;
            case "past":
                date = date.minusDays(days);
                break;
            case "current":
                break;
            default:
                throw new IllegalArgumentException("Type must be 'current', 'future', or 'past'");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter).replace("Sept", "Sep");
    }

    /**
     * Returns a date string based on "current", "future", or "past".
     *
     * @param dateString    Date string to calculate month from
     * @param months    Number of month to add/subtract. Positive for future, negative for past
     * @param format  Date format pattern (e.g., "yyyy-MM-dd")
     * @param dateRequired  If null, return the same date as dateString, if 1, return the first day of the month, if 0, return the last day of the month
     * @return        Formatted date string
     */
    public static String getDateStringAfterMonths(String dateString, int months, String format, int dateRequired) {
        if (dateString == null || dateString.isEmpty()) {
            dateString = getDateString("current", 0, format);
        }
        if (format == null || format.isEmpty()) {
            format = "dd-MMM-yyyy";
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(format);
            LocalDate date = LocalDate.parse(dateString, inputFormatter);
            if (months > 0) {
                date = date.plusMonths(months);
            } else{
                date = date.minusMonths(Math.abs(months));
            }
            if (dateRequired == 1) {
                date = date.withDayOfMonth(1);
            } else if (dateRequired == 0) {
                date = date.withDayOfMonth(date.lengthOfMonth());
            }
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Error parsing date: " + e.getMessage();
        }
    }

    public static List<String> getListMonth(String InputDate,int monthRange,String format,boolean isPlus,boolean isRevert){
        List<String> ListMonth=new ArrayList<>();
        // String[] splited=InputDate.split("-");
    
        InputDate=convertDateFormatAdvanced(InputDate,"d-MMM-yyyy");

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("d-MMM-yyyy");
        LocalDate initialDate = LocalDate.parse(InputDate, dtf);
        if(isPlus){
            for(int i=0;i<=monthRange;i++) {
                LocalDate date = initialDate.plusMonths(i);
                String dateStr = date.format(dtf);
                String Formated=convertDateFormat(dateStr,"d-MMM-yyyy",format);
                ListMonth.add(Formated);
            }
        } else {
            for(int j=0;j<=monthRange;j++) {
                LocalDate date = initialDate.minusMonths(j);
                String dateStr = date.format(dtf);
                String Formated=convertDateFormat(dateStr,"d-MMM-yyyy",format);
                ListMonth.add(Formated);
            }
         }
        if(isRevert) {
			logger.passed(String.format("List month: %s", ListMonth.reversed()));
			return ListMonth.reversed();
		}
		else {
			logger.info(String.format("List month: %s", ListMonth));
			return ListMonth;
		}
    }

    /**
     * @param InputDate : Input Date
     * @param numDiff : number Days/Months diff
     * @param type:  day / month / year
     * @param isPlus true : Plus/ false: Minus
     * @param formatOutput: output format date
     * @return
     */
    public static String getDateFromInput(String InputDate,int numDiff,String type,boolean isPlus,String formatOutput){
        LocalDate date;
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern(detectDateFormatAdvanced(InputDate));
        LocalDate initialDate = LocalDate.parse(InputDate, dtf);
        if(isPlus){
            if(type.toLowerCase().equals("day")){
            date = initialDate.plusDays(numDiff);
            } else if(type.toLowerCase().equals("month")){
            date = initialDate.plusMonths(numDiff);
            } else if(type.toLowerCase().equals("year")){
            date = initialDate.plusYears(numDiff);
            } else {
                date=null;
                logger.error(String.format("Type %s is not support", type));
            }
        } else {
            if(type.toLowerCase().equals("day")){
                date = initialDate.minusDays(numDiff);
                } else if(type.toLowerCase().equals("month")){
                date = initialDate.minusMonths(numDiff);
                } else if(type.toLowerCase().equals("year")){
                date = initialDate.minusYears(numDiff);
                } else {
                    date=null;
                    logger.error(String.format("Type %s is not support", type));
                }
        }
        String dateStr = date.format(dtf);
        String Formated=convertDateFormatAdvanced(dateStr,formatOutput);
        logger.info(String.format("Date Output: %s", Formated));
        return Formated;
    }

    public static int countDiffBetweenTwoDate(String firstDate,String secondDate,String type){
        int numdiff=0;
  
        String[] lengthFirstDate=firstDate.split("-");
        String[] lengthSecondDate=secondDate.split("-");
        if(lengthFirstDate.length!=lengthSecondDate.length){
            logger.error(String.format("Format Pattern not matching: 1st: %s | 2nd: %s",firstDate,secondDate));
        }

        DateTimeFormatter dtf=DateTimeFormatter.ofPattern(detectDateFormatAdvanced(firstDate));

        String firstDateAfter=convertDateFormatAdvanced(firstDate,detectDateFormatAdvanced(firstDate));
        String SecondDateAfter=convertDateFormatAdvanced(secondDate,detectDateFormatAdvanced(firstDate));
       if(lengthFirstDate.length==3){
       LocalDate initialDate1st = LocalDate.parse(firstDateAfter, dtf);
       LocalDate initialDate2nd = LocalDate.parse(SecondDateAfter, dtf);
        if(type.toLowerCase().equals("day")){
            numdiff=(int) ChronoUnit.DAYS.between(initialDate1st, initialDate2nd);
          } else if(type.toLowerCase().equals("month")){
            numdiff=(int) ChronoUnit.MONTHS.between(initialDate1st, initialDate2nd);
          } else if(type.toLowerCase().equals("year")){
              numdiff=(int) ChronoUnit.YEARS.between(initialDate1st, initialDate2nd);
          } else {
              numdiff=0;
              logger.error(String.format("Type %s is not support", type));
              
          }
          logger.info(String.format("%s between is: %s", type,  Math.abs(numdiff)));
          
       } else if(lengthFirstDate.length==2){
       YearMonth initialDate1st = YearMonth.parse(firstDateAfter, dtf);
       YearMonth initialDate2nd = YearMonth.parse(SecondDateAfter, dtf);
        if(type.toLowerCase().equals("day")){
            numdiff=(int) ChronoUnit.DAYS.between(initialDate1st, initialDate2nd);
          } else if(type.toLowerCase().equals("month")){
            numdiff=(int) ChronoUnit.MONTHS.between(initialDate1st, initialDate2nd);
          } else if(type.toLowerCase().equals("year")){
              numdiff=(int) ChronoUnit.YEARS.between(initialDate1st, initialDate2nd);
          } else {
              numdiff=0;
              logger.error(String.format("Type %s is not support", type));
          }
          logger.info(String.format("%s between is: %s", type,Math.abs(numdiff)));
          
       }
       return Math.abs(numdiff);
       
    }


    public static String detectDateFormatAdvanced(String dateStr) {
        for (String pattern : knownFormats) {
            String[] splited=dateStr.split("-");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                if(splited.length==3){
                LocalDate.parse(dateStr, formatter);
                }else{
                YearMonth.parse(dateStr, formatter);
                }
                return pattern;
            } catch (DateTimeParseException ignored) {

            }
        }

        return null; // no matching format found
    }

    public static String convertLocalDateToYearMonth(String localdateInput,String yearMonthFormat){
        DateTimeFormatter inputFormatrer=DateTimeFormatter.ofPattern(detectDateFormatAdvanced(localdateInput));
        LocalDate date = LocalDate.parse(localdateInput,inputFormatrer);
        YearMonth yearMonth=YearMonth.from(date);
        DateTimeFormatter outputFormatrer=DateTimeFormatter.ofPattern(yearMonthFormat);
        String formatedOut=yearMonth.format(outputFormatrer);
        logger.info(String.format("Convert LocalDate: %s to YearMonth: %s",localdateInput,formatedOut));
        return formatedOut;
    }

    public static String convertYearMonthToLocalDate(String yearMonthInput,String LocalDateFormat){
        DateTimeFormatter inputFormatrer=DateTimeFormatter.ofPattern(detectDateFormatAdvanced(yearMonthInput));
        YearMonth yearMonth = YearMonth.parse(yearMonthInput,inputFormatrer);
        LocalDate date = yearMonth.atDay(1);
        DateTimeFormatter outputFormatrer=DateTimeFormatter.ofPattern(LocalDateFormat);
        String formatedOut=date.format(outputFormatrer);
        logger.info(String.format("Convert YearMonth %s to LocalDate: %s",yearMonthInput,formatedOut));
        return formatedOut;
    }



    public static String convertDateFormatAdvanced(String inputDate, String outputFormat) {    
        String[] splitedIn=inputDate.split("-");
        String[] splitedOut=outputFormat.split("-");
        try {
            if(splitedIn.length>splitedOut.length){
                inputDate=convertLocalDateToYearMonth(inputDate,outputFormat);
            }else if (splitedIn.length<splitedOut.length){
                inputDate=convertYearMonthToLocalDate(inputDate,outputFormat);
            }
            String inputFormat=detectDateFormatAdvanced(inputDate);
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
            if(splitedOut.length==3){
            LocalDate date = LocalDate.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
            }else {
            YearMonth date =YearMonth.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
            }
            // return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Invalid date or format: " + e.getMessage();
        }
    }

}