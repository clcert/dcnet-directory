import com.google.gson.Gson;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class DirectoryNode {

    public static void main(String[] args) throws InterruptedException {
        int n = Integer.parseInt(args[0]);
        Directory directory = new Directory(n);

        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(ZMQ.PUB);
        publisher.bind("tcp://*:5555");

        ZMQ.Socket pull = context.createSocket(ZMQ.PULL);
        pull.bind("tcp://*:5554");

        System.out.println("Waiting to receive connections");
        for (int i = 0; i < n; i++) {
            String messageReceived = pull.recvStr();
            int index = Integer.parseInt(messageReceived.split("%")[0]);
            String ip = messageReceived.split("%")[1];
            directory.nodes[i] = new NodeDCNET(index, ip);
        }

        System.out.println("Creating JSON with {index,ip}");
        Gson gson = new Gson();
        String directoryJson = gson.toJson(directory);
        System.out.println(directoryJson);

        Thread.sleep(20000);
        for (int i = 0; i < 10; i++) {
            publisher.send(directoryJson);
            System.out.println("Sent JSON to the nodes: #" + (i+1));
            Thread.sleep(5000);
        }

        System.out.println("Finished");

    }

}
