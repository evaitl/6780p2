package x780p2.client;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class RetrData extends DataXfer {
    RetrData(int id, Socket dataSocket){
	super(id,dataSocket);
    }
    public void run(){
    }
};
