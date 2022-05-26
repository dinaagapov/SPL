package bgu.spl.net.BGSServer;

import bgu.spl.net.impl.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Messagaes.Message;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        try (BaseServer<Message> server = (BaseServer<Message>) Server.threadPerClient(Integer.parseInt(args[0]), () -> new BidiMessagingProtocolImpl(), () -> new MessageEncoderDecoderImpl());) {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
