import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by tanzhikaivinson on 5/30/17.
 */
public class lexisnexus {


    Workbook workbook = null;
    Sheet sheet = null;
    Row row = null;
    Cell cell = null;
    BufferedReader br = null;
    String folder_name = null;
    String folder_name_mod = null;

    ArrayList<ArrayList<String>> compilationOfArticles = new ArrayList<>();

    String path;

    public lexisnexus(String inputFolderName) {

        folder_name = inputFolderName;

        if(inputFolderName.contains("/")) {
            folder_name_mod = inputFolderName.substring(inputFolderName.lastIndexOf("/") + 1);
        } else {
            folder_name_mod = folder_name;
        }

        path = System.getProperty("user.dir");

        System.out.println(">>>>>" + path);

        JFrame frame = new JFrame(folder_name_mod + "/DEXTER - LEXISNEXIS");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel space_1 = new JLabel(folder_name_mod + "running");
        frame.getContentPane().add(space_1);
        frame.setSize(350,200);
        frame.setResizable(false);
        frame.setVisible(true);

        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("LexisNexis");
        CreationHelper ch = workbook.getCreationHelper();

        CellStyle my_style = workbook.createCellStyle();
        my_style.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        my_style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

        int x = 0;

        Row row = sheet.createRow(x++);
        Cell cell = row.createCell(0);
        cell.setCellValue("ID");

        cell = row.createCell(1);
        cell.setCellValue("Source");

        cell = row.createCell(2);
        cell.setCellValue("Publisher");

        cell = row.createCell(3);
        cell.setCellValue("Headline");

        cell = row.createCell(4);
        cell.setCellValue("Potential Dateline");

        cell = row.createCell(5);
        cell.setCellValue("Dateline");

        cell = row.createCell(6);
        cell.setCellValue("Date");

        cell = row.createCell(7);
        cell.setCellValue("Wire Services");

        cell = row.createCell(8);
        cell.setCellValue("Length (TEXT ONLY)");

        cell = row.createCell(9);
        cell.setCellValue("Negative Count");

        cell = row.createCell(10);
        cell.setCellValue("Positive Count");

        cell = row.createCell(11);
        cell.setCellValue("Neg_Positive Count");

        cell = row.createCell(12);
        cell.setCellValue("Neg_Negative Count");

        cell = row.createCell(13);
        cell.setCellValue("Total_Positive");

        cell = row.createCell(14);
        cell.setCellValue("Total_Negative");

        cell = row.createCell(15);
        cell.setCellValue("Tone");

        splitText(inputFolderName);
        Double countD = compilationOfArticles.size() - 0.0;
        int folder_Count = (int)Math.ceil((countD)/1000.0);

        int count = 1;




        try {
            File folder = new File(inputFolderName);
            File[] file = folder.listFiles();

            ArrayList<String> articleCopy = new ArrayList<>();

            for (File inFiles : file) {

                if (!inFiles.getName().toLowerCase().contains(".txt")) {
                    continue;
                }

                br = new BufferedReader(new FileReader(inFiles));
                String line = br.readLine();

                while (line != null) {

                    String skinned = line.trim();
                    String cubed[] = skinned.split(" ");

                    if (cubed.length > 3 && cubed[1].equals("of") && cubed[3].equals("DOCUMENTS")) {

                        row = sheet.createRow(x++);
                        line = br.readLine();

                        cell = row.createCell(0);
                        cell.setCellValue(count++);

                        cell = row.createCell(1);
                        cell.setCellValue("LexisNexis");

                        while (line.trim().isEmpty()) {
                            line = br.readLine();
                        }

                        //Add the Publisher In
                        line = line.trim();
                        cell = row.createCell(2);
                        cell.setCellValue(line);

                        //Skip unnecessary shit
                        line = br.readLine();

                        while (line.isEmpty() || line.toCharArray()[0] == ' ') {
                            line = br.readLine();
                        }

                        cell = row.createCell(3);
                        cell.setCellValue(line);


                        while (!line.isEmpty()) {
                            line = br.readLine();
                        }
                    }

                    if (line != null && line.contains("LENGTH")) {
                        line = br.readLine();

                        while(line.isEmpty())
                            line = br.readLine();

                        if (line.contains("DATELINE")) {
                            cell = row.createCell(4);
                            line = line.replace("DATELINE: ", "");
                            cell.setCellValue(line);

                        } else {


                            while (line.isEmpty())
                                line = br.readLine();

                            line = line.trim();
                            //while (line.split(" ")[0] != null && line.split(" ")[0].contains(":")) {
                            while (line.contains(":")) {
                                //System.out.println("很好啊" + line);

                                while(!line.isEmpty())
                                    line = br.readLine();

                                while(line.isEmpty())
                                    line = br.readLine();
                            }


                            String dl;

                            //System.out.println("贱人就是矫情 " + count + " >> " + line);

                            String cube[] = line.split(" ");
                            char minced[] = line.toCharArray();

                            //System.out.println("贱人就 " + count + " >> " + cube[0]);

                            //Error 1: The length of the line is less than 6
                            // 6 is usually the maximum
                            if (cube.length < 6) {
                                dl = " !Error 1: " + line;

                                cell = row.createCell(4);
                                cell.setCellValue(dl);
                            } else {

                                if (line.length() > 0) {

                                    dl = datelineExtract(line);

                                    if (dl != null) {

                                        String paragraph = "";

                                        while (line != null && !line.isEmpty()) {
                                            paragraph = paragraph + line;
                                            line = br.readLine();
                                        }

                                        int dash_count = countDash(paragraph);

                                        if (dash_count > 1 && dash_count%2 == 0) {
                                            dl = " !Error 2: " + dl;
                                        }

                                        cell = row.createCell(4);
                                        cell.setCellValue(dl);

                                        if(!dl.contains(" !Error")) {
                                            cell = row.createCell(5);
                                            cell.setCellValue(dl);
                                        }

                                    }

                                }
                            }
                            line = br.readLine();
                        }
                    }

                    //System.out.println("贱人就是矫情 " + line);

                    if (line != null && line.contains("LOAD-DATE: ")) {
                        cell = row.createCell(6);
                        line = line.replace("LOAD-DATE: ", "");
                        cell.setCellValue(getDate(line));
                    }

                    if (line != null && line.contains("BYLINE")) {
                        cell = row.createCell(7);
                        if (line.toLowerCase().contains("associated press") || line.toLowerCase().contains("reuters") || line.toLowerCase().contains("agence france-presse")) {
                            cell.setCellValue(1);
                        } else {
                            cell.setCellValue(0);
                        }
                    }
                    articleCopy.add(line);
                    line = br.readLine();
                }

                br.close();
            }

            articleCopy.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println(">>");

        //Lexicoder Part
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
                System.out.println(new String(b));  //
                String wordCount = new String(b);
                String wcArray[] = wordCount.split("\n");
                InputStream err = proc.getErrorStream();    //
                err.read(b,0,b.length); //
                System.err.println(new String(b));  //

                command = "java -jar " + path + "/Lexicoder_0.jar dc dat=" + folder_name + "/" + folder_name_mod + "_Split/folder_split_text_only_" + a + " md=" + path + "/LSD2015.lc3";
                proc = Runtime.getRuntime().exec(command);
                proc.waitFor();

                in = proc.getInputStream();
                b = new byte[in.available()];
                in.read(b, 0, b.length);
                System.out.println(new String(b));
                String resultW = new String(b);
                String result[] = resultW.split("\\n");
                err = proc.getErrorStream();    //
                err.read(b,0,b.length); //
                System.err.println(new String(b));  //

                command = "java -jar " + path + "/Lexicoder_0.jar dc dat=" + folder_name + "/" + folder_name_mod + "_Split/folder_split_text_only_" + a + " md=" + path + "/LSD2015_NEG.lc3";
                proc = Runtime.getRuntime().exec(command);
                proc.waitFor();
                in = proc.getInputStream();

                b = new byte[in.available()];
                System.out.println(new String(b));
                in.read(b, 0, b.length);
                resultW = new String(b);
                String result2[] = resultW.split("\\n");
                err = proc.getErrorStream();    //
                err.read(b,0,b.length); //
                System.err.println(new String(b));  //


                for (int i = 1; i < result.length; i++) {

                    String avatar[] = wcArray[i].split("\t");
                    String james[] = result[i].split("\t");
                    String mcavoy[] = result2[i].split("\t");

                    String shit = james[0];
                    int first = shit.indexOf('_');
                    int second = shit.indexOf('.');
                    String fecal = shit.substring(first + 1, second);

                    Row low = sheet.getRow(Integer.parseInt(fecal));

                    cell = low.createCell(8);
                    Double wc = Double.parseDouble(avatar[1]);
                    System.out.println("wc for " + i + " is " + wc);
                    cell.setCellValue(wc);

                    Cell neg = low.createCell(9);
                    Double neg_I = Double.parseDouble(james[1]);
                    neg.setCellValue(neg_I);

                    Cell pos = low.createCell(10);
                    Double pos_I = Double.parseDouble(james[2]);
                    pos.setCellValue(pos_I);

                    Cell neg_positive = low.createCell(11);
                    Double np_I = Double.parseDouble(mcavoy[1]);
                    neg_positive.setCellValue(np_I);

                    Cell neg_negative = low.createCell(12);
                    Double nn_I = Double.parseDouble(mcavoy[2]);
                    neg_negative.setCellValue(nn_I);

                    Cell tot_pos = low.createCell(13);
                    Double totP_I = pos_I - np_I + nn_I;
                    tot_pos.setCellValue(totP_I);

                    Cell tot_neg = low.createCell(14);
                    Double totN_I = neg_I - nn_I + np_I;
                    tot_neg.setCellValue(totN_I);

                    Cell tone = low.createCell(15);
                    Double tone_I = (totP_I - totN_I) / wc;
                    tone.setCellValue(tone_I);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int probrem[] = new int[x];
        for (int i = 1; i < x; i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow.getCell(5) == null || currentRow.getCell(5).getStringCellValue().isEmpty()) {
                //System.out.println(">> " + i);
                currentRow.createCell(5).setCellValue(-99);
                probrem[i - 1] = 1;
            }

            if (currentRow.getCell(7) == null) {
                currentRow.createCell(7).setCellValue(0);
            }
        }

            /*************** copy problem files ***************/

            //System.out.println(">>>" + allText.size());
            //System.out.println(">>>>>" + x);

        try {
            FileWriter writer = new FileWriter(folder_name + "/" + folder_name_mod + "_Problem.txt");
            BufferedWriter bw = new BufferedWriter(writer);

            //create problem file
            for (int i = 0; i < compilationOfArticles.size(); i++) {
                if(probrem[i] == 1) {
                    ArrayList<String> copy = compilationOfArticles.get(i);

                    bw.write("\n-------- -------- -------- -------- ID: " + (i + 1) + " -------- -------- -------- --------\n\n");

                    for (int j = 0; j < copy.size(); j++) {
                        bw.write("ID: " + (i + 1) + " -- " + copy.get(j));
                        bw.write("\n");
                    }
                }
            }

            bw.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream output = new FileOutputStream(folder_name + "/" + folder_name_mod + ".csv");
            workbook.write(output);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.dispose();
    }

    /************************************************************************************/
    public int countDash(String line) {
        String cubed[] = line.split(" ");
        int count = 0;

        for(int i = 0; i < cubed.length; i++) {
            if(cubed[i].equals("-")) count++;
        }

        return count;
    }

    public static String datelineExtract(String line) {

        if(line.isEmpty()) {
           return null;
        }

        String cubed[] = line.split(" ");

        String patty = "";
        for(int i = 0; i < cubed.length; i++) {
            if (cubed[i].equals("-") || cubed[i].equals("--")) {
                if (i < 6) {
                    return patty;
                } else {
                    return " !Error 3: " + patty;
                }
            }
            patty = patty + cubed[i] + " ";
        }

        return " !Error 0: " + line;
    }


    public String getDate(String string) {

        String year = string.substring(string.length() - 4);
        String monthday = string.substring(0,string.length() - 6);
        String day = String.format("%02d",Integer.parseInt(monthday.replaceAll("\\D+","")));
        String month = getMonth(monthday.replaceAll("[^A-Za-z]+",""));
        //System.out.println(day + "/" + month + "/" + year);
        return month + "/" + day + "/" + year;
    }

    public String getMonth(String string) {

        String monS = string.toLowerCase();

        if(monS.equals("january")) {
            return "01";
        } else if (monS.equals("february")) {
            return "02";
        }else if (monS.equals("march")) {
            return "03";
        }else if (monS.equals("april")) {
            return "04";
        }else if (monS.equals("may")) {
            return "05";
        }else if (monS.equals("june")) {
            return "06";
        }else if (monS.equals("july")) {
            return "07";
        }else if (monS.equals("august")) {
            return "08";
        }else if (monS.equals("september")) {
            return "09";
        }else if (monS.equals("october")) {
            return "10";
        }else if (monS.equals("november")) {
            return "11";
        }else if (monS.equals("december")) {
            return "12";
        }

        return null;
    }

    /************************************************************************************/
    public void splitFolder_text_only() {

        Double countD = compilationOfArticles.size() - 0.0;
        int FolderCount = (int)Math.ceil((countD)/1000.0);

        for (int i = 1; i <= FolderCount; i++) {

            String folder_text_name = folder_name + "/" + folder_name_mod + "_Split/" +  "folder_split_text_only_" + i;
            File folder_text = new File(folder_text_name);
            if(!folder_text.exists()) {
                folder_text.mkdir();
            }
        }

        for (int i = 0; i < compilationOfArticles.size(); i++) {

            int folder = (int)Math.ceil((i + 1.0)/1000.0);
            String fileName = folder_name + "/" + folder_name_mod + "_Split/" +  "folder_split_text_only_" + folder + "/article_" + (i+1) + ".txt";

            FileWriter writer = null;
            BufferedWriter bw = null;
            File file = new File(fileName);

            for(int j = 0; j < compilationOfArticles.get(i).size(); j++) {

                if (compilationOfArticles.get(i).get(j).contains("LENGTH:")) {

                    try {
                        String line = compilationOfArticles.get(i).get(++j);
                        writer = new FileWriter(file);
                        bw = new BufferedWriter(writer);
                        while(!line.contains("LOAD-DATE:")) {
                            bw.write(line);
                            bw.write("\n");
                            line = compilationOfArticles.get(i).get(j++);
                        }

                        bw.close();
                        writer.close();
                    } catch(Exception e) {
                            e.printStackTrace();
                    }
                    continue;
                }
            }
        }

    }

    public int splitText(String inputFile) {

        try {
            int count = 1;
            String currDir = folder_name + "/" + folder_name_mod + "_Split";
            File theDig = new File(currDir);
            if (!theDig.exists()) {
                try {
                    theDig.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            File folder = new File(inputFile);
            File[] file = folder.listFiles();

            for (File thisFile : file) {
                if (!thisFile.toString().toLowerCase().contains(".txt")) {
                    continue;
                }

                if (thisFile.toString().contains("._")) {
                    continue;
                }

                if (thisFile.isDirectory()) {
                    continue;
                }

                if (thisFile.toString().contains(".DS_Store")) {
                    continue;
                }

                if (thisFile.toString().toLowerCase().contains("problem")) {
                    continue;
                }

                BufferedReader bbr = new BufferedReader(new FileReader(thisFile));
                String line = bbr.readLine();

                FileWriter writer = null;
                BufferedWriter bw = null;

                while (line != null) {

                    String skinned = line.trim();
                    String cubed[] = skinned.split(" ");

                    if (cubed.length > 3 && cubed[1].equals("of") && cubed[3].equals("DOCUMENTS")) {
                        if (bw != null && writer != null) {
                            bw.close();
                            writer.close();
                        }

                        File currentFile = new File(currDir + "/article_" + count + ".txt");
                        writer = new FileWriter(currentFile);
                        bw = new BufferedWriter(writer);

                        count++;
                    }

                    if (bw != null) {
                        bw.write(line);
                        bw.write("\n");
                    }

                    line = bbr.readLine();
                }

                if(bw != null) {
                    bw.close();
                }

                if(writer != null) {
                    writer.close();
                }

                if(bbr != null) {
                    bbr.close();
                }

            }


            for (int i = 1; i < count; i++) {

                File eachFile = new File(currDir + "/article_" + i + ".txt");
                ArrayList<String> eachArticle = new ArrayList<>();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(eachFile));
                    String line = br.readLine();
                    while(line != null) {
                        eachArticle.add(line);
                        line = br.readLine();
                    }

                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                compilationOfArticles.add(eachArticle);

            }

            splitFolder_text_only();
            return (int)Math.ceil((count - 1.0)/1000.0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
    public int splitFolder(int count) {

        Double countD = count - 1.0;
        int FolderCount = (int)Math.ceil((countD)/1000.0);

        String currDir = folder_name + "/" + folder_name_mod + "_Split";

        for(int i = 1; i <= FolderCount; i++) {
            String destinedDir = folder_name + "/" + folder_name_mod + "_Split" + "/" + "folder_split_" + i;
            //System.out.println(destinedDir);

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
                    String currFilePath = currDir + "/" + fileName + ".txt";
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
                    String currFilePath = currDir + "/" + fileName + ".txt";
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

        return FolderCount;
    }*/
}
