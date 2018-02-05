package x780p2.server;
import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
class TerminateServer implements Runnable{
    private ServerSocket tssock;
    TerminateServer(ServerSocket tssock){
	this.tssock=tssock;
    }
    public void run(){
	try{
	    
	    while(true){
		Socket tsock=tssock.accept();
		tsock.shutdownOutput();
		(new Thread(new TerminateHandler(tsock))).start();
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
}
