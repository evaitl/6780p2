package x780p2.client;

class Response {
    public static final int ERROR = 500;
    public static final int OK = 200;
    public static final int INFO = 100;
    private int cid;
    private int code;
    private String args;
    Response(String s){
        String split[] = s.trim().split("\\s+");

        cid = Integer.parseInt(split[0]);
        code = Integer.parseInt(split[1]);
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < split.length; ++i) {
            sb.append(split[i]);
            sb.append(" ");
        }
        args = sb.toString().trim();
    }
    int commandId(){
        return cid;
    }
    int result(){
        return code;
    }
    String getArgs(){
        return args;
    }
    public String toString(){
        return code + ": " + args;
    }
}
