import java.math.BigInteger;

class InfoFromDirectory {

    ParticipantNodeInfoFromDirectory[] nodes;
    private BigInteger g, h;
    private BigInteger q, p;
    private int l, padLength;

    InfoFromDirectory(int n, BigInteger g, BigInteger h, BigInteger q, BigInteger p, int l, int padLength) {
        this.nodes = new ParticipantNodeInfoFromDirectory[n];
        this.g = g;
        this.h = h;
        this.q = q;
        this.p = p;
        this.l = l;
        this.padLength = padLength;
    }

}
