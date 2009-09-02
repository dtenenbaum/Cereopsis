package org.systemsbiology.gaggle.cereopsis.policy;

/*
* Copyright (C) 2009 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/
/*
 * PolicyServer.java
 *
 * This file is part of a tutorial on making a chat application using Flash
 * for the clients and Java for the multi-client server.
 *
 * View the tutorial at http://www.broculos.net/
 */

import java.net.*;

/**
 * The PolicyServer waits for client connections and uses PolicyServerConnections to handle policy requests.
 *
 * @author Nuno Freitas (nunofreitas@gmail.com)
 */
public class PolicyServer extends Thread {
    public static final String POLICY_REQUEST = "<policy-file-request/>";

    public static final String POLICY_XML =
            "<?xml version=\"1.0\"?>"
            + "<cross-domain-policy>"
            + "<site-control permitted-cross-domain-policies='master-only'/>"
            + "<allow-access-from domain=\"*\" to-ports=\"*\" />"
            + "</cross-domain-policy>";


    //public static final String POLICY_REQUEST = "";

    protected int port;
    protected ServerSocket serverSocket;
    protected boolean listening;


    protected static boolean DEBUG = false;

    /**
     * Creates a new instance of PolicyServer.
     *
     * @param serverPort the port to be used by the server
     */
    public PolicyServer(int serverPort) {
        this.port = serverPort;
        this.listening = false;
    }

    /**
     * Gets the server's port.
     *
     * @return the port of the server
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Gets the server's listening status.
     *
     * @return true if the server is listening
     */
    public boolean getListening() {
        return this.listening;
    }

    /**
     * Roots a debug message to the main application.
     *
     * @param msg the debug message to be sent to the main application
     */
    protected void debug(String msg) {
        //Main.debug("PolicyServer (" + this.port + ")", msg);
        if (DEBUG) {
            System.out.println("PolicyServer: " + msg);
        }
    }

    /**
     * Waits for clients' connections and handles them to a new PolicyServerConnection.
     */
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.listening = true;
            debug("listening");

            while (this.listening) {
                Socket socket = this.serverSocket.accept();
                debug("client connection from " + socket.getRemoteSocketAddress());
                PolicyServerConnection socketConnection = new PolicyServerConnection(socket);
                socketConnection.start();
            };
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }

    public void stopPolicyServer() {
        finalize();
    }


    /**
     * Closes the server's socket.
     */
    protected void finalize() {
        try {
            this.serverSocket.close();
            this.listening = false;
            debug("stopped");
        }
        catch (Exception e) {
            debug("Exception (finalize): " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 9876;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Starting policy server on port " + port + "...");
        PolicyServer policyServer = new PolicyServer(port);
        policyServer.start();

    }
}
