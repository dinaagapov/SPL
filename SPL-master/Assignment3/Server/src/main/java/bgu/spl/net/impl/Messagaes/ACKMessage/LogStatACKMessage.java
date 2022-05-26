package bgu.spl.net.impl.Messagaes.ACKMessage;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.Message;

public class LogStatACKMessage extends Message {
    private int age;
    private int numPosts;
    private int numFollowers;
    private int numFollowing;

    public LogStatACKMessage(int age ,int NumOfPost, int NumOfFollowers, int NumOfFollowing)
    {
        this.age = age;
        numPosts = NumOfPost;
        numFollowers = NumOfFollowers;
        numFollowing = NumOfFollowing;
    }

    public void process(Connections connections, int connectionID, DataHolder dataHolder){}
    public String messageToString(){
        return 10+"ACK 0" + 7 +" " + age +" " + numPosts + " " + numFollowers+ " "+ numFollowing+";";
    }
}
