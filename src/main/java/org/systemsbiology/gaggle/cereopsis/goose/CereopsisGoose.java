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
        producer.sendNamelist(nameList, source);
    }

    public void handleMatrix(String source, DataMatrix matrix) throws RemoteException {
        producer.sendMatrix(matrix, source);
    }

    public void handleTuple(String source, GaggleTuple gaggleTuple) throws RemoteException {
    }

    public void handleCluster(String source, Cluster cluster) throws RemoteException {
        producer.sendCluster(cluster, source);
    }

    public void handleNetwork(String source, Network network) throws RemoteException {
        producer.sendNetwork(network, source);
    }

    public void update(String[] gooseNames) throws RemoteException {
        activeGooseNames = gooseNames;
        producer.sendGooseList(activeGooseNames);
    }

    public void gotGooseNameRequest() {
        producer.sendGooseName(myGaggleName, activeGooseNames);
    }
    

    public void broadcastNamelist(Namelist namelist, String target) {
        try {
            boss.broadcastNamelist(myGaggleName, target, namelist);
        } catch (RemoteException e) {
            e.printStackTrace();  
        }
    }

    public void broadcastNetwork(Network network, String target) {
        try {
            boss.broadcastNetwork(myGaggleName, target, network);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void broadcastMatrix(DataMatrix matrix, String target) {
        try {
            boss.broadcastMatrix(myGaggleName, target, matrix);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void broadcastCluster(Cluster cluster, String target) {
        try {
            boss.broadcastCluster(myGaggleName, target, cluster);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void showGoose(String target) {
        try {
            boss.show(target);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void hideGoose(String target) {
        try {
            boss.hide(target);
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
