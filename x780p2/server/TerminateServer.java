package x780p2.server;
import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class TerminateServer implements Runnable{
    private ServerSocket tssock;
    TerminateServer(ServerSocket tssock){
	this.tssock
    }
    public void run(){
	while(true){
	    Socket tsock=tssock.accept();
	    (new Thread(new TerminateHandler(tsock))).start();
	}
    }
}
