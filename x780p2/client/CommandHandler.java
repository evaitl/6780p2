package x780p2.client;

import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class CommandHandler implements Runnable, Closeable {
    private Socket commandSocket;
    CommandHandler(Socket commandSocket){
	this.commandSocket=commandSocket;
    }
    public void run(){
    }
    public void close(){
	try{
	    commandSocket.close();
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }    
}
