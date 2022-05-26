package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;

public class ErrorMessage extends Message{
    private short opcode;


    public ErrorMessage(short msgOpcode)
    {
        this.opcode = msgOpcode;
    }
    public void process(Connections connections, int connectionID, DataHolder dataHolder){}

    public String messageToString(){
        return  11 + ""+zero+"" + opcode+";";
    }
}
