package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

class RetrXfer extends DataXfer{
    private ServerSocket pasvListener;
    private InputStream is;
    RetrXfer(CommandHandler ch,
	     int cid,
	     InputStream is){
	super(ch,cid);
	this.is=is;
    }
    public void run(){
	if(!accept()){
	    return;
	}
	try{
	    OutputStream os=dataSocket.getOutputStream();
	    byte [] buffer= new byte[1000];
	    int len;
	    while(!terminated && (len=is.read(buffer))!=-1){
		os.write(buffer,0,len);
	    }
	    close();
	    if(terminated){
		ch.println(getCid()+" 500 RETR terminated");
	    }else{
		ch.println(getCid()+" 200 RETR completed");
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}	
    }
    public void close(){
	super.close();
	try{
	    if(is!=null){
		is.close();
		is=null;
	    }
	}catch(IOException e){
	    // Ignore close errors
	}
    }
}
