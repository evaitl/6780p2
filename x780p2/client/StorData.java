package x780p2.client;

import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class StorData extends DataXfer{
    StorData(int id, Socket dataSocket){
	super(id,dataSocket);
    }
    public void run() {
    }

}
