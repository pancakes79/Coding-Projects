/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirusSniper;

/**
 *
 * @author craig
 */
public class Settings {
    
    private static int scanTime;
    
    public static void setScanTime(Integer st) {
        scanTime = st;
    }
    
    public static int getScanTime() {
        return scanTime;
    }
    
}
