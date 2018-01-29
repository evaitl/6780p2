package x780p2.client;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

class Responses{
    private List<Response> responses;
    private static Responses r=new Responses();

    private Responses(){
	responses=new LinkedList<>();
    }
    
    private synchronized Response getResponse(int cid){
	while(!hasResponse(cid)){
	    try{
	    responses.wait();
	    }catch(InterruptedException e){}
	}
	Response r=null;
	Iterator<Response> iter=responses.iterator();
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
	for(Response r: responses){
	    if(r.commandId() ==cid){
		return true;
	    }
	}
	return false;
    }
    
    private synchronized void addResponse(String s){
	responses.add(new Response(s));
	responses.notifyAll();
    }
    
    static void add(String s){
	r.addResponse(s);
    }
    
    static Response get(int cid){
	return r.getResponse(cid);
    }
}
