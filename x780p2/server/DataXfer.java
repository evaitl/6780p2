package x780p2.server;
import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.nio.file.Path;

public abstract class DataXfer implements Runnable, Closeable{
    protected int cid;
    protected int xid;
    protected Path path;
    protected CommandHandler ch;
    protected boolean terminated=false;

    protected ServerSocket pasvListener;
    protected Socket dataSocket;
    
    protected DataXfer(CommandHandler ch, int cid, Path path){
	this.ch=ch;
	this.cid=cid;
	this.path=path;
	xid=XferId.next();
	Xfers.register(this);
	try{
	    pasvListener=new ServerSocket(0);
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
	StringBuilder sb=new StringBuilder();
	sb.append(cid).append(" 100 ").append(xid)
	    .append(',').append(ch.commandSocket.getLocalAddress().getHostAddress())
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
	    // Probably terminate(), which closed socket. 
	    ch.println(getCid() + " 500 accept failure");
	    return false;
	}
	return !terminated;
    }
    
    public void terminate(){
	terminated=true;
	// Network writes block when the buffers are full. Whack the
	// loop with a hammer by closing the network socket in addition
	// to setting the terminate flag. 
	close();
    }
    
    public int getXid(){
	return xid;
    }
    public int getCid(){
	return cid;
    }
    public void close(){
	Xfers.done(this);
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
    @Override
    public boolean equals(Object o){
	if(this==o){
	    return true;
	}
	return false;
    }
}
