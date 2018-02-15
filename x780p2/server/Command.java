package x780p2.server;

class Command {
    String command;
    int cid;
    String arg;
    Command(String line){
        String [] split = line.trim().split("\\s+");
        cid = Integer.parseInt(split[0]);
        command = split[1].toLowerCase();
        if (split.length > 2) {
            arg = split[2];
        }
    }
    public String toString(){
        return String.format("Command: [%s %d %s]", command, cid, arg);
    }
}
