import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;


/**
 * Created by tanzhikaivinson on 5/29/17.
 */
public class proquest {

    ArrayList<ArrayList<String>> compilationOfArticles = new ArrayList<>();
    String folder_name = null;
    String folder_name_mod = null;

    String path;

    public proquest(String inputFolderName) {

        folder_name = inputFolderName;
        if(inputFolderName.contains("/")) {
            folder_name_mod = inputFolderName.substring(inputFolderName.lastIndexOf("/") + 1);
        } else {
            folder_name_mod = folder_name;
        }

        path = System.getProperty("user.dir");
        System.out.println(">>>>>" + path);

        JFrame frame = new JFrame(folder_name_mod + "/DEXTER - PROQUEST");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel space_1 = new JLabel(folder_name_mod + "running");
        frame.getContentPane().add(space_1);
        frame.setSize(350,200);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.YELLOW);




        splitText();
        double textFile_Count = compilationOfArticles.size() + 0.0;
        int folder_Count = (int)Math.ceil(textFile_Count/1000.0);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("ProQuest");;
        Row row = null;
        Cell cell = null;

        int rowCount = 0;

        row = sheet.createRow(rowCount++);
        cell = row.createCell(0);
        cell.setCellValue("ID");

        cell = row.createCell(1);
        cell.setCellValue("Source");

        cell = row.createCell(2);
        cell.setCellValue("Publisher");

        cell = row.createCell(3);
        cell.setCellValue("Headline");

        /*
        cell = row.createCell(4);
        cell.setCellValue("Length");
        */

        cell = row.createCell(4);
        cell.setCellValue("Dateline");

        cell = row.createCell(5);
        cell.setCellValue("Date");

        cell = row.createCell(6);
        cell.setCellValue("Wire Services");

        cell = row.createCell(7);
        cell.setCellValue("Length (TEXT ONLY)");

        cell = row.createCell(8);
        cell.setCellValue("Negative Count");

        cell = row.createCell(9);
        cell.setCellValue("Positive Count");

        cell = row.createCell(10);
        cell.setCellValue("Neg_Positive Count");

        cell = row.createCell(11);
        cell.setCellValue("Neg_Negative Count");

        cell = row.createCell(12);
        cell.setCellValue("Total_Positive");

        cell = row.createCell(13);
        cell.setCellValue("Total_Negative");

        cell = row.createCell(14);
        cell.setCellValue("Tone");

        splitText();

        //first loop is a loop of the different articles
        for(int i = 0; i < compilationOfArticles.size(); i++) {

            row = sheet.createRow(rowCount);
            cell = row.createCell(0);
            cell.setCellValue(rowCount++);

            cell = row.createCell(1);
            cell.setCellValue("ProQuest");

            for(int j = 0; j < compilationOfArticles.get(i).size(); j++) {

                String line = compilationOfArticles.get(i).get(j);

                if(line.contains("Publication title:")) {
                    cell = row.createCell(2);
                    line = line.substring(19);
                    cell.setCellValue(line);
                }

                if(line.contains("Title:")) {

                    if(row.getCell(3) == null) {
                        cell = row.createCell(3);
                        line = line.substring(7);
                        cell.setCellValue(line);
                    }
                }

                if(line.contains("Dateline:")) {
                    cell = row.createCell(4);
                    line = line.substring(10);
                    cell.setCellValue(line);
                }

                if(line.toLowerCase().contains("publication date:")) {
                    cell = row.createCell(5);
                    line = getDate(line.substring(18));
                    cell.setCellValue(line);
                }

                if (line.contains("Credit:") && line.contains("Associated Press")) {
                    cell = row.createCell(6);
                    cell.setCellValue(1);
                }

                if (line.contains("Credit:") && line.contains("Reuters")) {
                    cell = row.createCell(6);
                    cell.setCellValue(1);
                }

                if (line.contains("Credit:") && line.contains("Agence France-Presse")) {
                    cell = row.createCell(6);
                    cell.setCellValue(1);
                }
            }
        }

