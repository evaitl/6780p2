package x780p2.server;
import java.net.Socket;
import java.util.LinkedList;
class CommandHandler implements Runnable {
    private Socket commandSocket;
    private LinkedList<DataXfer> backgroundList;

    synchronized void terminateXfer(int id){
    }
    synchronized void xferComplete(int id){
    }
    synchronized void addBg(DataXfer bg){
	
    }
    
    CommandHandler(Socket commandSocket){
	this.commandSocket=commandSocket;
	backgroundList=new LinkedList<>();
    }
    public void run(){
    }
}
