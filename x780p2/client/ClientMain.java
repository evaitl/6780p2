package x780p2.client;

import java.net.Socket;
import java.util.LinkedList;

public class ClientMain implements Runnable{
    private Socket termSocket;
    private LinkedList<Integer> backgroundList;
    ClientMain(){
    }
    /**
       Places a DataXfer on the backgroundList;
     */
    synchronized void transferStart(DataXfer bg){
    }
    /**
       Remove a DataXfer from the backgroundList;
     */
    synchronized void transferComplete(int id){
    }
    /**
       @return true if a DataXfer.getId()==id on backgroundList
     */
    synchronized boolean currentXfer(int id){
	return false;
    }
    void commandResponse(int id, int code, String resp){
    }
    public void run(){
    }
    public static void main(String [] args){
    }
}
