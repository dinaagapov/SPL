package bgu.spl.net.impl.Messagaes.ACKMessage;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.Message;

public class StatACKMessage extends Message {
    private final short opcode = 8;
    private int age;
    private int numPosts;
    private int numFollowers;
    private int numFollowing;


    public StatACKMessage(int age, int NumOfPost, int NumOfFollowers, int NumOfFollowing) {
        this.age = age;
        numPosts = NumOfPost;
        numFollowers = NumOfFollowers;
        numFollowing = NumOfFollowing;
    }


    public void process(Connections connections, int connectionID, DataHolder dataHolder) {
    }

    public String messageToString() {
        return "ACK 0" + opcode + " " + age + " " + numPosts + " " + numFollowers + " " + numFollowing+";";
    }
}
