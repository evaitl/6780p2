package x780p2.server;
import java.util.LinkedList;
class Xfers {
    private static Object mutex=new Object();
    private static LinkedList<DataXfer> xfers = new LinkedList<>();
    static void terminate(int xid){
	synchronized(mutex){
	    for(DataXfer dx: xfers){
		if(dx.getXid()==xid){
		    dx.terminate();
		}
	    }
	}
    }
    static void register(DataXfer dx){
	synchronized(mutex){
	    xfers.add(dx);
	}
    }
    static void done(DataXfer dx){
	synchronized(mutex){
	    xfers.remove(dx);
	}
    }
}
