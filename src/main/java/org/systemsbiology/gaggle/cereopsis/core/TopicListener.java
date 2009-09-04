package org.systemsbiology.gaggle.cereopsis.core;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.systemsbiology.gaggle.cereopsis.goose.CereopsisGoose;
import org.systemsbiology.gaggle.cereopsis.serialization.CompactNetworkToGaggleNetworkConverter;
import org.systemsbiology.gaggle.cereopsis.serialization.CompactNetwork;
import org.systemsbiology.gaggle.cereopsis.serialization.CompactNetworkSerializer;
import org.systemsbiology.gaggle.cereopsis.serialization.SerializableDataMatrix;
import org.systemsbiology.gaggle.core.datatypes.Namelist;
import org.systemsbiology.gaggle.core.datatypes.Cluster;

import javax.jms.*;

import net.sf.json.JSONObject;

/*
* Copyright (C) 2009 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/
public class TopicListener implements MessageListener {


    private CereopsisGoose goose;



    public TopicListener(CereopsisGoose goose) {
        this.goose = goose;
        
    }

    public void onMessage(Message message) {
        //System.out.println("Got a message!");

        

        //System.out.println("its class is " + message.getClass().getName());

        ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;


        try {
            if (textMessage.getStringProperty("MessageType") != null) {
                //System.out.println("its type is " + message.getStringProperty("MessageType"));
                //System.out.println("Message content:\n" + textMessage.getText());
                JSONObject jsonObject = JSONObject.fromObject( textMessage.getText() );
                String target = "Boss";
                if (message.getStringProperty("Target") != null) {
                    target = message.getStringProperty("Target");
                }
                handleMessage(textMessage.getStringProperty("MessageType"), jsonObject.toString(), target);

            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


    protected void handleMessage(String messageType, String message, String target) {
        if (messageType.equals("Namelist"))  {
            handleNamelist(message, target);
        } else if (messageType.equals("RequestGooseName")) {
            goose.gotGooseNameRequest();
        }  else if (messageType.equals("Network")) {
            handleNetwork(message, target);
        } else if (messageType.equals("DataMatrix")) {
            handleMatrix(message, target);
        } else if (messageType.equals("Cluster")) {
            handleCluster(message, target);
        } else if (messageType.equals("Show")) {
            goose.showGoose(target);
        } else if (messageType.equals("Hide"))  {
            goose.hideGoose(target);
        }
    }

    protected void handleNamelist(String message, String target) {
        JSONObject jsonObject = JSONObject.fromObject(message);
        Namelist namelist = (Namelist) JSONObject.toBean( jsonObject, Namelist.class );
        goose.broadcastNamelist(namelist, target);
    }

    protected void handleNetwork(String message, String target) {
        CompactNetwork cn = CompactNetworkSerializer.serializeFromJSON(message);
        CompactNetworkToGaggleNetworkConverter converter = new CompactNetworkToGaggleNetworkConverter(cn);
        goose.broadcastNetwork(converter.toGaggleNetwork(),  target);
    }

    protected void handleMatrix(String message, String target) {
        JSONObject bodyObject = JSONObject.fromObject(message);
        SerializableDataMatrix m = (SerializableDataMatrix) JSONObject.toBean(bodyObject, SerializableDataMatrix.class);
        goose.broadcastMatrix(m.toDataMatrix(),  target);
    }

    protected void handleCluster(String message, String target) {
        JSONObject bodyObject = JSONObject.fromObject(message);
        Cluster c = (Cluster)JSONObject.toBean(bodyObject, Cluster.class);
        goose.broadcastCluster(c, target);
    }



    public void run() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("Broadcast");

        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(this);

        connection.start();

        System.out.println("Waiting for messages...");
    }

}
