package x780p2.client;
import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class DataXfer implements Runnable, Closeable{
    private int cid;
    private int xid;
    protected Socket dataSocket;
    protected ClientMain cm;
    protected DataXfer(ClientMain cm, int cid, Socket dataSocket){
	this(cm, cid, dataSocket,0);
    }
    protected DataXfer(ClientMain cm, int cid, Socket dataSocket,int xid){
	this.cm=cm;
	this.cid=cid;
	this.dataSocket=dataSocket;
	this.xid=xid;
	Xfers.add(this);
    }
    public int getCid(){
	return cid;
    }
    public int getXid(){
	return xid;
    }
    
    public void close(){
	Xfers.del(cid,false);
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
