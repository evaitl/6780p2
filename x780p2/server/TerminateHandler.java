package x780p2.server;
import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Closeable;

class TerminateHandler implements Runnable, Closeable{
    Scanner sin;
    TerminateHandler(Socket tsock){
	try{
	    sin=new Scanner(tsock.getInputStream());
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
    public void run(){
	try{
	while(sin.hasNextLine()){
	    String line=sin.nextLine();
	    DataXfer dx=Xfers.getxid(Integer.parseInt(line.trim()));
	    if(dx!=null){
		dx.terminate();
	    }
	}
	}catch(Exception e){
	    //
	}
    }
    public void close(){
	sin.close();
    }
}
