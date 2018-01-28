package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

class RetrHandler implements DataXfer {
    private ServerSocket pasvListener;
    private Socket dataSocket;
    private int id;
    private OutputStream os;
    private boolean terminate=false;
    RetrHandler(ServerSocket pasvListener,
		int id,
		OutputStream os){
	this.id=id;
	this.pasvListener=pasvListener;
	this.os=os;
    }
    public int getId(){
	return id;
    }
    void terminateTransfer(){
	terminate=true;
    }
    public void run(){

    }
    public void close(){
	try{
	    if(pasvListener!=null) pasvListener.close();
	    if(dataSocket!=null) dataSocket.close();
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}	
    }
}
