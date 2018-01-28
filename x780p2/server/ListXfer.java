package x780pe.server;
import x780p2.DataXfer;
import java.net.Socket;
class ListXfer extends DataXfer{
    ListXfer(int id, Socket dataSocket){
	super(id,dataSocket);
    }
    public void run(){
    }
}
