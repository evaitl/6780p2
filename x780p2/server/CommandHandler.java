package x780p2.server;
import java.net.Socket;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.nio.file.Files;

class CommandHandler implements Runnable, Closeable {
    private Socket commandSocket;
    private PrintStream ps;
    Path cwd;
    synchronized void println(String s){
	ps.println(s);
    }
    CommandHandler(Socket commandSocket){
	this.commandSocket=commandSocket;
	ps=new PrintStream(commandSocket.getOutputStream());
	cwd=Paths.get(".").normalize().toAbsolutePath();
    }
    private void doList(Command c){
	(new Thread(new ListXfer(this, c.cid, cwd))).start();
    }
    private void doStor(Command c){
	FileOutputStream fos=null;
	try{
	    fos=new FileOutputStream(cwd.resolve(c.arg).
				     normalize().toAbsolutePath());
	}catch(IOException e){
	    println(c.cid+" 500 Couldn't open target");
	    return;
	}
	(new Thread(new StorXfer(this, c.cid,fos))).start();
    }
    private void doRetr(Command c){
	FileInputStream fis;
	try{
	    fis=new FileInputStream(cwd.resolve(c.arg).
				    normalize().toAbsolutePath());
	}catch(IOException e){
	    println(c.cid+" 500 Couldn't open source file");
	    return;
	}
	(new Thread(new ListXfer(this, c.cid, fis))).start();
    }
    public void run(){
	Scanner sin=new Scanner(commandSocket.getInputStream());	
	while(sin.hasNextLine()){
	    Command c=new Command(sin.nextLine());
	    switch(c.command){
	    case "mkd":
		{
		    Path p=cwd.resolve(c.arg).normalize().toAbsolutePath();
		    try{
			Files.createDirectory(p);
			println(c.cid+" 200 Directory created");
		    }catch(Exception e){
			println(c.cid+" 500 dir not created");
		    }
		}
		break;
	    case "cdup":
		cwd=cwd.resolve("..").normalize().toAbsolutePath();
		println(c.cid +" 200 moved up");
		break;
	    case "cwd":
		if(Files.isDirectory(cwd.resolve(c.arg))){
		    cwd=cwd.resolve(c.arg).normalize().toAbsolutePath();
		    println(c.cid+" 200 changed directory");
		}else{
		    println(c.cid+" 500 no can do");
		}
		break;
	    case "dele":
		{
		    Path p = cwd.resolve(c.arg).normalize().toAbsolutePath();
		    if (!Files.exists(p)){
			println(c.cid+ " 500 file "+c.arg+" doesn't exist?");
		    }else if(Files.isDirectory(p)) {
			println(c.cid+ " 500 "+c.arg+" is a directory");
		    }else{
			try{
			    Files.delete(p);
			}catch (Exception e) {
			    println(c.cid+ " 500 Couldn't delete for some reason");
			    break;
			}
			println(c.cid+" 200 deleted");
		    }
		}
		break;
	    case "pwd":
		println(c.cid + " 200 "+cwd);
		break;
	    case "list":
		doList(c);
		break;
	    case "retr":
		doRetr(c);
		break;
	    case "stor":
		doStor(c);
		break;
	    }
	}
    }
    public void close(){
	try{
	    commandSocket.close();
	}catch(IOException e){
	}
    }
}
