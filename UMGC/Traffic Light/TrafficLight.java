
package project3;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project 3
 * File: TrafficLight.java 
 * Author: Craig Jennings Date:
 * December 15, 2020 
 * Purpose: This class defines the properties of TrafficLight.
 */
public class TrafficLight implements Runnable {

    private final String name;
    private final long gTime;
    private final long yTime;
    private final long rTime;
    private final double xPos;
    private Color lightColor;
    private boolean stop = false;
    private boolean pause = false;
    private final Object pauseLock = new Object();

    public TrafficLight(String name, long gTime, long yTime, long rTime, double xPos) {
        this.name = name;
        this.gTime = gTime;
        this.yTime = yTime;
        this.rTime = rTime;
        this.xPos = xPos;
        this.lightColor = Color.RED;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // Sleep for specified time before changing color
                if (lightColor == Color.GREEN) {
                    Thread.sleep(gTime);
                } else if (lightColor == Color.YELLOW) {
                    Thread.sleep(yTime);
                } else if (lightColor == Color.RED) {
                    Thread.sleep(rTime);
                }
            } catch (InterruptedException exc) {
                stop = true;
            }

            synchronized (pauseLock) {
                while (pause) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TrafficLight.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            changeColor();

        }
    }

    // Change color. 
    private synchronized void changeColor() {
        if (lightColor == Color.GREEN) {
            lightColor = Color.YELLOW;
        } else if (lightColor == Color.YELLOW) {
            lightColor = Color.RED;
        } else if (lightColor == Color.RED) {
            lightColor = Color.GREEN;
        }
    }

    public void cancel() {
        stop = true;
    }
    
    public void reset() {
        lightColor = Color.RED;
        stop = false;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return lightColor;
    }

    public double getPos() {
        return xPos;
    }

    public void pause() {
        this.pause = true;
    }

    public void resume() {
        this.pause = false;

        synchronized (pauseLock) {
            pauseLock.notify();
        }
    }

}
