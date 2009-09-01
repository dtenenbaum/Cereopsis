package org.systemsbiology.gaggle.cereopsis.core;

import org.apache.activemq.broker.BrokerService;

import javax.swing.*;/*
* Copyright (C) 2007 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/

public class GaggleBroker {

    public static void main(String[] args) {
        try {
            BrokerService broker = new BrokerService();
            broker.setUseJmx(true);
            broker.addConnector("tcp://localhost:61616");
            broker.addConnector("stomp://localhost:61613");
            broker.start();

            // todo start policy server
            // todo add log4j and send log message that broker has started
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
