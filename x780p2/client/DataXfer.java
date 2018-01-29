package x780p2.client;
import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class DataXfer implements Runnable, Closeable{
    private int cid;
    protected Socket dataSocket;
    protected boolean terminated=false;
    protected DataXfer(int cid, Socket dataSocket){
	this.cid=cid;
	this.dataSocket=dataSocket;
    }
    public void terminate(){
	terminated=true;
    }
    public int getId(){
	return cid;
    }
    public void close(){
	try{
	    if(dataSocket!=null){
		dataSocket.close();
		dataSocket=null;
	    }
	}catch(IOException e){
	    // Ignore close errors.
	}
    }
}
