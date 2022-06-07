/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirusSniper;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 *
 * @author Andrew
 */
public class StreamReader extends Thread{

    private InputStream is;
    private StringWriter sw= new StringWriter();

    public StreamReader(InputStream is) {
        this.is = is;
    }

    public void run() {
        try {
            int c;
            while ((c = is.read()) != -1)
                sw.write(c);
        }
        catch (IOException e) {
        }
    }

    public String getResult() {
        return sw.toString();
    }

}
