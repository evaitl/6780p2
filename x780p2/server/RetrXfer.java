package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import x780p2.DataXfer;

class RetrHandler extends  DataXfer {
    private ServerSocket pasvListener;
    private InputStream is;
    RetrHandler(ServerSocket pasvListener,
		int id,
		InputStream is){
	super(id,null); // Set dataSocket when accept returns.
	this.pasvListener=pasvListener;
	this.is=is;
    }
    public void run(){

    }
    public void close(){
	super.close();
	try{
	    if(is!=null){
		is.close();
		is=null;
	    }
	    if(ServerSocket!=null){
		ServerSocket.close();
		ServerSocket=null;
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
}
