
package project3;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Project 3
 * File: Car.java 
 * Author: Craig Jennings Date:
 * December 15, 2020 
 * Purpose: This class defines the properties of Car.
 */
public class Car implements Runnable {

    private double xPos;
    private final double xOrigin;
    private double curSpeed = 0;
    private final double origSpeed;
    private final String name;
    private boolean stop = false;
    private final StopWatch timer = new StopWatch();
    private final Object pauseLock = new Object();
    private boolean pause = false;

    public Car(String name, double xOrigin, double origSpeed) {
        this.name = name;
        this.xOrigin = xOrigin;
        xPos = xOrigin;
        this.origSpeed = origSpeed;
    }

    @Override
    public void run() {
        if (!timer.isStarted()) {
            timer.start();
        }
        while (!stop) {
            synchronized (pauseLock) {
                while (pause) {
                    try {
                        curSpeed = 0;
                        pauseLock.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TrafficLight.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            travel();
        }
    }

    // Calculates position in meters.
    private synchronized void travel() {
        curSpeed = origSpeed;
        xPos = xOrigin + ((100 * curSpeed * timer.getTime(TimeUnit.MILLISECONDS)) / 3600000);
    }

    public void reset() {      
        stop = false;
    }
    
    public void cancel() {       
        if (!timer.isStopped()) {
            timer.stop();
            timer.reset();
        }
        stop = true;
    }

    public double getPos() {
        return xPos;
    }

    public String getName() {
        return name;
    }

    public double getSpeed() {
        return curSpeed;
    }

    public void pause() {
        if (timer.isStarted() && !timer.isSuspended()) {
            timer.suspend();
        }
        pause = true;
    }

    public void resume() {
        if (timer.isSuspended()) {
            timer.resume();
        }
        pause = false;
        synchronized (pauseLock) {
            pauseLock.notify();
        }
    }

}
