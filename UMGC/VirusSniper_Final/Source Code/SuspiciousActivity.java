/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirusSniper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author craig
 */
public class SuspiciousActivity {

    private final ArrayList<String> reportStrings = new ArrayList();
    private final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
    private final boolean psexec;
    private final boolean newUser;

    public SuspiciousActivity() throws IOException {

        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate s = LocalDate.now().minusDays(Settings.getScanTime());
        LocalDate e = LocalDate.now().plusDays(1);
        Date start = Date.from(s.atStartOfDay(defaultZoneId).toInstant());
        Date end = Date.from(e.atStartOfDay(defaultZoneId).toInstant());

        psexec = psexecCheck(formatter.format(start), formatter.format(end));
        newUser = newUsers(formatter.format(start), formatter.format(end));

    }

    private boolean psexecCheck(String start, String end) throws IOException {
        // Run powershell command to find if psexesvc was installed as a service
        String command = "powershell.exe Get-WinEvent -ListLog System | "
                + "foreach { Get-WinEvent @{LogName=$_.logname;ID=7045;"
                + "StartTime='" + start + "';"
                + "EndTime='" + end + "'} } |"
                + " where Message -match PSEXESVC | Format-Table TimeCreated,Message -wrap";
        // Execute the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        // Get the results
        powerShellProcess.getOutputStream().close();
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        String line;
        // Add event log entries to report
        if ((line = stdout.readLine()) != null) {
            reportStrings.add("PSExec Activity Found:");
            while ((line = stdout.readLine()) != null) {
                reportStrings.add(line);
            }
            stdout.close();
            return true;
        } else {
            stdout.close();
            return false;
        }
    }

    private boolean newUsers(String start, String end) throws IOException {
        // Run powershell command to find if psexesvc was installed as a service
        String command = "powershell.exe Get-WinEvent -FilterHashTable "
                + "@{LogName='Security';ID=4720;"
                + "StartTime='" + start + "';"
                + "EndTime='" + end + "'} | Format-Table TimeCreated,Message -wrap";
        // Execute the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        // Get the results
        powerShellProcess.getOutputStream().close();
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        String line;
        // Add event log entries to report
        if ((line = stdout.readLine()) != null) {
            reportStrings.add("New Users Recently Added:\n");
            while ((line = stdout.readLine()) != null) {
                reportStrings.add(line);
            }
            stdout.close();
            return true;
        } else {
            stdout.close();
            return false;
        }
    }

    public boolean getPsexec() {
        return this.psexec;
    }

    public boolean getNewUsers() {
        return this.newUser;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String str : reportStrings) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }
}
