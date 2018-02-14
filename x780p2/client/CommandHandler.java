package x780p2.client;

import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Scanner;
import java.io.PrintStream;

import static java.lang.System.out;
class CommandHandler implements Runnable, Closeable {
    private Socket commandSocket;
    private PrintStream os;
    
    CommandHandler(Socket commandSocket){
	this.commandSocket=commandSocket;
	out.println("ch created: "+commandSocket);
	try{
	    os=new PrintStream(commandSocket.getOutputStream());
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}

    }
    
    synchronized void println(String s){
	os.println(s);
    }
    
    public void run(){
	out.println("ch run");
	try{
	    Scanner in=new Scanner(commandSocket.getInputStream());
	    while(in.hasNextLine()){
		out.println("ch next line");
		Responses.add(in.nextLine());
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
	out.println("Command socket closed. Exiting.");
	System.exit(1);
    }
    public void close(){
	try{
	    commandSocket.close();
	}catch(IOException e){
	    // don't catch close errors.
	    out.println("close error: "+e);
	}
    }    
}
