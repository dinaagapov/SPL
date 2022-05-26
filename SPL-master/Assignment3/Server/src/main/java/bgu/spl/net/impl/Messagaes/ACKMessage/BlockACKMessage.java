package bgu.spl.net.impl.Messagaes.ACKMessage;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.Message;

public class BlockACKMessage extends Message {
    private final short opcode = 12;
    private final short ackOp=10;
    public BlockACKMessage(){ }
    public void process(Connections connections, int connectionID, DataHolder dataHolder){}
    public String messageToString() {
        return ackOp+"ACK " + opcode+";";
    }
}
