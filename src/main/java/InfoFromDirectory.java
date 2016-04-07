import java.math.BigInteger;

public class InfoFromDirectory {

    ParticipantNodeInfoFromDirectory[] nodes;
    BigInteger g, h;
    BigInteger q, p;

    public InfoFromDirectory(int n, BigInteger g, BigInteger h, BigInteger q, BigInteger p) {
        this.nodes = new ParticipantNodeInfoFromDirectory[n];
        this.g = g;
        this.h = h;
        this.q = q;
        this.p = p;
    }

}
