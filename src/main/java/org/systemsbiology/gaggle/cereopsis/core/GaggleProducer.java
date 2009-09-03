package org.systemsbiology.gaggle.cereopsis.core;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.systemsbiology.gaggle.core.datatypes.Namelist;
import org.systemsbiology.gaggle.core.datatypes.Network;
import org.systemsbiology.gaggle.core.datatypes.Cluster;
import org.systemsbiology.gaggle.core.datatypes.DataMatrix;
import org.systemsbiology.gaggle.cereopsis.serialization.GaggleNetworkToCompactNetworkConverter;
import org.systemsbiology.gaggle.cereopsis.serialization.CompactNetwork;
import org.systemsbiology.gaggle.cereopsis.serialization.CompactNetworkSerializer;
import org.systemsbiology.gaggle.cereopsis.serialization.SerializableDataMatrix;

import javax.jms.*;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

/*
* Copyright (C) 2009 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/
public class GaggleProducer {

    Connection connection;
    Destination destination;
    Session session;
    MessageProducer producer;

    public GaggleProducer() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("Broadcast");
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); // for now
            System.out.println("Ready for Gaggle broadcasts");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public void sendNamelist(Namelist namelist, String source) {
        JSONObject jsonObject = JSONObject.fromObject(namelist);
        String json = jsonObject.toString();
        sendMessage("Namelist", json, source);
    }

    protected void sendMessage(String messageType, String messageBody, String source) {
        try {
            TextMessage message = session.createTextMessage(messageBody);
            message.setStringProperty("MessageType",messageType);
            message.setStringProperty("Source", source);
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void sendNetwork(Network network, String source) {
        GaggleNetworkToCompactNetworkConverter converter = new  GaggleNetworkToCompactNetworkConverter(network);
        CompactNetwork cn = converter.toCompactNetwork();
        String json = CompactNetworkSerializer.serializeToJSON(cn);
        sendMessage("Network", json, source);
    }

    public void sendCluster(Cluster cluster, String source) {
        sendMessage("Cluster", JSONObject.fromObject(cluster).toString(), source);
    }

    public void sendMatrix(DataMatrix matrix, String source) {
        SerializableDataMatrix sdm = new SerializableDataMatrix(matrix);
        JSONObject jsonObject = JSONObject.fromObject(sdm);
        sendMessage("DataMatrix", jsonObject.toString(), source);
    }

    public void sendGooseList(String[] gooseNames) {
        sendMessage("GooseList", JSONArray.fromObject(gooseNames).toString(), null);
    }

    public void sendGooseName(String gooseName, String[] gooseNames) {
        sendGooseList(gooseNames);
        sendMessage("GooseName", gooseName, null);
    }
}
