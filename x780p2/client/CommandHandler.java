package x780p2.client;

import java.io.Closeable;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Scanner;
import java.io.PrintStream;

class CommandHandler implements Runnable, Closeable {
    private Socket commandSocket;
    private PrintStream os;
    CommandHandler(Socket commandSocket){
	this.commandSocket=commandSocket;
	try{
	    os=new PrintStream(commandSocket.getOutputStream());
	}catch(Exception e){
	    System.out.println(e.toString());
	    System.exit(1);
	}

    }
    
    synchronized void println(String s){
	os.println(s);
    }
    
    public void run(){
	try{
	    Scanner in=new Scanner(commandSocket.getInputStream());
	    while(in.hasNextLine()){
		Responses.add(in.nextLine());
	    }
	}catch(IOException e){
	    System.out.println(e.toString());
	    return;
	}

    }
    public void close(){
	try{
	    commandSocket.close();
	}catch(IOException e){
	}
    }    
}
