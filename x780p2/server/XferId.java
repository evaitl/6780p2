package x780p2.server;
class XferId{
    private static Object mutex=new Object();
    private static int nextInt=0;
    private XferId(){}
    static int next(){
	synchronized(mutex){
	    return ++nextInt;
	}
    }
}
