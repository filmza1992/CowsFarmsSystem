package com.jozzz.gui.dip;

import com.jozzz.constant.DisplayState;
import com.jozzz.cow_format.RegexPattern;
import com.jozzz.gui.component.CowsTable;
import com.jozzz.util.CardPage;
import com.jozzz.util.DataDB;
import com.jozzz.util.Dialog;
import com.jozzz.util.Element;
import com.jozzz.util.RunDB;
import com.jozzz.util.WriteXlsxFile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hpsf.Array;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.util.List;

public class TableListSelect extends JPanel {
    private ArrayList<String[]> allDairyBreedPattern;
    private boolean isPageLoading = true;
    ArrayList<String[]> mapedRows = new ArrayList<>();
    ArrayList<String[]> mapedProject = new ArrayList<>();

    public TableListSelect() {
        Dialog dialog = new Dialog();
        new Thread(() -> {
            try {
                allDairyBreedPattern = DataDB.getAllDairBreedCode();
                setPageLoading(false);
                SwingUtilities.invokeLater(() -> {
                    init();
                    dialog.getDialog().setVisible(false);
                });
            } catch (Exception e) {
                setPageLoading(true);
                dialog.getDialog().setVisible(false);
                SwingUtilities.invokeLater(() -> CardPage.showPage(DisplayState.MAIN_MENU));
            }
        }).start();
        dialog.getDialog().setVisible(true);
    }

    private void init() {
        this.setPreferredSize(new Dimension(1366, 768));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());

