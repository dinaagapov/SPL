package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.impl.Messagaes.PMMessage;
import bgu.spl.net.impl.Messagaes.PostMessage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int connectionid;
    public static ConnectionsImpl connections = new ConnectionsImpl();


    public BlockingConnectionHandler(int connectionID,Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol) {
        connectionid = connectionID;
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.protocol.start(connectionid, connections);
    }
    public int getConnectionid() {
        return connectionid;
    }

    @Override
    public void run() {
        int x=0;
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (x==-1)
                {
                    reset();
                    x++;
                }
                if (nextMessage != null) {
                    encdec.reset();
                    protocol.process(nextMessage);
                    if (nextMessage instanceof PostMessage ||nextMessage instanceof PMMessage)x=-1;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            out.write(encdec.encode(msg));
            out.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void reset()
    {
        encdec.reset();
    }
}
