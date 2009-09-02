package org.systemsbiology.gaggle.cereopsis.core;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.systemsbiology.gaggle.cereopsis.goose.CereopsisGoose;
import org.systemsbiology.gaggle.core.datatypes.Namelist;

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
public class TopicListener implements MessageListener {


    private Connection connection;
    private Session session;
    private Topic topic;

    private CereopsisGoose goose;



    public TopicListener(CereopsisGoose goose) {
        this.goose = goose;
        
    }

    public void onMessage(Message message) {
        System.out.println("Got a message!");

        

        System.out.println("its class is " + message.getClass().getName());

        ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;


        try {
            if (textMessage.getStringProperty("MessageType").equals("Namelist")) {
                System.out.println("it's a namelist message!");
                System.out.println("Text is: ");
                System.out.println(textMessage.getText());

                JSONArray jsonArray = JSONArray.fromObject(textMessage.getText());
                String[] names = new String[jsonArray.size()];

                Namelist namelist = new Namelist();

                for(int i = 0; i < names.length; i++) {
                    names[i] = jsonArray.getString(i);
                }

                namelist.setNames(names);
                goose.broadcastNamelist(namelist);


            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


    public void run() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic("Broadcast");

        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(this);

        connection.start();

        System.out.println("Waiting for messages...");
    }

}