        JPanel appBarPanel = new JPanel();
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BorderLayout());

        String[] columnList = { "List" };
        String[] columnSelectedList = { "Selected List" };
        List<String[]> regex = RegexPattern.loadRegexProperties();
        ArrayList<String[]> tempDairyBreed = new ArrayList<>(allDairyBreedPattern);

        ArrayList<String[]> filter = RegexPattern.filterData(tempDairyBreed, regex.get(15)[1], 0);
        ArrayList<String[]> dataExcel = findNameInExcel();

        HashSet<String> keyMaped = new HashSet<>();

        if (!dataExcel.isEmpty()) {
            for (int i = 0; i < filter.size(); i++) {
                String[] filterData = filter.get(i);
                for (String[] dataExcelRow : dataExcel) {
                    if (filterData[0].equals(dataExcelRow[1])) {
                        filter.remove(i);
                        i--; // ลดค่าตัวแปร i เพื่อปรับปรุงตำแหน่งใหม่หลังจากการลบ
                        break; // ออกจากลูปภายในเมื่อพบสมาชิกที่ต้องการลบแล้ว
                    }

                }
            }
            for (String[] dataExcelRow : dataExcel) {
                if (dataExcelRow[0].equals("Key")) {
                    continue;
                }
                keyMaped.add(dataExcelRow[0]);
            }
            mapedProject = dataExcel;
        }

        CowsTable table1 = new CowsTable(filter, columnList, false);
        CowsTable table2 = new CowsTable(new ArrayList<>(), columnSelectedList, false);

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel keyLabel = new JLabel("Key Breed:");
        JTextField keyTextField = new JTextField(10);

        keyPanel.add(keyLabel);
        keyPanel.add(keyTextField);

        JButton moveToTable2Button = new JButton("ย้ายไป Selected List >>");
        moveToTable2Button.setFont(Element.getFont(15));
        JButton moveToTable1Button = new JButton("<< ย้ายไป List");
        moveToTable1Button.setFont(Element.getFont(15));

        moveToTable2Button.addActionListener(
                event -> moveSelectedRows(table1.getTable(), table1.getTableModel(), table2.getTableModel()));
        moveToTable1Button.addActionListener(
                event -> moveSelectedRows(table2.getTable(), table2.getTableModel(), table1.getTableModel()));

        JPanel tablePanel = new JPanel(new GridLayout(1, 2));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setPreferredSize(new Dimension(0, 50));
        buttonPanel.add(moveToTable2Button);
        buttonPanel.add(moveToTable1Button);

        JPanel movePanel = new JPanel(null);
        tablePanel.add(table1);
        tablePanel.add(movePanel);

        moveToTable1Button.setBounds(100, 250, 315, 50);
        moveToTable2Button.setBounds(100, 100, 315, 50);

        JTextArea mapTextArea = new JTextArea();
        JLabel mapLabel = new JLabel("ประวัติ Key");
        mapTextArea.setBounds(100, 340, 315, 200);
        mapTextArea.setFont(Element.getFont(15));
        JScrollPane scrollPane = new JScrollPane(mapTextArea);
        scrollPane.setBounds(100, 340, 315, 200);
        scrollPane.setFont(Element.getFont(15));
        mapLabel.setBounds(100, 320, 315, 20);
        mapLabel.setFont(Element.getFont(20));

        movePanel.add(moveToTable1Button);
        movePanel.add(moveToTable2Button);
        movePanel.add(mapLabel);
        movePanel.add(scrollPane);

        tablePanel.add(table2);

        JPanel menuBarPanel = new JPanel();
        menuBarPanel.setPreferredSize(new Dimension(0, 50));
        menuBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel menuCenterPanel = new JPanel();
        menuBarPanel.setPreferredSize(new Dimension(0, 50));
        menuBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton backButton = new JButton("ย้อนกลับ");
        backButton.setFont(Element.getFont(15));
        backButton.addActionListener(event -> CardPage.showPage(DisplayState.MAIN_MENU));

        JButton exportButton = new JButton("ส่งออกเป็นไฟล์ Excel (.xlsx)");
        exportButton.setFont(Element.getFont(15));
        exportButton.addActionListener(event -> WriteXlsxFile.exportToExcel(mapedRows, mapedProject));

        JButton readButton = new JButton("อ่านไฟล์ Excel (.xlsx)");
        readButton.setFont(Element.getFont(15));
        readButton.addActionListener(event -> readExcelData(mapTextArea, keyMaped, table1));
        JButton mapButton = new JButton("เพิ่ม");
        mapButton.setFont(Element.getFont(15));
        mapButton.addActionListener(
                event -> mapKey(table2.getTable(), keyTextField, (DefaultTableModel) table2.getTable().getModel(),
                        keyMaped, mapTextArea));

        JButton editButton = new JButton("แก้ไข");
        editButton.setFont(Element.getFont(15));
        editButton.addActionListener(
                event -> editExcelFile(keyTextField.getText(), table2, keyMaped, mapTextArea));

        JButton deleteButton = new JButton("ลบ");
        deleteButton.setFont(Element.getFont(15));
        deleteButton.addActionListener(
                event -> deleteKey(table1, mapTextArea, keyMaped, keyTextField.getText()));

        menuBarPanel.add(backButton);
        menuBarPanel.add(exportButton);
        menuBarPanel.add(readButton);
        menuCenterPanel.add(keyPanel);
        menuCenterPanel.add(mapButton);
        menuCenterPanel.add(editButton);
        menuCenterPanel.add(deleteButton);

        bodyPanel.add(menuCenterPanel, BorderLayout.NORTH);
        bodyPanel.add(tablePanel, BorderLayout.CENTER);
        bodyPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(menuBarPanel, BorderLayout.NORTH);
        this.add(bodyPanel, BorderLayout.CENTER);
    }

    private void moveSelectedRows(JTable sourceTable, DefaultTableModel sourceModel, DefaultTableModel targetModel) {
        int[] selectedRows = sourceTable.getSelectedRows();
        Vector<Vector<Object>> rowsToMove = new Vector<>();

        for (int row : selectedRows) {
            Vector<Object> rowData = new Vector<>();
            for (int col = 0; col < sourceModel.getColumnCount(); col++) {
                rowData.add(sourceModel.getValueAt(row, col));
            }
            rowsToMove.add(rowData);
        }

        for (Vector<Object> rowData : rowsToMove) {
            targetModel.addRow(rowData);
        }

        for (int i = selectedRows.length - 1; i >= 0; i--) {
            sourceModel.removeRow(selectedRows[i]);
        }
    }

    private ArrayList<String[]> findNameInExcel() {
        ArrayList<String[]> foundNames = new ArrayList<>();
        String excelFilePath = "BreedMap.xlsx";

        File excelFile = new File(excelFilePath);
        try (FileInputStream fis = new FileInputStream(excelFilePath);
                Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            for (Row row : sheet) {
                Cell cell0 = row.getCell(0);
                Cell cell1 = row.getCell(1); // Assuming name is in the second column
                if (cell0 != null && cell0.getCellType() == CellType.STRING) {
                    String key = cell0.getStringCellValue();
                    String name = cell1.getStringCellValue();
                    // Add the name to the foundNames list
                    foundNames.add(new String[] { key, name });
                    System.out.println(name);
                }
            }
        } catch (IOException | EncryptedDocumentException ex) {
            ex.printStackTrace();
        }
        return foundNames;
    }

    private void editExcelFile(String key, CowsTable table2, HashSet<String> keyMapSet, JTextArea mapTextArea) {
        mapedRows.clear();
        for (int i = 0; i < mapedProject.size(); i++) {
            if (key.equals(mapedProject.get(i)[0])) {
                DefaultTableModel table2Model = (DefaultTableModel) table2.getTable().getModel();
                table2Model.addRow(new String[] { mapedProject.get(i)[1] });
                mapedProject.remove(i);
                i--;
            }
        }
        mapTextArea.setText("");
        String[] keyMapString = keyMapSet.toArray(new String[keyMapSet.size()]);
        System.out.println(keyMapString.length);
        for (int i = 0; i < keyMapString.length; i++) {
            if (keyMapString[i].equals(key)) {
                keyMapSet.remove(keyMapString[i]);
                continue;
            }
            System.out.println(keyMapString[i]);
            mapTextArea.append(keyMapString[i] + "\n");
        }

        System.out.println("EDIT SUCCESS " + key);
        System.out.println("====");
        System.out.println("Map in project");
        printArrayList(mapedProject);
        System.out.println("====");
        System.out.println("Map to user");
        printArrayList(mapedRows);

    }

    private void deleteKey(
            CowsTable table1, JTextArea keyTextField, HashSet<String> mapHashSet, String key) {
        // Provide the path to your Excel file
        mapedRows.clear();
        DefaultTableModel tableModel1 = (DefaultTableModel) table1.getTable().getModel();

        for (int i = 0; i < mapedProject.size(); i++) {
            if (key.equals(mapedProject.get(i)[0])) {
                tableModel1.addRow(new String[] { mapedProject.get(i)[1] });
                mapedProject.remove(i);
                i--;
            }
        }

        tableModel1.fireTableDataChanged(); // Refresh the table view
        keyTextField.setText("");

        mapHashSet.remove(key);
        String[] keyMapString = mapHashSet.toArray(new String[mapHashSet.size()]);
        System.out.println(keyMapString.length);
        for (int i = 0; i < keyMapString.length; i++) {
            System.out.println(keyMapString[i]);
            keyTextField.append(keyMapString[i] + "\n");
        }
        System.out.println("DELETE SUCCESS " + key);
        System.out.println("====");
        System.out.println("Map in project");
        printArrayList(mapedProject);
        System.out.println("====");
        System.out.println("Map to user");
        printArrayList(mapedRows);

        
    }

    private void readExcelData(JTextArea mapTextArea, HashSet<String> keyMaped, CowsTable table1) {
        mapedProject.clear();
        mapTextArea.setText("");
        keyMaped.clear();
        mapedProject = WriteXlsxFile.readExcelData(mapedRows, mapedProject, mapTextArea, keyMaped);
        System.out.println("====");
        System.out.println("Map in project");
        printArrayList(mapedProject);
        System.out.println("====");
        System.out.println("Map to user");
        printArrayList(mapedRows);
        for (String key : keyMaped) {
            System.out.println(key);
        }

        DefaultTableModel tableModel1 = (DefaultTableModel) table1.getTable().getModel();

        for (int i = 0; i < mapedProject.size(); i++) {
            String[] row = mapedProject.get(i);
            for (int j = 0; j < tableModel1.getRowCount(); j++) {

                if (row[1].equals(tableModel1.getValueAt(j, 0))) {
                    System.out.println(row[1] + "===" + tableModel1.getValueAt(j, 0));
                    tableModel1.removeRow(j);
                    break; // Assuming each row in the JTable has unique identifiers
                }
            }
        }

    }

    private void mapKey(JTable rightTable, JTextField keyTextField, DefaultTableModel rightTableModel,
            HashSet<String> keyMapSet, JTextArea mapTextArea) {
        String key = keyTextField.getText().trim();
        int rightRowCount = rightTableModel.getRowCount();
        mapedRows.clear();
        if (key.equals("")) {
            System.out.println("Dont have key");
            return;
        }

        // เตรียมหัวของไฟล์ Excel

        for (int i = rightRowCount - 1; i >= 0; i--) {
            String[] rowData = new String[rightTableModel.getColumnCount() + 1]; // เก็บข้อมูลของแถวที่ถูกลบ
            rowData[0] = key; // เพิ่ม Key ในแถวแรก
            for (int j = 1; j < rightTableModel.getColumnCount() + 1; j++) {
                rowData[j] = rightTableModel.getValueAt(i, j - 1).toString();
            }
            mapedProject.add(rowData);
            mapedRows.add(rowData); // เพิ่มข้อมูลที่ถูกลบลงในรายการ
            System.out.println(rowData[1]);
            rightTableModel.removeRow(i);
        }

        mapTextArea.setText("");

        keyMapSet.add(key);
        String[] keyMapString = keyMapSet.toArray(new String[keyMapSet.size()]);
        System.out.println(keyMapString.length);
        for (int i = 0; i < keyMapString.length; i++) {
            System.out.println(keyMapString[i]);
            mapTextArea.append(keyMapString[i] + "\n");
        }
        System.out.println("MAP SUCCESS " + key);
        keyTextField.setText("");
        System.out.println("====");
        System.out.println("Map in project");
        printArrayList(mapedProject);
        System.out.println("====");
        System.out.println("Map to user");
        printArrayList(mapedRows);
        // for(String key : ){

        // }
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
            if (count == 20) {
                break;
            }
            count++;
        }
    }
}
