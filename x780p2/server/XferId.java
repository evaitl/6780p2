package x780p2.server;
class XferId{
    private int nextInt=1;
    private static XferId xid=new XferId();
    private XferId(){
    }
    private synchronized int nextId(){
	return nextInt++;
    }
    static int next(){
	return xid.nextId();
    }
}
