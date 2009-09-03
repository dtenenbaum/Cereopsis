package org.systemsbiology.gaggle.cereopsis.goose;

import org.systemsbiology.gaggle.core.Goose;
import org.systemsbiology.gaggle.core.Boss;
import org.systemsbiology.gaggle.core.datatypes.*;
import org.systemsbiology.gaggle.geese.common.RmiGaggleConnector;
import org.systemsbiology.gaggle.geese.common.GooseShutdownHook;
import org.systemsbiology.gaggle.util.MiscUtil;
import org.systemsbiology.gaggle.cereopsis.core.GaggleBroker;
import org.systemsbiology.gaggle.cereopsis.core.TopicListener;
import org.systemsbiology.gaggle.cereopsis.core.GaggleProducer;

import javax.swing.*;
import javax.jms.JMSException;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;


/*
* Copyright (C) 2009 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/
public class CereopsisGoose extends JFrame implements Goose, WindowListener {

    RmiGaggleConnector connector = new RmiGaggleConnector(this);
    String myGaggleName = "Cereopsis";
    Boss boss;
    GaggleBroker broker;
    GaggleProducer producer;
    String[] activeGooseNames;

    public CereopsisGoose() {
        super("Cereopsis Goose");
        new GooseShutdownHook(connector);
        addWindowListener(this);
        MiscUtil.setApplicationIcon(this);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Cereopsis Goose is running");
        panel.add(label);
        add(panel);
        setSize(200, 200);
        setVisible(true);

        broker = new GaggleBroker();
        try {
            broker.startBroker();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Message Broker Already In Use");
            System.exit(0);

        }
        TopicListener topicListener = new TopicListener(this);
        producer = new GaggleProducer();

        try {
            topicListener.run();
        } catch (JMSException e) {
            e.printStackTrace();
        }


        try {
            connectToGaggle();
        }
        catch (Exception ex0) {
            System.err.println("CereopsisGoose failed to connect to gaggle: " + ex0.getMessage());
        }
        

    }

    public void connectToGaggle() {
        try {
            connector.connectToGaggle();
        }
        catch (Exception ex0) {
            System.err.println("failed to connect to gaggle: " + ex0.getMessage());
            ex0.printStackTrace();
        }
        boss = connector.getBoss();
    }


    public void handleNameList(String source, Namelist nameList) throws RemoteException {
        System.out.println("Received a Namelist from " + source);
        producer.sendNamelist(nameList, source);
    }

    public void handleMatrix(String source, DataMatrix matrix) throws RemoteException {
        System.out.println("Received a DataMatrix from " + source);
        producer.sendMatrix(matrix, source);
    }

    public void handleTuple(String source, GaggleTuple gaggleTuple) throws RemoteException {
        System.out.println("Received a GaggleTuple from " + source);
    }

    public void handleCluster(String source, Cluster cluster) throws RemoteException {
        System.out.println("Received a Cluster from " + source);
        producer.sendCluster(cluster, source);
    }

    public void handleNetwork(String source, Network network) throws RemoteException {
        System.out.println("Received a Network from " + source);
        producer.sendNetwork(network, source);
    }

    public void update(String[] gooseNames) throws RemoteException {
        System.out.println("Update has been called with " + gooseNames.length + " geese in the gaggle");
        activeGooseNames = gooseNames;
        if (activeGooseNames != null)
            producer.sendGooseList(activeGooseNames);
    }

    public void gotGooseNameRequest() {
        producer.sendGooseName(myGaggleName, activeGooseNames);
    }
    

    public void broadcastNamelist(Namelist namelist) {
        try {
            boss.broadcastNamelist(myGaggleName, "Boss", namelist);
        } catch (RemoteException e) {
            e.printStackTrace();  
        }
    }

    public void broadcastNetwork(Network network) {
        try {
            boss.broadcastNetwork(myGaggleName, "Boss", network);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void broadcastMatrix(DataMatrix matrix) {
        try {
            boss.broadcastMatrix(myGaggleName, "Boss", matrix);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void broadcastCluster(Cluster cluster) {
        try {
            boss.broadcastCluster(myGaggleName, "Boss", cluster);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public String getName() {
        return myGaggleName;
    }

    public void setName(String newName) {
        myGaggleName = newName;
        setTitle(myGaggleName);
        producer.sendGooseName(myGaggleName, activeGooseNames);
    }

    public void doBroadcastList() throws RemoteException { // todo - remove
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doHide() {
        setVisible(false);
    }

    public void doShow() {
        setAlwaysOnTop(true);
        setVisible(true);
        setAlwaysOnTop(false);
    }

    public void doExit() /*throws RemoteException*/ {
        System.out.println("Received doExit() command, exiting....");
        broker.stopBroker();
        System.exit(0);
    }


    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        doExit();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public static void main(String[] args) {
        System.out.println("Starting Cereopsis goose...");
        new CereopsisGoose();
    }

}
