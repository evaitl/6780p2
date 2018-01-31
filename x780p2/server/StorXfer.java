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
	     int cid,
	     OutputStream os,
	     Path path){
	super(ch,cid); 
	this.os=os;
	this.path=path;
    }
    public void run(){
	if(!accept()){
	    return;
	}
	try{
	    InputStream is=null;
	    byte [] buffer= new byte[1000];
	    int len;
	    if(!terminated){
		is=dataSocket.getInputStream();
	    }
	    while(!terminated && (len=is.read(buffer))!=-1){
		os.write(buffer,0,len);
	    }
	    close();
	    if(terminated){
		ch.println(getCid()+" 500 STOR terminated");
		Files.deleteIfExists(path);
	    }else{
		ch.println(getCid()+" 200 STOR completed");
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}	
    }
    public void close(){
	super.close();
	try{
	    if(os!=null){
		os.close();
		os=null;
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
}
