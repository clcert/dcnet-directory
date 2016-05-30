import directorynode.DirectoryNode;

import java.net.SocketException;

public class TestInConsole {

    public static void main(String[] args) throws SocketException, InterruptedException {
        DirectoryNode directoryNode = new DirectoryNode();

        int roomSize = Integer.parseInt(args[0]);
        int messageLength = Integer.parseInt(args[1]);
        int paddingLength = Integer.parseInt(args[2]);
        boolean nonProbabilistic = Boolean.parseBoolean(args[3]);

        directoryNode.configureDirectoryNode(roomSize, messageLength, paddingLength, nonProbabilistic);
        System.out.println("Directory IP: " + directoryNode.getDirectoryIp());
        directoryNode.createRoom();

    }

}
