package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
class ServerMain {
    
    public static void main(String[] args){
	// https://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html
	System.setProperty("java.net.preferIPv4Stack","true");
	System.setProperty("java.net.preferIPv4Addresses","true");
	
	int sport=Integer.parseUnsignedInt(args[0]);
	int tport=Integer.parseUnsignedInt(args[1]);

	try{
	    ServerSocket mainSocket=new ServerSocket(sport);
	    // Avoid TIME_WAIT bind error:
	    // (https://hea-www.harvard.edu/~fine/Tech/addrinuse.html)	
	    mainSocket.setReuseAddress(true);
	    ServerSocket termSocket=new ServerSocket(tport);
	    termSocket.setReuseAddress(true);	
	    (new Thread(new TerminateServer(termSocket))).start();
	    
	    while(true){
		Socket commandSocket=mainSocket.accept();
		(new Thread(new CommandHandler(commandSocket))).start();
	    }
	}catch(IOException e){
	    System.out.println(e.toString());
	}
    }
}
