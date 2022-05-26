package bgu.spl.net.BGSServer;

import bgu.spl.net.impl.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

import java.io.IOException;

public class ReactorMain {
    public static void main(String[] args) throws IOException {
        DataHolder.getInstance().ADD_DATA_FOR_TEST();//
        Server.reactor(3, 7777,
                () -> new BidiMessagingProtocolImpl(),
                () -> new MessageEncoderDecoderImpl()).serve();
    }
}
