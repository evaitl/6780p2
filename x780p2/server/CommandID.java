package x780p2;

/**
   A singleton with a single synchronized getNext() to return the next
   command id in a multi-threaded environment.
 */
class CommandID{
    private static int nextId=1000;
    private static CommandID obj=new CommandID();
    private CommandID(){
    }
    private synchronized int getNext(){
	return nextId++;
    }
    static int nextId(){
	return obj.getNext();
    }
}
