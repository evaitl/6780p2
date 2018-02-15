package x780p2.client;
class CommandId {
    private int nextInt = 1;
    private static CommandId cid = new CommandId();
    private CommandId(){
    }
    private synchronized int nextId(){
        return nextInt++;
    }
    static int next(){
        return cid.nextId();
    }
}
