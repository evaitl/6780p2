package x780p2.client;

import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class ListData implements DataXfer {
    private int id;
    private Socket dataSocket;
    StringBuffer sb;
    ListData(int id, Socket dataSocket){
	this.id=id;
	this.dataSocket=dataSocket;
	sb=new StringBuffer();
    }
    public int getId(){
	return id;
    }
    public void run(){
    }
    public void close(){
	try{
	    dataSocket.close();
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }
    String listData(){
	return sb.toString();
    }
};
