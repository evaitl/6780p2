package x780p2.server;
import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

public abstract class DataXfer implements Runnable, Closeable{
    private int cid;
    private int xid;
    protected CommandHandler ch;
    protected boolean terminated=false;

    protected ServerSocket pasvListener;
    protected Socket dataSocket;
    
    protected DataXfer(CommandHandler ch, int cid){
	this.ch=ch;
	this.cid=cid;
	xid=XferId.next();
	try{
	    pasvListener=new ServerSocket(0);
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
	StringBuilder sb=new StringBuilder();
	sb.append(cid).append(" 100 ").append(xid)
	    .append(',').append(ch.commandSocket.getLocalAddress().toString())
	    .append(',').append(pasvListener.getLocalPort());
	ch.println(sb.toString());
    }

    synchronized void closePassive(){
	try{
	    if(pasvListener!=null){
		pasvListener.close();
		pasvListener=null;
	    }
	}catch(IOException e){
	    // Do I care?
	}
    }

    protected boolean accept(){
	try {
	    dataSocket=pasvListener.accept();
	    closePassive();
	}catch(IOException e){
	    close();
	    return false;
	}
	return !terminated;
    }
    
    public void terminate(){
	terminated=true;
	closePassive();
    }
    
    public int getXid(){
	return xid;
    }
    public int getCid(){
	return xid;
    }
    public void close(){
	try{
	    if(dataSocket!=null){
		dataSocket.close();
		dataSocket=null;
	    }
	    closePassive();
	}catch(IOException e){
	    // Ignore close exceptions.
	}
    }
}
