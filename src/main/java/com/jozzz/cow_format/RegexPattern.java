package com.jozzz.cow_format;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jozzz.Main;
import com.jozzz.util.RunDB;

public class RegexPattern {

    public static  ArrayList<String[]> filterData(ArrayList<String[]> inputData, String pattern,int position) {
        ArrayList<String[]> filteredData = new ArrayList<>();
        System.out.println(inputData.size());
        System.out.println(pattern);
        Pattern regexPattern = Pattern.compile(pattern);

        for (String[] value : inputData) {
            
            Matcher matcher = regexPattern.matcher(value[position].trim());
            if (matcher.matches()) {
                filteredData.add(value);
            }
        }
        return filteredData;
    }

    public static  ArrayList<String[]> filterDataWithId(ArrayList<String[]> inputData, String id) {
        ArrayList<String[]> filteredData = new ArrayList<>();
        System.out.println(inputData.size());
        System.out.println(id);
        for (String[] value : inputData) {
            if (value[4].equals(id)) {
                filteredData.add(value);
            }
        }
        return filteredData;
    }
    
    public static void setRegexProperties(){
        Properties properties = new Properties();

        //        String percentRegx = "([1-9]\\d?(\\.\\d*)?|100)\\s*%?";
//        String percentRegx = "\\d+(\\.\\d+)?\\s*%";
        String percentRegx = "\\d+\\.*\\d*\\s*%$";
        String numberRegx = "\\d+\\.?\\d*\\s*";
        String percentAndEngRegx = "\\d+(\\.\\d+)?\\s*%\\s*([a-zA-Z]+\\s*)+";
        String percentAndThaiRegx = "\\d+(\\.\\d+)?\\s*%\\s*([ก-๙]+\\s*)+";
        String engAndPercentRegx = "([a-zA-Z]+\\s*)+\\d+(\\.\\d+)?\\s*%";
        String thaiAndPercentRegx = "([ก-๙]+\\s*)+\\d+(\\.\\d+)?\\s*%";
        String numAndEngRegx = "\\d+\\.*\\d*\\s*([a-zA-Z]+\\s*)+";
        String numAndThaiRegx = "\\d+\\.*\\d*\\s*([ก-๙]+\\s*)+";
        String engAndNumRegx = "([a-zA-Z]+\\s*)+\\d+\\.*\\d*";
        String thaiAndNumRegx = "([ก-๙]+\\s*)+\\d+\\.*\\d*";
        String engMultiRegx = "(\\d+\\.*\\d*\\s*%\\s*[a-zA-Z]*\\s*)+";
        String thaiMultiRegx = "(\\d+\\.*\\d*\\s*%\\s*[ก-๙]*\\s*)+";
        String commaRegx = ".+\\,.+";
        String plusRegx = ".+\\+.+";
        String letterAndNumMultiRegx = "([a-zA-Zก-๙]*\\s*\\d+\\.*\\d*\\s*%\\s*\\s*)+";
        String letterRegx = "([^0-9]+\\s*)+";
        String thaiPercentEng = "([ก-๙]+\\s*)+\\d+\\.*\\d*\\s*%\\s*([a-zA-Z]+\\s*)+";

        String[] tabsName = {"Percent", "Number", "Percent & Eng", "Percent & Thai", "Eng & Percent", "Thai & Percent", "Num & Eng", "Num & Thai", "Eng & Num", "Thai & Num", "Eng Multi", "Thai Multi", "Comma", "Plus", "Letter & Num Multi", "Letter", "Thai Per Eng"};
        List<String[]> regexList = new ArrayList<>();

        regexList.add(new String[] { "Percent", "\\d+\\.*\\d*\\s*%$" });
        regexList.add(new String[] { "Number", "\\d+\\.?\\d*\\s*" });
        regexList.add(new String[] { "Percent & Eng", "\\d+(\\.\\d+)?\\s*%\\s*([a-zA-Z]+\\s*)+" });
        regexList.add(new String[] { "Percent & Thai", "\\d+(\\.\\d+)?\\s*%\\s*([ก-๙]+\\s*)+" });
        regexList.add(new String[] { "Eng & Percent", "([a-zA-Z]+\\s*)+\\d+(\\.\\d+)?\\s*%" });
        regexList.add(new String[] { "Thai & Percent", "([ก-๙]+\\s*)+\\d+(\\.\\d+)?\\s*%" });
        regexList.add(new String[] { "Num & Eng", "\\d+\\.*\\d*\\s*([a-zA-Z]+\\s*)+" });
        regexList.add(new String[] { "Num & Thai", "\\d+\\.*\\d*\\s*([ก-๙]+\\s*)+" });
        regexList.add(new String[] { "Eng & Num", "([a-zA-Z]+\\s*)+\\d+\\.*\\d*" });
        regexList.add(new String[] { "Thai & Num", "([ก-๙]+\\s*)+\\d+\\.*\\d*" });
        regexList.add(new String[] { "Eng Multi", "(\\d+\\.*\\d*\\s*%\\s*[a-zA-Z]*\\s*)+" });
        regexList.add(new String[] { "Thai Multi", "(\\d+\\.*\\d*\\s*%\\s*[ก-๙]*\\s*)+" });
        regexList.add(new String[] { "Comma", ".+\\,.+" });
        regexList.add(new String[] { "Plus", ".+\\+.+" });
        regexList.add(new String[] { "Letter & Num Multi", "([a-zA-Zก-๙]*\\s*\\d+\\.*\\d*\\s*%\\s*\\s*)+" });
        regexList.add(new String[] { "Letter", "([^0-9]+\\s*)+" });
        regexList.add(new String[] { "Thai Per Eng", "([ก-๙]+\\s*)+\\d+\\.*\\d*\\s*%\\s*([a-zA-Z]+\\s*)+" });

        for (String[] regex : regexList) {
            properties.setProperty(regex[0], regex[1]);
        }

        try {
            FileOutputStream fos = new FileOutputStream("regex.properties");
            properties.store(fos, "Regex Patterns");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> loadRegexProperties(){
        List<String[]> regexList = new ArrayList<>();

        Properties properties = new Properties();
        Main.class.getClassLoader().getResourceAsStream("regex.properties");
        
        try {
           
            properties.load(Main.class.getClassLoader().getResourceAsStream("regex.properties"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int countRegex = countRegexKeys(properties, "regex");
        System.out.println(countRegex);
        for (int i = 0; i < countRegex; i++) {
            String key = "regex" + i; // Assuming your keys are like regex0, regex1, ...
            String data = properties.getProperty(key);
            
            String[] splitValue = data.split("=");
            String regexName = splitValue[0];
            String regex = splitValue[1];
            regexList.add(new String[]{regexName,regex});
        }
        
        // regexList.add(new String[]{"Number", properties.getProperty("Number")});
        // regexList.add(new String[]{"Percent & Eng", properties.getProperty("Percent & Eng")});
        // regexList.add(new String[]{"Percent & Thai", properties.getProperty("Percent & Thai")});
        // regexList.add(new String[]{"Eng & Percent", properties.getProperty("Eng & Percent")});
        // regexList.add(new String[]{"Thai & Percent", properties.getProperty("Thai & Percent")});
        // regexList.add(new String[]{"Num & Eng", properties.getProperty("Num & Eng")});
        // regexList.add(new String[]{"Num & Thai", properties.getProperty("Num & Thai")});
        // regexList.add(new String[]{"Eng & Num", properties.getProperty("Eng & Num")});
        // regexList.add(new String[]{"Thai & Num", properties.getProperty("Thai & Num")});
        // regexList.add(new String[]{"Eng Multi", properties.getProperty("Eng Multi")});
        // regexList.add(new String[]{"Thai Multi", properties.getProperty("Thai Multi")});
        // regexList.add(new String[]{"Comma", properties.getProperty("Comma")});
        // regexList.add(new String[]{"Plus", properties.getProperty("Plus")});
        // regexList.add(new String[]{"Letter & Num Multi", properties.getProperty("Letter & Num Multi")});
        // regexList.add(new String[]{"Letter", properties.getProperty("Letter")});
        // regexList.add(new String[]{"Thai Per Eng", properties.getProperty("Thai Per Eng")});

        return regexList;
    }

    public static List<String[]> loadFarmIdProperties(){
        List<String[]> farmIdList = new ArrayList<>();

        Properties properties = new Properties();

        try {
            FileInputStream fis = new FileInputStream("regex.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int countFarmId = countRegexKeys(properties, "farm_id");
        System.out.println(countFarmId);
        for (int i = 1; i <= countFarmId; i++) {
            String key = "farm_id" + i; // Assuming your keys are like regex0, regex1, ...
            String data = properties.getProperty(key);
            
            farmIdList.add(new String[]{"farm_id_"+data,data});
            System.out.println(data);
        }
        return farmIdList;
    }

    private static int countRegexKeys(Properties properties , String prefix){
        int count = 0;
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                count++;
            }
        }

        return count;
    }
}
