package x780p2.client;

import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.io.OutputStream;

class StorData extends DataXfer{
    InputStream is;
    StorData(ClientMain cm, int id, InputStream is, Socket dataSocket){
	super(cm,id,dataSocket);
	this.is=is;
    }
    public void run() {
	int len;
	try{
	    OutputStream os=dataSocket.getOutputStream();
	    byte [] buffer=new byte[2000];
	    while((len=is.read(buffer))!=-1){
		os.write(buffer,0,len);
	    }
	}catch(IOException e){}
	close();
	cm.println(Responses.get(getId()));
    }
}
