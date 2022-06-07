
package project3;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Project 3
 * File: Project3GUI.java 
 * Author: Craig Jennings Date:
 * December 15, 2020 
 * Purpose: This application simulates a real time traffic display.
 */
public class Project3GUI extends JFrame implements ItemListener {

    private static final List<TrafficLight> lights = new CopyOnWriteArrayList<>();
    private static final List<Car> cars = new CopyOnWriteArrayList<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private JButton addRemoveButton, continueButton, pauseButton, startButton, stopButton;
    private JComboBox<String> addRemoveCB;
    private JPanel addRemovePanel, displayPanel;
    private JLabel curTimeLabel;
    private JTextField curTimeTF, addCarNameTF, addCarPosTF, addCarSpeedTF, delCarNameTF,
            addLightNameTF, addLightGTimeTF, addLightYTimeTF, addLightRTimeTF, addLightPosTF,
            delLightNameTF;
    private JScrollPane statsScrollPane;
    private JTextArea statsTextArea;
    private ThreadGroup lightThreads = new ThreadGroup("lightThreads");

    public Project3GUI() {
        initComponents();
    }

    private void initComponents() {

        curTimeTF = new JTextField();
        curTimeLabel = new JLabel();
        startButton = new JButton();
        pauseButton = new JButton();
        continueButton = new JButton();
        stopButton = new JButton();
        displayPanel = new DisplayPanel();
        addRemoveCB = new JComboBox<>();
        addRemovePanel = new JPanel();
        addRemoveButton = new JButton();
        statsScrollPane = new JScrollPane();
        statsTextArea = new JTextArea();

        this.setTitle("Traffic Simulator Application");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        statsTextArea.setEditable(false);

        curTimeTF.setEditable(false);
        setTimestamp();

        curTimeLabel.setText("Current Time");

        startButton.setText("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        continueButton.setText("Continue");
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });

