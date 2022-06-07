/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirusSniper;

/**
 *
 * @author Andrew
 */

public class Registry {

    // This method invokes the runtime.exec feature of Java to run the reg query command
    // It returns a string representation of the result of the query
    public String readRegistry(String location){
        try {
            /* Java utilizes the runtime to run the reg query command with the provided location
             * The location variable needs to be a String value with "" around it in order to
             * account for spaces in the path
             */
            Process process = Runtime.getRuntime().exec(("REG QUERY "+location));


            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String output = reader.getResult();

            // This allows you to remove spaces and put each entry in a separte location in a String array
            // This can be used in the future to remove unwanted items from the reg query results
            //String[] parsed = output.split("    ");
            //return parsed[parsed.length-1];

            return output;
        }
        catch (Exception e) {
            System.out.println("Exeception "+e);
            return null;
        }

    }
}