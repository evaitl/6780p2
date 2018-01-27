class ServerRetrHandler implements Runnable{
    ServerSocket pasvListener;
    Socket dataSocket;
    int commandId;
    private bool terminate=false;
    ServerRetrHandler(ServerSocket pasvListener,
		      int commandId,
		      OutputStream os){
	this.commandId=commandId;
	this.pasvListener=pasvListener;
    }

    void terminateTransfer(){
	terminate=true;
    }
    public void run(){
	Socket dataSocket=
    }
}
