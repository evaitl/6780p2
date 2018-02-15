package x780p2.client;

import java.util.LinkedList;
import java.util.Iterator;

class Xfers {
    private static LinkedList<Integer> xfers=new LinkedList<>();
    private static Object mutex = new Object();
    private Xfers(){  }
    static boolean hasX(Integer xid){
	synchronized(mutex){
	    for(Integer dx: xfers){
		if(dx.equals(xid)){
		    return true;
		}
	    }
	}
	return false;
    }
    static void add(Integer xid){
	synchronized(mutex){
	    xfers.add(xid);
	}
    }
    static void del(Integer xid){
	synchronized(mutex){
	    Iterator<Integer> iter = xfers.iterator();
	    while (iter.hasNext()) {
		Integer dx = iter.next();
		if (dx.equals(xid)) {
		    iter.remove();
		}
	    }
	}
    }
}
