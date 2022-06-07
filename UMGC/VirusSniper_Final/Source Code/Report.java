package VirusSniper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    String file_name;
    Date start;

    File malware;
    String details;
    ReportType type;

    public Report(ReportType type_, String details_, File malware_){
        details = details_;
        malware = malware_;
        type = type_;
        System.out.println("Starting report...");

        start = new Date(System.currentTimeMillis());
        file_name = "reports/"+typeToString(type)+"_"+formatter.format(start)+".txt";
        File my_report = new File(file_name);
        if(my_report.exists()){
            // try again
        }
        try {
            my_report.createNewFile();
            FileWriter writer = new FileWriter(my_report);
            writer.write("VIRUS SNIPER REPORT ("+formatter.format(start)+"):\n");
            if(malware != null) {
                writer.write("MALWARE FILE :"+malware.getAbsolutePath()+"\n");
            }
            writer.write(details);
            writer.close();
            System.out.println("REPORT SAVED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String typeToString(ReportType rt){
        if(rt == ReportType.MSFVENOM)
            return "MSFVenom";
        if(rt == ReportType.REGISTRY)
            return "REGISTRY";
        if(rt == ReportType.SUSPICIOUSACTIVITY)
            return "SuspiciousActivity";
        if(rt == ReportType.FILESCAN)
            return "FileScan";
        return null;
    }

    public enum ReportType{
        MSFVENOM,
        REGISTRY,
        SUSPICIOUSACTIVITY,
        FILESCAN
    }

}