import directorynode.DirectoryNode;

import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

public class TestInConsole {

    public static void main(String[] args) throws SocketException, InterruptedException {
        DirectoryNode directoryNode = new DirectoryNode();
        ParticipantConnectedObserver participantConnectedObserver = new ParticipantConnectedObserver(directoryNode.getParticipantConnected());
        directoryNode.getParticipantConnected().addObserver(participantConnectedObserver);

        int roomSize = Integer.parseInt(args[0]);
        int messageLength = Integer.parseInt(args[1]);
        int paddingLength = Integer.parseInt(args[2]);
        boolean nonProbabilistic = Boolean.parseBoolean(args[3]);

        directoryNode.configureDirectoryNode(roomSize, messageLength, paddingLength, nonProbabilistic);
        System.out.println("Directory IP: " + directoryNode.getDirectoryIp());
        directoryNode.createRoom();
        System.out.println("Finished");

    }

    private static class ParticipantConnectedObserver implements Observer {

        private DirectoryNode.ObservableParticipantConnected observableParticipantConnected;
        int participantConnectedIndex;

        public ParticipantConnectedObserver(DirectoryNode.ObservableParticipantConnected observableParticipantConnected) {
            this.observableParticipantConnected = observableParticipantConnected;
            this.participantConnectedIndex = 1;
        }

        @Override
        public void update(Observable observable, Object data) {
            if (observable == observableParticipantConnected) {
                final String participantConnectedValue = observableParticipantConnected.getValue();
                System.out.println("Connected Participant " + participantConnectedIndex + ": " + participantConnectedValue);
                participantConnectedIndex++;
            }
        }

    }

}
