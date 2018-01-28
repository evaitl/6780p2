package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Files;

class StorXfer extends  DataXfer {
    private ServerSocket pasvListener;
    private OutputStream os;
    private Path path;
    StorXfer(CommandHandler ch,
	     ServerSocket pasvListener,
	     int id,
	     OutputStream os,
	     Path path){
	super(ch,id,pasvListener); 
	this.os=os;
	this.path=path;
    }
    public void run(){
	try{
	    accept();
	    InputStream is=null;
	    byte [] buffer= new byte[1000];
	    int len;
	    if(!terminated){
		// We could've been terminated/closed during accept().
		is=dataSocket.getInputStream();
	    }
	    while(!terminated && (len=is.read(buffer))!=-1){
		os.write(buffer,0,len);
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}	
	close();
	if(terminated){
	    ch.println(getId()+" 455 STOR terminated");
	    try{
		Files.deleteIfExists(path);
	    }catch(IOException e){
		throw new UncheckedIOException(e);
	    }
	}else{
	    ch.println(getId()+" 250 STOR completed");
	}
    }
    public void close(){
	super.close();
	try{
	    if(os!=null){
		os.close();
		os=null;
	    }
	    if(pasvListener!=null){
		pasvListener.close();
		pasvListener=null;
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
}
