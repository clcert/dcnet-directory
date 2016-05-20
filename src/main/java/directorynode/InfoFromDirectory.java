package directorynode;

import json.ParticipantNodeInfoFromDirectory;

import java.math.BigInteger;

public class InfoFromDirectory {

    public ParticipantNodeInfoFromDirectory[] nodes;
    private BigInteger g, h;
    private BigInteger q, p;
    private int l, padLength;
    private boolean nonProbabilistic;

    public InfoFromDirectory(int n, BigInteger g, BigInteger h, BigInteger q, BigInteger p, int l, int padLength, boolean nonProbabilistic) {
        this.nodes = new ParticipantNodeInfoFromDirectory[n];
        this.g = g;
        this.h = h;
        this.q = q;
        this.p = p;
        this.l = l;
        this.padLength = padLength;
        this.nonProbabilistic = nonProbabilistic;
    }

}
