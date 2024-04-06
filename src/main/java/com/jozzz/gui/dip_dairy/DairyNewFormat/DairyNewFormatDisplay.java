package com.jozzz.gui.dip_dairy.DairyNewFormat;

import com.jozzz.algor.OldBreedIdString;
import com.jozzz.constant.DisplayState;
import com.jozzz.cow_format.RegexPattern;
import com.jozzz.gui.component.CowsTable;
import com.jozzz.records.DataTab;
import com.jozzz.util.*;
import com.jozzz.util.Dialog;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DairyNewFormatDisplay extends JPanel {


    private ArrayList<String[]> cowData;
    private ArrayList<String[]> listAllBreedMain;
    private ArrayList<String[]> selectedBreed;
    private ArrayList<String[]> selectedBreedFarmId;
    private JTabbedPane tabbedPane;
    private ArrayList<DataTab> allDataTabs;
    private final String[] columnAlLBreed = {"breed_uuid", "breed_code", "breed_name"};
    private final String[] columnAlLPattern = {"Cow_id","Cow_name","Cow_fa" ,"Cow_ma","Farm_id","breed_id","breed_code","breed_name","breed_id_string","new_breed_name_string","new_breed_id_string","sum_breed"};
    private boolean isPageLoading = true;

    private ArrayList<String[]> tempRegex;
    private ArrayList<String[]> tempFarmId;

    public DairyNewFormatDisplay(){
        Dialog dialog = new Dialog();
        new Thread(() -> {
            try {

                cowData = RunDB.getAllDairyBreedPattern();
                //cowData = DataDB.getAllDairyCowBreedForPattern();
                listAllBreedMain = RunDB.getAllDairyBreedMain();
                
                setPageLoading(false);
                SwingUtilities.invokeLater(() -> {
                    init();
                    dialog.getDialog().setVisible(false);
                });
            }catch (Exception e){
                setPageLoading(true);
                dialog.getDialog().setVisible(false);
                SwingUtilities.invokeLater(() -> CardPage.showPage(DisplayState.MAIN_MENU));
            }
        }).start();
        dialog.getDialog().setVisible(true);
    }

    private void init(){
        this.setPreferredSize(new Dimension(1366, 768));
        this.setBorder(new EmptyBorder(10,10,10,10));
        this.setLayout(new BorderLayout());
 
        List<String[]> regexList = RegexPattern.loadRegexProperties();
        List<String[]> farmIdList = RegexPattern.loadFarmIdProperties();

        allDataTabs = new ArrayList<>();
        selectedBreed = new ArrayList<>();
        selectedBreedFarmId = new ArrayList<>();
        ArrayList<String[]> notHaveStringBreed = new ArrayList<>(cowData);
        
        //temp for all cow data
        tempRegex = new ArrayList<>(cowData);
        tempFarmId = new ArrayList<>(cowData);
        
        for (String[] regex : regexList) {
            
            ArrayList<String[]> filteredData = RegexPattern.filterData(tempRegex, regex[1],7);

            allDataTabs.add(new DataTab(regex[0],DataRow.formatDairyNewFormatDisplay(filteredData,selectedBreed)));
            printArrayList(filteredData);
            printArrayList(tempRegex);
            tempRegex.removeAll(filteredData);
        }


        for (String[] id : farmIdList) {
            
            ArrayList<String[]> filteredData = RegexPattern.filterDataWithId(tempFarmId,id[1]);

            allDataTabs.add(new DataTab(id[0],DataRow.formatDairyNewFormatDisplay(filteredData,selectedBreedFarmId)));
            
            tempFarmId.removeAll(filteredData);
        }
        allDataTabs.add(new DataTab("Other",
        tempRegex));

        notHaveStringBreed.removeAll(selectedBreed);

        allDataTabs.add(new DataTab("BreedIdString", selectedBreed));
        allDataTabs.add(new DataTab("NotHaveBreedIdString", notHaveStringBreed));
        
        createTable();

        JPanel menuBarPanel = new JPanel();
        menuBarPanel.setPreferredSize(new Dimension(0,50));
        menuBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton backButton = new JButton("ย้อนกลับ");
        backButton.setFont(Element.getFont(15));
        backButton.addActionListener(event -> CardPage.showPage(DisplayState.MAIN_MENU));

        JButton exportButton = new JButton("ส่งออกเป็นไฟล์ Excel (.xlsx)");
        exportButton.setFont(Element.getFont(15));
        exportButton.addActionListener(event -> WriteXlsxFile.selectFileDialog(tabbedPane));

        menuBarPanel.add(backButton);
        menuBarPanel.add(exportButton);
        this.add(menuBarPanel, BorderLayout.NORTH);
    }

    private void createTable(){
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Element.getFont(15));

        for (DataTab tab : allDataTabs){
            tabbedPane.add(tab.title(),
                    new CowsTable(tab.data(), columnAlLPattern, false));
        }

        this.add(tabbedPane);
    }

    public boolean isPageLoading() {
        return isPageLoading;
    }

    public void setPageLoading(boolean pageLoading) {
        isPageLoading = pageLoading;
    }
      public static void printArrayList(ArrayList<String[]> arrayList) {
        int count = 0;
        for (String[] array : arrayList) {
            System.out.print("[ ");
            for (String element : array) {
                System.out.print(element + ", ");
            }
            System.out.println("]");
            if(count == 20){
                break;
            }
            count++;
        }
    }
}
