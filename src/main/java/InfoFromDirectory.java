import java.math.BigInteger;

class InfoFromDirectory {

    ParticipantNodeInfoFromDirectory[] nodes;
    private BigInteger g, h;
    private BigInteger q, p;

    InfoFromDirectory(int n, BigInteger g, BigInteger h, BigInteger q, BigInteger p) {
        this.nodes = new ParticipantNodeInfoFromDirectory[n];
        this.g = g;
        this.h = h;
        this.q = q;
        this.p = p;
    }

}
