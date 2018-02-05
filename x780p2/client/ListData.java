package x780p2.client;

import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;

class ListData extends DataXfer {
    StringBuffer sb;
    ListData(ClientMain cm, int id, Socket dataSocket){
	super(cm,id,dataSocket);
	sb=new StringBuffer();
    }
    public void run(){
	try{
	    InputStream is=dataSocket.getInputStream();
	    int len;
	    byte []buffer=new byte[2048];
	    while((len=is.read(buffer))!=-1){
		sb.append(new String(buffer,0,len));
	    }
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
	close();
	cm.println(sb.toString());
	cm.println(Responses.get(getCid()));
    }
};
