package x780p2.server;
import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class DataXfer implements Runnable, Closeable{
    private int id;
    private ServerSocket pasvListener;
    protected Socket dataSocket;
    protected CommandHandler ch;
    protected boolean terminated=false;
    protected DataXfer(int id, ServerSocket pasvListener){
	this.id=id;
	this.pasvListener=pasvListener;
    }

    protected void accept(){
	try {
	    dataSocket=pasvListener.accept();
	    pasvListener.close();
	    pasvListener=null;
	}catch(IOException e){
	    // Ignore. Possibly terminated/closed while accepting.
	}
    }
    
    public void terminate(){
	terminated=true;
    }
    
    public int getId(){
	return id;
    }
    
    public void close(){
	try{
	    if(dataSocket!=null){
		dataSocket.close();
		dataSocket=null;
	    }
	    if(pasvListener!=null){
		pasvListener.close();
		pasvListener=null;
	    }
	}catch(IOException e){
	    // Ignore close exceptions.
	}
    }
}
