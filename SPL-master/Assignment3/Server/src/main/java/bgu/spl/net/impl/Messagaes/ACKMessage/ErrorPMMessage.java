package bgu.spl.net.impl.Messagaes.ACKMessage;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.Message;

public class ErrorPMMessage extends Message {
    private final short opcode = 6;
    int zero=0;
    private String userNAME;
    public ErrorPMMessage(String S){
        userNAME = S;
    }
    public void process(Connections connections, int connectionID, DataHolder dataHolder){}
    public String messageToString(){
        return 11 +"@"+userNAME+" isn’t applicable for private messages;";
    }
}
