package x780p2.client;

import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;

class ListData extends DataXfer {
    StringBuffer sb;
    ListData(int id, Socket dataSocket){
	super(id,dataSocket);
	sb=new StringBuffer();
    }
    public void run(){
    }
    String listData(){
	return sb.toString();
    }
};
