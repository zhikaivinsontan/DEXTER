/**
 * Created by tanzhikaivinson on 5/23/17.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class converter /*implements ActionListener*/ {

    String folderName_1 = null;
    String folderName_2 = null;

    public String JFileChooser() {

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = jFileChooser.showOpenDialog(new JFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            return selectedFile.toString();
        }

        return null;
    }

    public void go() {
        JFrame frame = new JFrame("DEXTER");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Container container = frame.getContentPane();
        container.setLayout(new FlowLayout());

        JLabel space_1 = new JLabel("                                                                 ");
        container.add(space_1);
        /********************************************************************************/
        JLabel space_folder_1 = new JLabel("                          Folder 1                          ");
        JTextField textfield_1 = new JTextField(10);
        JTextField jtf10 = new JTextField(10);
        JLabel input_directory_1 = new JLabel("Input Directory");
        JButton fileChooserButton_1 = new JButton("Open");


        container.add(space_folder_1);
        container.add(input_directory_1);
        container.add(textfield_1);
        container.add(fileChooserButton_1);

        //check box
        JLabel sourceType_1 = new JLabel("Source Type");
        String source [] = {"LexisNexis", "ProQuest"};
        JComboBox cb_1 = new JComboBox(source);
        JLabel pad_1 = new JLabel("                 ");
        container.add(sourceType_1);
        container.add(cb_1);
        container.add(pad_1);

        JLabel space_below_1 = new JLabel("                                                                 ");
        container.add(space_below_1);
        /********************************************************************************/
        JLabel space_dash_2 = new JLabel("___________________________________________________________________");
        JLabel space_top_2 = new JLabel("                                                                 ");
        JLabel space_folder_2 = new JLabel("                          Folder 2                           ");

        container.add(space_dash_2);
        container.add(space_top_2);
        container.add(space_folder_2);
        JLabel input_directory_2 = new JLabel("Input Directory");
        JTextField textfield_2 = new JTextField(10);
        JButton fileChooserButton_2 = new JButton("Open");

        container.add(input_directory_2);
        container.add(textfield_2);
        container.add(fileChooserButton_2);

        JLabel sourceType_2 = new JLabel("Source Type");
        JComboBox cb_2 = new JComboBox(source);
        JLabel pad_2 = new JLabel("                 ");
        JLabel space_dash_3 = new JLabel("___________________________________________________________________");
        container.add(sourceType_2);
        container.add(cb_2);
        container.add(pad_2);

        JLabel space_below_2 = new JLabel("                                                                 ");
        container.add(space_below_2);
        container.add(space_dash_3);

        /********************************************************************************/
        JLabel space_3 = new JLabel("                                                                 ");
        JButton enterButt = new JButton("ENTER");
        container.add(space_3);
        container.add(enterButt);

        JLabel space_2 = new JLabel("                                                                   ");
        container.add(space_2);
        container.add(enterButt);

        fileChooserButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                folderName_1 = JFileChooser();
                textfield_1.setText(folderName_1);
            }
        });

        fileChooserButton_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                folderName_2 = JFileChooser();
                textfield_2.setText(folderName_2);
            }
        });

        enterButt.addActionListener(new ActionListener() {
           @Override
            public void actionPerformed(ActionEvent e) {

               String inputFileString_1 = textfield_1.getText();
               String sourceType_1 = source[cb_1.getSelectedIndex()];

               if (!inputFileString_1.isEmpty()) {

                   File file1 = new File(inputFileString_1);

                   if(!file1.exists()) {

                       JFrame errorFrame = new JFrame();
                       errorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                       if(textfield_2.getText().isEmpty()) {
                           JOptionPane.showMessageDialog(errorFrame, "Folder 1 Does Not Exist", "dialog", JOptionPane.ERROR_MESSAGE);
                       } else {
                           JOptionPane.showMessageDialog(errorFrame, "Folder 1 Does Not Exist, Proceeding to Folder 2", "dialog", JOptionPane.ERROR_MESSAGE);
                       }
                   } else {
                       if (sourceType_1.equals("LexisNexis")) {
                           lexisnexus ln = new lexisnexus(inputFileString_1);
                       } else if (sourceType_1.equals("ProQuest")) {
                           System.out.println("NUMBER 1 PROQUEST SELECTED");
                           proquest pq = new proquest(inputFileString_1);
                       }
                   }
               } else {

                   JFrame errorFrame = new JFrame();
                   errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   JOptionPane.showMessageDialog(errorFrame,"Invalid file name","dialog",JOptionPane.ERROR_MESSAGE);

               }

               String inputFileString_2 = textfield_2.getText();
               String sourceType_2 = source[cb_2.getSelectedIndex()];

               if (!inputFileString_2.isEmpty()) {

                   File file2 = new File(inputFileString_2);
                   if (!file2.exists()) {

                       JFrame errorFrame = new JFrame();
                       errorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                       JOptionPane.showMessageDialog(errorFrame, "Folder 2 Does Not Exist", "dialog", JOptionPane.ERROR_MESSAGE);

                   } else {

                       if (sourceType_2.equals("LexisNexis")) {
                           lexisnexus ln = new lexisnexus(inputFileString_2);
                       } else if (sourceType_2.equals("ProQuest")) {
                           proquest pq = new proquest(inputFileString_2);
                       }
                   }
               }

               System.exit(0);
               frame.dispose();

           }
        });

        frame.setSize(350,450);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.YELLOW);

        System.out.println("Die-ded");
    }

    public static void main(String[] args) {

        converter c = new converter();
        c.go();

    }
}
