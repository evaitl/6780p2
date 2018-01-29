package x780p2.client;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

class Xfers{
    private List<DataXfer> xfers;
    private static Xfers x=new Xfers();
    private Xfers(){
	xfers=new LinkedList<>();
    }
    private synchronized DataXfer getXfer(int cid, boolean shouldWait){
	if(!shouldWait && !hasXfer(cid)){
	    return null;
	}
	while(!hasXfer(cid)){
	    try{
		wait();
	    }catch(InterruptedException e){}
	}
	Iterator<DataXfer> iter=xfers.iterator();
	while(iter.hasNext()){
	    DataXfer d=iter.next();
	    if(d.getId()==cid){
		iter.remove();
		return d;
	    }
	}
	throw new RuntimeException("How'd i get here?");
    }
    
    private synchronized boolean hasXfer(int cid){
	for(DataXfer d: xfers){
	    if(d.getId()==cid){
		return true;
	    }
	}
	return false;
    }
    
    private synchronized void addXfer(DataXfer xfer){
	xfers.add(xfer);
	notifyAll();
    }

    static boolean has(int cid){
	return x.hasXfer(cid);
    }
    
    static DataXfer get(int cid, boolean shouldWait){
	return x.getXfer(cid,shouldWait);
    }
    
    static void add(DataXfer xfer){
	x.addXfer(xfer);
    }
}
