package x780p2.server;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;
class PathReadWrite {
    // Disable construction.
    private PathReadWrite(){}
    
    private static class RW{
	int currentReaders;
	int currentWriters;
	LinkedList <Runnable> waitingReaders;
	LinkedList <Runnable> waitingWriters;
	RW(){
	    waitingReaders=new LinkedList<>();
	    waitingWriters=new LinkedList<>();
	}
    }
    private static Object mutex = new Object();;
    private static Map<Path, RW > clients = new HashMap<>();

    /**
       A read request. Kick off a thread, or queue the request. 
    */
    static void read(Path p, Runnable r){
	synchronized(mutex){
	    RW rw=clients.get(p);
	    if(rw==null){
		rw = new RW();
		clients.put(p,rw);
	    }
	    if(rw.currentWriters==0){
		rw.currentReaders++;
		(new Thread(r)).start();
	    }else{
		rw.waitingReaders.add(r);
	    }
	}
    }
    /**
       A write request. Kick off or queue the request. 
    */
    static void write(Path p, Runnable r){
	synchronized(mutex){
	    RW rw=clients.get(p);
	    if(rw==null){
		rw = new RW();
		clients.put(p,rw);
	    }
	    if(rw.currentWriters==0 && rw.currentReaders==0){
		(new Thread(r)).start();
		rw.currentWriters++;
	    }else{
		rw.waitingWriters.add(r);
	    }
	}
    }

    /**
       A reader finished. Kick of a writer or remove entry. 
    */
    static void readDone(Path p){
	synchronized(mutex){
	    RW rw=clients.get(p);
	    if(rw==null || rw.currentReaders<=0){
		throw new IllegalStateException();
	    }
	    rw.currentReaders--;
	    if(rw.waitingWriters.isEmpty()){
		if(rw.currentReaders==0){
		    clients.remove(p);
		}
	    }else{
		(new Thread(rw.waitingWriters.poll())).start();
		rw.currentWriters++;
	    }
	}
    }
    /**
       A writer is done. Kick off any waiting readers, or a writer, or remove entry. 
    */
    static void writeDone(Path p){
	synchronized(mutex){
	    RW rw=clients.get(p);
	    if(rw==null || rw.currentWriters!=1){
		throw new IllegalStateException();
	    }
	    rw.currentWriters--;
	    while(!rw.waitingReaders.isEmpty()){
		(new Thread(rw.waitingReaders.poll())).start();
		rw.currentReaders++;
	    }
	    if(rw.currentReaders!=0){
		return;
	    }
	    if(rw.waitingWriters.isEmpty()){
		clients.remove(p);
	    }else{
		(new Thread(rw.waitingWriters.poll())).start();
		rw.currentWriters++;
	    }
	}
    }    
}