        try {
            for(int a = 1; a <= folder_Count; a++) {

                String command = "java -jar " + path + "/Lexicoder_0.jar pre dat=" + folder_name + "/" + folder_name_mod + "_Split/folder_split_text_only_" + a;
                Process proc = Runtime.getRuntime().exec(command);
                proc.waitFor();

                command = "java -jar " + path + "/Lexicoder_0.jar wc dat=" + folder_name + "/" + folder_name_mod + "_Split/folder_split_text_only_" + a;
                proc = Runtime.getRuntime().exec(command);
                proc.waitFor();
                InputStream in = proc.getInputStream();
                byte b[] = new byte[in.available()];
                in.read(b, 0, b.length);
                String wc = new String(b);
                String wcArray[] = wc.split("\n");

                command = "java -jar " + path + "/Lexicoder_0.jar dc dat=" + folder_name + "/" + folder_name_mod + "_Split/folder_split_text_only_" + a + " md=" + path + "/LSD2015.lc3";
                proc = Runtime.getRuntime().exec(command);
                proc.waitFor();
                in = proc.getInputStream();
                b = new byte[in.available()];
                in.read(b, 0, b.length);
                String resultDirect = new String(b);
                String resultDirectArray[] = resultDirect.split("\n");

                command = "java -jar " + path + "/Lexicoder_0.jar dc dat=" + folder_name + "/" + folder_name_mod + "_Split/folder_split_text_only_" + a + " md=" + path + "/LSD2015_NEG.lc3";
                proc = Runtime.getRuntime().exec(command);
                proc.waitFor();
                in = proc.getInputStream();
                b = new byte[in.available()];
                in.read(b, 0, b.length);
                String resultNeg = new String(b);
                String resultNegArray[] = resultNeg.split("\\n");

                //System.out.println(resultNeg);
                //System.out.println("-----" + a + " Neg -----");

                for (int i = 1; i < resultDirectArray.length; i++) {

                    String avatar[] = wcArray[i].split("\t");
                    String james[] = resultDirectArray[i].split("\t");
                    String cameron[] = resultNegArray[i].split("\t");

                    //System.out.println(">>>>> neg: " + james[1] + " pos: " + james[2] + " neg_positive: " + cameron[1] + " neg_negative: " + cameron[2]);

                    String shit = james[0];
                    int first = shit.indexOf('_');
                    int second = shit.indexOf('.');
                    String fecal = shit.substring(first + 1, second);

                    row = sheet.getRow(Integer.parseInt(fecal));

                    cell = row.createCell(7);
                    Double length = Double.parseDouble(avatar[1]);
                    cell.setCellValue(length);

                    cell = row.createCell(8);
                    Double neg_D = Double.parseDouble(james[1]);
                    cell.setCellValue(neg_D);

                    cell = row.createCell(9);
                    Double pos_D = Double.parseDouble(james[2]);
                    cell.setCellValue(pos_D);

                    cell = row.createCell(10);
                    Double neg_pos_D = Double.parseDouble(cameron[1]);
                    cell.setCellValue(neg_pos_D);

                    cell = row.createCell(11);
                    Double neg_neg_D = Double.parseDouble(cameron[2]);
                    cell.setCellValue(neg_neg_D);

                    Cell tot_pos = row.createCell(12);
                    Double totP_I = pos_D - neg_pos_D + neg_neg_D;
                    tot_pos.setCellValue(totP_I);

                    Cell tot_neg = row.createCell(13);
                    Double totN_I = neg_D - neg_neg_D + neg_pos_D;
                    tot_neg.setCellValue(totN_I);

                    Cell tone = row.createCell(14);
                    Double tone_D = (totP_I - totN_I)/length;
                    tone.setCellValue(tone_D);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //detecting problem articles
        boolean probArticles[] = new boolean[compilationOfArticles.size() + 1];
        for (int i = 1; i <= compilationOfArticles.size(); i++) {

            row = sheet.getRow(i);
            cell = row.getCell(2);

            if (row.getCell(6) == null) {
                cell = row.createCell(6);
                cell.setCellValue(0);
            }

            if (row.getCell(4) == null) {
               cell = row.createCell(4);
               cell.setCellValue(-99);
               probArticles[i] = true;
            }
        }

        try {
            File probArticleTextFile = new File(folder_name + "/" + folder_name_mod + "_Problem.txt");
            FileWriter articleWriter = new FileWriter(probArticleTextFile);
            BufferedWriter articleBW = new BufferedWriter(articleWriter);

            for (int i = 1; i < probArticles.length; i++) {

                if (probArticles[i] == true) {

                    articleBW.write("-------- -------- -------- -------- ID: " + i + " -------- -------- -------- --------\n");
                    articleBW.write("\n");

                    for(int j = 0; j < compilationOfArticles.get(i - 1).size(); j++) {
                        articleBW.write(compilationOfArticles.get(i - 1).get(j));
                        articleBW.write("\n");
                    }
                }
            }

            articleBW.close();
            articleWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }


        try {
            FileOutputStream output = new FileOutputStream(folder_name + "/" + folder_name_mod + ".csv");
            workbook.write(output);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        try {
            for (int i = 1; i <= folder_Count; i++) {
                FileUtils.deleteDirectory(new File(folder_name + "/" + folder_name_mod + "_Split/" + "folder_split_text_only_" + i));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        */
    }


    public String getDate(String string) {

        String year = string.substring(string.length() - 4);
        String monthday = string.substring(0,string.length() - 6);

        //System.out.println(monthday);
        String day = String.format("%02d",Integer.parseInt(monthday.replaceAll("\\D+","")));
        String month = getMonth(monthday.replaceAll("[^A-Za-z]+",""));
        //System.out.println(day + "/" + month + "/" + year);
        return month + "/" + day + "/" + year;
    }

    public String getMonth(String string) {

        String monS = string.toLowerCase();

        if(monS.equals("jan")) {
            return "01";
        } else if (monS.equals("feb")) {
            return "02";
        }else if (monS.equals("mar")) {
            return "03";
        }else if (monS.equals("apr")) {
            return "04";
        }else if (monS.equals("may")) {
            return "05";
        }else if (monS.equals("jun")) {
            return "06";
        }else if (monS.equals("jul")) {
            return "07";
        }else if (monS.equals("aug")) {
            return "08";
        }else if (monS.equals("sep")) {
            return "09";
        }else if (monS.equals("oct")) {
            return "10";
        }else if (monS.equals("nov")) {
            return "11";
        }else if (monS.equals("dec")) {
            return "12";
        }

        return null;
    }

    /*******************************************************************************************************/

    //this method takes a folder name as argument and splits the textfiles and outputs into the outputFolder
    //the folder should only contain textFiles

    public void splitFolder_text_only() {

        Double countD = compilationOfArticles.size() - 0.0;
        int FolderCount = (int)Math.ceil((countD)/1000.0);

        for(int i = 0; i < FolderCount; i++) {
            String destinedDirectoryName = folder_name + "/" + folder_name_mod + "_Split" + "/" + "folder_split_text_only_" + (i + 1);
            File destinedDirectoryFolder = new File(destinedDirectoryName);

            if(!destinedDirectoryFolder.exists()) {
                destinedDirectoryFolder.mkdir();
            }
        }

        for(int i = 0; i < compilationOfArticles.size(); i++) {

            int folder = (int)Math.ceil((i + 1.0)/1000.0);

            String destinedDirectoryName = folder_name + "/" + folder_name_mod + "_Split" + "/" + "folder_split_text_only_" + folder;
            File destinedDirectoryFolder = new File(destinedDirectoryName);

            int file_count = i + 1;
            String destinedFileName = destinedDirectoryName + "/article_" + file_count + ".txt";
            File destinedFile = new File(destinedFileName);
            FileWriter articleWriter = null;
            BufferedWriter articleBW = null;

            for (int j = 0; j < compilationOfArticles.get(i).size(); j++) {
                String line = compilationOfArticles.get(i).get(j);
                if(line.contains("Full text:")) {

                    try {
                        articleWriter = new FileWriter(destinedFile);
                        articleBW = new BufferedWriter(articleWriter);

                        line = line.substring(11);
                        while (line.trim().length() > 0 /*&& !line.contains("Credit:")*/) {
                            articleBW.write(line);
                            articleBW.write("\n");
                            line = compilationOfArticles.get(i).get(j++);
                        }

                        articleBW.close();
                        articleWriter.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println("BU CUO >>>>> " + i);

                    }

                    break;
                }
            }

        }
    }


    public void splitFolder() {

        Double countD = compilationOfArticles.size() - 0.0;
        int FolderCount = (int)Math.ceil((countD)/1000.0);

        for(int i = 1; i <= FolderCount; i++) {
            String destinedDir = folder_name + "/" + folder_name_mod + "_Split" + "/" + "folder_split_" + i;
            File theDig = new File(destinedDir);

            if (!theDig.exists()) {
                try {
                    theDig.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (i < FolderCount) {
                for(int j = 1; j <= 1000; j++) {
                    int fileCount = ((i - 1) * 1000) + j;
                    String fileName = "article_" + fileCount;
                    String currFilePath = folder_name + "/" + folder_name_mod + "_Split/" + fileName + ".txt";
                    String destinedFilePath = destinedDir;

                    File oldFile = new File(currFilePath);
                    File newFile = new File(destinedFilePath);

                    try {
                        FileUtils.copyFileToDirectory(oldFile,newFile);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }

            } else {

                for (int j = 1; j <= countD % 1000; j++) {
                    int fileCount = ((i - 1) * 1000) + j;
                    String fileName = "article_" + fileCount;
                    String currFilePath = folder_name + "/" + folder_name_mod + "_Split/" + fileName + ".txt";
                    String destinedFilePath = destinedDir;

                    //System.out.println(currFilePath + ">>>>>" + destinedFilePath);

                    File oldFile = new File(currFilePath);
                    File newFile = new File(destinedFilePath);

                    try {
                        FileUtils.copyFileToDirectory(oldFile, newFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void splitText() {

        compilationOfArticles = new ArrayList<>();

        String currDir = folder_name + "/" + folder_name_mod + "_Split";
        File thisDig = new File(currDir);
        if(!thisDig.exists()) {
            try {
                thisDig.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            //split the text files into separate articles
            File inputFolder = new File(folder_name);
            File[] file = inputFolder.listFiles();
            int count = 1;

            for(File txtFile : file) {

                if(!txtFile.toString().toLowerCase().contains(".txt") || txtFile.toString().toLowerCase().contains("problem")) {
                    continue;
                }

                //reads from the text file
                BufferedReader txtFileBR = new BufferedReader(new FileReader(txtFile));
                String line = txtFileBR.readLine();

                //writes into the text file
                FileWriter articleWriter = null;
                BufferedWriter articleBW = null;
                ArrayList<String> eachArticle = null;

                while(line != null) {

                    //indicates when to create a new article
                    //if (line.contains("Document") && line.contains("of") && checkStart(line)) {
                    if (line.contains("____________________________________________________________")) {
                        if (articleWriter != null && articleBW != null && eachArticle != null) {
                            articleBW.close();
                            articleWriter.close();

                            compilationOfArticles.add(eachArticle);
                        }

                        File currentArticle = new File( folder_name + "/" + folder_name_mod + "_Split/article_" + count + ".txt");
                        articleWriter = new FileWriter(currentArticle);
                        articleBW = new BufferedWriter(articleWriter);
                        count++;

                        eachArticle = new ArrayList<>();
                    }

                    if (articleBW != null && !line.contains("________")) {
                        articleBW.write(line);
                        articleBW.write("\n");

                        eachArticle.add(line);
                    }

                    line = txtFileBR.readLine();
                }

                txtFileBR.close();

                if (articleWriter != null && articleBW != null && eachArticle != null) {
                    articleBW.close();
                    articleWriter.close();

                    compilationOfArticles.add(eachArticle);
                }
            }

            splitFolder();
            splitFolder_text_only();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkStart(String line) {

        String splitLine[] = line.split(" ");
        if(splitLine[0].equals("Document") && splitLine[2].equals("of")) {
            return true;
        } else {
            return false;
        }
    }
}
