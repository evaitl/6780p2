package x780p2.client;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import static java.lang.System.out;
class Responses{
    private List<Response> responseList;
    private static Responses sr=new Responses();
    
    private Responses(){
	out.println("Creating responses");
	responseList=new LinkedList<>();
    }
    
    private synchronized Response getResponse(int cid){
	while(!hasResponse(cid)){
	    try{
		wait();
	    }catch(InterruptedException e){}
	}
	Response r=null;
	Iterator<Response> iter=responseList.iterator();
	while(iter.hasNext()){
	    r=iter.next();
	    if(r.commandId()==cid){
		iter.remove();
		    return r;
	    }	
	}
	throw new RuntimeException("Shouldn't be here");
    }
    
    private synchronized boolean hasResponse(int cid){
	for(Response r: responseList){
	    if(r.commandId() ==cid){
		return true;
	    }
	}
	return false;
    }
    
    private synchronized void addResponse(String s){
	responseList.add(new Response(s));
	sr.notifyAll();
    }
    
    static void add(String s){
	out.println("add response "+s);
	sr.addResponse(s);
	out.println("add done");
    }
    
    static Response get(int cid){
	out.println("get response "+cid);
	return sr.getResponse(cid);
    }
}
