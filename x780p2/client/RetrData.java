package x780p2.client;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.OutputStream;
import java.io.InputStream;

class RetrData extends DataXfer {
    OutputStream os;
    InputStream is;
    RetrData(ClientMain cm, int id, Socket dataSocket, OutputStream os){
	super(cm, id,dataSocket);
	this.os=os;
    }
    public void run(){
	int len;
	try{
	    InputStream is=dataSocket.getInputStream();
	    byte []buffer = new byte[2000];
	    while((len=is.read(buffer))!=-1){
		os.write(buffer,0,len);
	    }
	}catch(IOException e){}
	close();
	cm.println(Responses.get(getId()));
    }
    public void close(){
	super.close();
	try{
	    os.close();
	}catch(IOException e){}
    }
};