        stopButton.setText("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        displayPanel.setBorder(BorderFactory.createTitledBorder("Real-time Display"));

        GroupLayout displayPanelLayout = new GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
                displayPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
                displayPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 133, Short.MAX_VALUE)
        );

        addRemoveCB.setModel(new DefaultComboBoxModel<>(new String[]{"Choose Item...", "Add Car", "Remove Car", "Add Intersection", "Remove Intersection"}));
        addRemoveCB.addItemListener(this);

        addRemovePanel.setBorder(BorderFactory.createTitledBorder("Add/Remove Item"));

        GroupLayout addRemovePanelLayout = new GroupLayout(addRemovePanel);
        addRemovePanel.setLayout(new CardLayout());
        // Make cards
        JPanel card1 = chooseItem();
        JPanel card2 = addCar();
        JPanel card3 = removeCar();
        JPanel card4 = addIntersection();
        JPanel card5 = removeIntersection();

        addRemovePanel.add(card1, "Choose Item...");
        addRemovePanel.add(card2, "Add Car");
        addRemovePanel.add(card3, "Remove Car");
        addRemovePanel.add(card4, "Add Intersection");
        addRemovePanel.add(card5, "Remove Intersection");

        addRemoveButton.setText("Add/Remove");
        addRemoveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addRemoveButtonActionPerformed(evt);
            }
        });

        statsScrollPane.setBorder(BorderFactory.createTitledBorder("Car Statistics"));
        statsScrollPane.setViewportView(statsTextArea);
        getStats();

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(displayPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(3, 3, 3)
                                                                .addComponent(addRemoveCB, 0, 0, Short.MAX_VALUE))
                                                        .addComponent(addRemoveButton, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(addRemovePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(statsScrollPane, GroupLayout.PREFERRED_SIZE, 333, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(startButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(continueButton, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(stopButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(pauseButton, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(curTimeLabel)
                                                .addGap(18, 18, 18)
                                                .addComponent(curTimeTF, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(curTimeTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(curTimeLabel))
                                .addGap(18, 18, 18)
                                .addComponent(displayPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(addRemoveCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(addRemoveButton))
                                                        .addComponent(addRemovePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(statsScrollPane))
                                                .addGap(45, 45, 45))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(46, 46, 46)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(startButton)
                                                        .addComponent(stopButton))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(continueButton)
                                                        .addComponent(pauseButton))
                                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout) (addRemovePanel.getLayout());
        cl.show(addRemovePanel, (String) evt.getItem());
    }

    // panel cards
    private JPanel chooseItem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Choose an item from the menu"));
        panel.add(Box.createRigidArea(new Dimension(0, 200)));

        return panel;
    }

    private JPanel addCar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        addCarNameTF = new JTextField();
        addCarSpeedTF = new JTextField();
        addCarPosTF = new JTextField();

        panel.add(new JLabel("Enter the car's name:"));
        panel.add(addCarNameTF);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Enter the car's speed (km/h):"));
        panel.add(addCarSpeedTF);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Enter the car's starting position in meters from 0 (<10,000):"));
        panel.add(addCarPosTF);

        return panel;
    }

    private JPanel removeCar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        delCarNameTF = new JTextField();

        panel.add(new JLabel("Enter the car's name you wish to delete:"));
        panel.add(delCarNameTF);

        return panel;
    }

    private JPanel addIntersection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        addLightNameTF = new JTextField();
        addLightGTimeTF = new JTextField();
        addLightYTimeTF = new JTextField();
        addLightRTimeTF = new JTextField();
        addLightPosTF = new JTextField();

        panel.add(new JLabel("Enter the light's name:"));
        panel.add(addLightNameTF);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Enter how long the light will stay green in seconds:"));
        panel.add(addLightGTimeTF);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Enter how long the light will stay yellow in seconds:"));
        panel.add(addLightYTimeTF);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Enter how long the light will stay red in seconds:"));
        panel.add(addLightRTimeTF);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Enter the light's position in meters away from 0 (<10,000):"));
        panel.add(addLightPosTF);

        return panel;
    }

    private JPanel removeIntersection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        delLightNameTF = new JTextField();

        panel.add(new JLabel("Enter the light's name you wish to delete:"));
        panel.add(delLightNameTF);

        return panel;
    }

    // Displays current timestamp
    private void setTimestamp() {

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Date date = new Date();
                        curTimeTF.setText(sdf.format((new Timestamp(date.getTime()))));
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Project3GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        t.start();
    }

    // Gets statistics about cars and displays them
    private void getStats() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    statsTextArea.setText("");
                    for (Car car : cars) {
                        statsTextArea.append("Name: " + car.getName() + "\n");
                        statsTextArea.append("Position (meters): " + Double.toString(car.getPos() * 10) + "\n");
                        statsTextArea.append("Speed (km/h): " + Double.toString(car.getSpeed()) + "\n\n");
                    }
                    statsTextArea.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Project3GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        t.start();
    }

    private void startButtonActionPerformed(ActionEvent evt) {
        for (TrafficLight light : lights) {
            light.reset();
            Thread t = new Thread(lightThreads, light);
            t.start();
        }

        for (Car car : cars) {
            car.reset();
            Thread t = new Thread(car);
            t.start();
            (new CheckLight(car)).execute();
        }

    }

    private void pauseButtonActionPerformed(ActionEvent evt) {
        for (TrafficLight light : lights) {
            light.pause();
        }

        for (Car car : cars) {
            car.pause();
        }
    }

    private void continueButtonActionPerformed(ActionEvent evt) {
        for (TrafficLight light : lights) {
            light.resume();
        }

        for (Car car : cars) {
            car.resume();
        }
    }

    private void stopButtonActionPerformed(ActionEvent evt) {
        lightThreads.interrupt();

        for (Car car : cars) {
            car.cancel();
        }

        JOptionPane.showMessageDialog(null, "Simulation Stopped", "", JOptionPane.PLAIN_MESSAGE);
    }

    private void addRemoveButtonActionPerformed(ActionEvent evt) {
        String choice = String.valueOf(addRemoveCB.getSelectedItem());

        try {
            switch (choice) {
                case "Add Car":
                    String addCarName = addCarNameTF.getText();
                    double addCarSpeed = Double.parseDouble(addCarSpeedTF.getText());
                    double addCarPos = Double.parseDouble(addCarPosTF.getText()) / 10;
                    cars.add(new Car(addCarName, addCarPos, addCarSpeed));
                    break;
                case "Remove Car":
                    String delCarName = delCarNameTF.getText();
                    for (Car car : cars) {
                        if (car.getName() == null ? delCarName == null : car.getName().equals(delCarName)) {
                            cars.remove(car);
                        }
                    }
                    break;
                case "Add Intersection":
                    String addLightName = addLightNameTF.getText();
                    // Multiply by 1000 to convert seconds to milliseconds
                    long addLightGTime = Long.parseLong(addLightGTimeTF.getText()) * 1000;
                    long addLightYTime = Long.parseLong(addLightYTimeTF.getText()) * 1000;
                    long addLightRTime = Long.parseLong(addLightRTimeTF.getText()) * 1000;
                    // Divide by 10 to convert to scale
                    double addLightPos = Double.parseDouble(addLightPosTF.getText()) / 10;
                    lights.add(new TrafficLight(addLightName, addLightGTime, addLightYTime, addLightRTime, addLightPos));
                    break;
                case "Remove Intersection":
                    String delLightName = delLightNameTF.getText();
                    for (TrafficLight light : lights) {
                        if (light.getName() == null ? delLightName == null : light.getName().equals(delLightName)) {
                            lights.remove(light);
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "All parameters except Name must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        // Add default cars and lights
        lights.add(new TrafficLight("Light 1", 4000, 1000, 6000, 250));
        lights.add(new TrafficLight("Light 2", 2000, 500, 5000, 350));
        lights.add(new TrafficLight("Light 3", 2000, 1000, 6000, 450));
        cars.add(new Car("Car 1", 230, 60));
        cars.add(new Car("Car 2", 290, 110));
        cars.add(new Car("Car 3", 330, 75));

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Project3GUI().setVisible(true);
            }
        });

    }

    private class DisplayPanel extends JPanel implements ActionListener {

        Timer timer = new Timer(10, this);

        public DisplayPanel() {
            timer.start();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            Rectangle2D.Double bar = new Rectangle2D.Double(0, 75, 1000, 10);
            g2.setPaint(Color.BLUE);
            g2.fill(bar);

            lights.forEach((light) -> {
                JLabel ll = new JLabel(light.getName());
                ll.setBounds((int) light.getPos(), 10, 50, 50);
                this.add(ll);
                Rectangle2D.Double marker = new Rectangle2D.Double(light.getPos(), 70, 5, 20);
                g2.setPaint(Color.BLACK);
                g2.fill(marker);
                Ellipse2D.Double tl = new Ellipse2D.Double(light.getPos(), 50, 15, 15);
                g2.setPaint(light.getColor());
                g2.fill(tl);
            });

            cars.forEach((car) -> {
                JLabel cl = new JLabel(car.getName());
                cl.setBounds((int) car.getPos(), 85, 50, 50);
                this.add(cl);
                Rectangle2D.Double marker = new Rectangle2D.Double(car.getPos(), 75, 5, 25);
                g2.setPaint(Color.ORANGE);
                g2.fill(marker);
            });

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == timer) {
                this.removeAll();
                this.repaint();
            }
        }
    }

    private class CheckLight extends SwingWorker<String, Object> {

        private final Car car;

        public CheckLight(Car car) {
            this.car = car;
        }

        @Override
        protected String doInBackground() throws Exception {
            while (true) {
                if (car.getPos() > 1000) {
                    car.pause();
                }
                for (TrafficLight light : lights) {
                    if (Math.floor(car.getPos()) == Math.floor(light.getPos()) && light.getColor() == Color.RED) {
                        while (light.getColor() == Color.RED) {
                            car.pause();
                        }
                        car.resume();
                    }
                }
            }
        }
    }
}
