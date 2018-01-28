package x780p2.client;

import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class StorData implements DataXfer{
    private Socket dataSocket;
    private int id;
    StorData(int id, Socket dataSocket){
	this.id=id;
	this.dataSocket=dataSocket;
    }
    public void run() {
    }
    public void close(){
	try{
	    dataSocket.close();
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
    public int getId(){
	return id;
    }
}
