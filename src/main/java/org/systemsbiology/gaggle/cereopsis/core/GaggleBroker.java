package org.systemsbiology.gaggle.cereopsis.core;

import org.apache.activemq.broker.BrokerService;
import org.systemsbiology.gaggle.cereopsis.policy.PolicyServer;

/*
* Copyright (C) 2009 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/

public class GaggleBroker {

    private BrokerService broker;
    private PolicyServer policyServer;


    public GaggleBroker() {
        broker = new BrokerService();
        policyServer = new PolicyServer(9876);


    }

    public void startBroker() throws Exception{
        //broker.setUseJmx(true);
        broker.addConnector("tcp://localhost:61616");
        broker.addConnector("stomp://localhost:61613");
        broker.start();

        policyServer.start();

        // todo add log4j and send log message that broker and policy server have started
    }

    public void stopBroker() {
        policyServer.stopPolicyServer();
        try {
            broker.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //new GaggleBroker().startBroker();
    }


}
