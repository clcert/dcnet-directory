package directorynode;

import com.google.gson.Gson;
import crypto.PedersenCommitment;
import json.ParticipantNodeInfoFromDirectory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/*
    This application work as the Directory node for the DC-NET room, and is necessary for the nodes
    to receive information about the other nodes (ip address and index) in order to establish
    subsequently connections with them to run the anonymous DC-NET protocol.
*/

public class DirectoryNode {

    // Usage: ./gradlew run -PappArgs=[<numberOfNodes>]
    public static void main(String[] args) throws InterruptedException, SocketException {
        // Print directorynode.InfoFromDirectory IP address
        System.out.println("Directory IP: " + getLocalNetworkIp());

        // Variable to store the number of nodes admitting in the room controlled by this nodesInTheRoom node
        int n = Integer.parseInt(args[0]);

        // Variable to store the message size, in order to create group for commitments
        int l = Integer.parseInt(args[1]);

        // Variable to store the padding length of the future messages to send
        int padLength = Integer.parseInt(args[2]);

        // Variable to store non probabilistic mode of the room
        boolean nonProbabilistic = Boolean.parseBoolean(args[3]);

        // Create crypto.PedersenCommitment object and extract generators that will be used in the protocol by each of the participantNodes
        PedersenCommitment pedersenCommitment = new PedersenCommitment(l, padLength, n);
        BigInteger g = pedersenCommitment.getG();
        BigInteger h = pedersenCommitment.getH();
        BigInteger q = pedersenCommitment.getQ();
        BigInteger p = pedersenCommitment.getP();

        // Create object directorynode.InfoFromDirectory with the total number of nodes and values of generators
        InfoFromDirectory infoFromDirectory = new InfoFromDirectory(n, g, h, q, p, l, padLength, nonProbabilistic);

        // Create context where to run the sockets
        ZContext context = new ZContext();

        // Create the PUB socket and bind it to the port 5555
        ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
        publisher.bind("tcp://*:5555");

        // Create the PULL socket and bind it to the port 5554
        ZMQ.Socket pull = context.createSocket(ZMQ.PULL);
        pull.bind("tcp://*:5554");

        // Wait to receive <numberOfNodes> connections from each node that wants to send a message in this room
        System.out.println("Waiting to receive connections");
        for (int i = 0; i < n; i++) {
            // Receive a message from the PULL socket, which corresponds to the IP address of this node
            String messageReceived = pull.recvStr();
            // Assign an index to this node and store it in the nodesInTheRoom with his correspondent IP address
            infoFromDirectory.nodes[i] = new ParticipantNodeInfoFromDirectory(i+1, messageReceived);
        }

        // Create a Json message with all the information from the directory: every pair {index,ip} and group generators that will be used
        System.out.println("Creating JSON with the necessary info for the nodes");
        Gson gson = new Gson();
        String directoryJson = gson.toJson(infoFromDirectory);
        System.out.println(directoryJson);

        // Send broadcast through the PUB socket to all the nodes with the Json message created before
        // TODO: Check if the continuous resending is working or not
        for (int i = 0; i < 5; i++) {
            publisher.send(directoryJson);
            System.out.println("Sent JSON to the nodes: #" + (i+1));
            Thread.sleep(3000);
        }

        // Close both sockets
        publisher.close();
        pull.close();

        // The task of the directorynode.DirectoryNode is over
        System.out.println("Finished");

    }

    // Get the LAN IP address of the node
    private static String getLocalNetworkIp() throws SocketException {
        String ip = "";
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            if (!n.getDisplayName().contains("docker")) {
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (!i.isLinkLocalAddress() && !i.isLoopbackAddress()) {
                        ip =  i.getHostAddress();
                    }
                }
            }
        }
        return ip;
    }

}
