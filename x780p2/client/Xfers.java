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
	    if(d.getCid()==cid){
		iter.remove();
		return d;
	    }
	}
	throw new RuntimeException("How'd i get here?");
    }
    
    private synchronized boolean hasXfer(int cid){
	for(DataXfer d: xfers){
	    if(d.getCid()==cid){
		return true;
	    }
	}
	return false;
    }

    private synchronized boolean hasXid(int xid){
	for(DataXfer d: xfers){
	    if(d.getXid()==xid){
		return true;
	    }
	}
	return false;
    }
    private synchronized void addXfer(DataXfer xfer){
	xfers.add(xfer);
	notifyAll();
    }
    static boolean hasX(int xid){
	return x.hasXid(xid);
    }
    static void add(DataXfer xfer){
	x.addXfer(xfer);
    }
    static void del(int cid, boolean shouldWait){
	x.getXfer(cid,shouldWait);
    }
}
