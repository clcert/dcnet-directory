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
import java.util.Observable;

/*
    This application work as the Directory node for the DC-NET room, and is necessary for the nodes
    to receive information about the other nodes (ip address and index) in order to establish
    subsequently connections with them to run the anonymous DC-NET protocol.
*/

public class DirectoryNode {

    private String directoryIp;
    private int roomSize;
    private int messageLength;
    private int paddingLength;
    private boolean nonProbabilistic;
    private InfoFromDirectory infoFromDirectory;
    private ObservableParticipantsLeft participantsLeft;

    public boolean configureDirectoryNode(int roomSize, int messageLength, int paddingLength, boolean nonProbabilistic) throws SocketException {
        this.directoryIp = getLocalNetworkIp();
        this.roomSize = roomSize;
        this.messageLength = messageLength;
        this.paddingLength = paddingLength;
        this.nonProbabilistic = nonProbabilistic;
        return true;
    }

    public void createRoom() throws InterruptedException, SocketException {
        // Create crypto.PedersenCommitment object and extract generators that will be used in the protocol by each of the participantNodes
        PedersenCommitment pedersenCommitment = new PedersenCommitment(messageLength, paddingLength, roomSize);
        BigInteger g = pedersenCommitment.getG();
        BigInteger h = pedersenCommitment.getH();
        BigInteger q = pedersenCommitment.getQ();
        BigInteger p = pedersenCommitment.getP();

        // Create object directorynode.InfoFromDirectory with the total number of nodes and values of generators
        this.infoFromDirectory = new InfoFromDirectory(roomSize, g, h, q, p, messageLength, paddingLength, nonProbabilistic);

        // Create context where to run the sockets
        ZContext context = new ZContext();

        // Create the PUB socket and bind it to the port 5555
        ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
        publisher.bind("tcp://*:5555");

        // Create the PULL socket and bind it to the port 5554
        ZMQ.Socket pull = context.createSocket(ZMQ.PULL);
        pull.bind("tcp://*:5554");

        // Wait to receive <numberOfNodes> connections from each node that wants to send a message in this room
        this.participantsLeft = new ObservableParticipantsLeft(roomSize);
        for (int i = 0; i < roomSize; i++) {
            participantsLeft.setValue(participantsLeft.getValue() - i);
            System.out.println("Waiting " + participantsLeft + " participant nodes");
            // Receive a message from the PULL socket, which corresponds to the IP address of this node
            String messageReceived = pull.recvStr();
            // Assign an index to this node and store it in the nodesInTheRoom with his correspondent IP address
            this.infoFromDirectory.nodes[i] = new ParticipantNodeInfoFromDirectory(i+1, messageReceived);
        }

        // Create a Json message with all the information from the directory: every pair {index,ip} and group generators that will be used
        Gson gson = new Gson();
        String directoryJson = gson.toJson(infoFromDirectory);

        // Send broadcast through the PUB socket to all the nodes with the Json message created before
        // TODO: Check if the continuous resending is working or not
        for (int i = 0; i < 10; i++) {
            publisher.send(directoryJson);
            System.out.println("Sent JSON to the nodes: #" + (i+1));
            Thread.sleep(100);
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

    public String getDirectoryIp() {
        return directoryIp;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public String[] getNodesIPs() {
        String[] _a = new String[roomSize];
        int i = 0;
        for (ParticipantNodeInfoFromDirectory info : this.infoFromDirectory.nodes) {
            _a[i] = info.getIp();
            i++;
        }
        return _a;
    }

    private class ObservableParticipantsLeft extends Observable {
        private int participantsLeft = 0;

        public ObservableParticipantsLeft(int participantsLeft) {
            this.participantsLeft = participantsLeft;
        }

        public void setValue(int participantsLeft) {
            this.participantsLeft = participantsLeft;
            setChanged();
            notifyObservers();
        }

        public int getValue() {
            return this.participantsLeft;
        }
    }

}
