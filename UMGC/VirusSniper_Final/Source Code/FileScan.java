/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirusSniper;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Markell
 */

public class FileScan {

    //To store virus definition md5 hashes and their respective names and types
    public static ArrayList<String> virusDefinitions = new ArrayList<String>();
    public static ArrayList<String> virusNames = new ArrayList<String>();;
    public static ArrayList<String> virusTypes = new ArrayList<String>();;

    public void scanFile() {      
        try {
            System.out.println("Select the file to scan for virus ");
            
            //File chooser select the file to scan
            File file = fileChooser();
            System.out.println(file.toString());

            VirusSniperAnalyzer logic = new VirusSniperAnalyzer();
            
            //Generating md5 checksum value of the given file
            String fileChecksum = logic.md5Generator(file.toString());
            
            //Reading the virus definitions and storing them in a array list
            FileHandler fh = new FileHandler();
            boolean ok = fh.readVirusDefinition();

            if(ok){
                //Analyzing the result of the scan
                int index = logic.analyze(fileChecksum, virusDefinitions);

                if(index==-1){
                    System.out.println("No Virus has been detected !!");
                    JOptionPane.showMessageDialog(null, file.getName()+" file does not contain a virus.\nFile MD5: "+fileChecksum+"\nis not found in virus definitions", "No Virus !!",JOptionPane.INFORMATION_MESSAGE);
               }else{    
                    System.out.println("Virus has been Detected !!");
                    JOptionPane.showMessageDialog(null, 
                            file.getName()+" file contains a virus.\n"
                                    + "File MD5: "+fileChecksum+"\nis found in virus definitions\n"
                                    + "Virus Name: "+virusNames.get(index)+"\nVirus Type: "+virusTypes.get(index), 
                            "Virus Detected !!",JOptionPane.WARNING_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null, "virusDef.txt not found. Create virusDef.txt file with your virus definitions in this folder.", null, JOptionPane.WARNING_MESSAGE);
            }
         
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
  
	//Use File chooser to select file
    public static File fileChooser(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select the file to scan");

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
          return chooser.getSelectedFile();
        } else {
          return null;
        }    
    }
    
}