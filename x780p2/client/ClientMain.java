package x780p2.client;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.net.InetAddress;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;

import static java.lang.System.out;
public class ClientMain implements Runnable {
    private PrintStream termPs;
    private CommandHandler ch;

    ClientMain(Socket commandSocket, Socket termSocket) throws IOException {
        termSocket.shutdownInput();
        termPs = new PrintStream(termSocket.getOutputStream());
        ch = new CommandHandler(commandSocket);
        (new Thread(ch)).start();
    }

    synchronized void println(Response r){
        println(r.toString());
    }
    synchronized void println(String s){
        System.out.println(s);
    }
    synchronized void print(String s){
        System.out.print(s);
    }

    private Socket createSocket(String s) throws UnknownHostException,
    IOException {
        String [] split = s.split(",");
        //	for(String a: split) out.println(a);
        return new Socket(split[1], Integer.parseInt(split[2]));
    }

    private void doGet(String file, boolean bg){
        FileOutputStream fos = null;
        int commandId = CommandId.next();

        ch.println(commandId + " RETR " + file);
        Response r = Responses.get(commandId);
        if (r.result() != Response.INFO) {
            println(r);
            return;
        }
        int xid = Integer.parseInt(r.getArgs().split(",")[0]);
        Socket ds = null;
        try {
            fos = new FileOutputStream(file);
            ds = createSocket(r.getArgs());
        }catch (Exception e) {
            forceClose(fos);
            forceClose(ds);
            println(e.toString());
            return;
        }
        RetrData rd = new RetrData(this, commandId, ds, fos, xid, file);
        if (bg) {
            Xfers.add(rd);
            println("Terminate ID: " + xid);
            (new Thread(rd)).start();
        }else{
            rd.run();
        }
    }

    private void doList(){
        int commandId = CommandId.next();

        ch.println(commandId + " LIST");
        Response r = Responses.get(commandId);
        Socket s = null;
        try {
            s = createSocket(r.getArgs());
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        ListData rd = new ListData(this, commandId, s);
        rd.run();
    }

    private void forceClose(Closeable c){
        if (c == null) {
            return;
        }
        try{
            c.close();
        }catch (Exception e) {};
    }

    private void doPut(String file, boolean bg){
        FileInputStream fis = null;
        int commandId = CommandId.next();

        ch.println(commandId + " STOR " + file);
        Response r = Responses.get(commandId);
        if (r.result() != Response.INFO) {
            println(r);
            return;
        }
        Socket ds = null;
        int xid = Integer.parseInt(r.getArgs().split(",")[0]);
        try {
            fis = new FileInputStream(file);
            ds = createSocket(r.getArgs());
        }catch (Exception e) {
            println(e.toString());
            forceClose(fis);
            forceClose(ds);
            return;
        }
        StorData sd = new StorData(this, commandId, fis, ds, xid);
        if (bg) {
            Xfers.add(sd);
            println("Terminate ID: " + xid);
            (new Thread(sd)).start();
        }else{
            sd.run();
        }
    }

    /*
     */
    private void handleCommand(String command) throws FtpException {
        command = command.trim();
        boolean backgrounded = false;
        if (command.endsWith("&")) {
            backgrounded = true;
            command = command.substring(0, command.length() - 1);
        }
        String [] split = command.split("\\s+");
        int commandId;
        Response response;
        if (split.length < 1) {
            return;
        }
        switch (split[0].toLowerCase()) {
        case "cd":
            if (split.length < 2) {
                println("CD where?");
            }else{
                commandId = CommandId.next();
                if (split[1] == "..") {
                    ch.println(commandId + " CDUP");
                }else{
                    ch.println(commandId + " CWD " + split[1]);
                }
                println(Responses.get(commandId));
            }
            break;
        case "delete":
            if (split.length < 2) {
                println("Delete what?");
            }else{
                commandId = CommandId.next();
                ch.println(commandId + " DELE " + split[1]);
                println(Responses.get(commandId));
            }
            break;
        case "get":
            if (split.length < 2) {
                println("Get what?");
            }else{
                doGet(split[1], backgrounded);
            }
            break;
        case "ls":
            doList();
            break;
        case "mkdir":
            if (split.length < 2) {
                println("Delete what?");
            }else{
                commandId = CommandId.next();
                ch.println(commandId + " MKD " + split[1]);
                println(Responses.get(commandId));
            }
            break;
        case "put":
            if (split.length < 2) {
                println("Get what?");
            }else{
                doPut(split[1], backgrounded);
            }
            break;
        case "pwd":
            commandId = CommandId.next();
            ch.println(commandId + " PWD");
            println(Responses.get(commandId));
            break;
        case "quit":
            // Nothing says to wait for transfers in progress to complete.
            System.exit(0);
            break;
        case "terminate":
            println("terminating");
            if (Xfers.hasX(Integer.parseInt(split[1]))) {
                termPs.println(split[1]);
            }else{
                println("No such transfer in progress");
            }
            break;
        default:
            println("Unknown command: " + split[0]);
        }
    }

    public void run(){
        Scanner in = new Scanner(System.in);

        print("ftp# ");
        while (in.hasNextLine()) {
            try{
                handleCommand(in.nextLine());
            }catch (FtpException e) {
                println(e.toString());
            }
            print("ftp# ");
        }
    }
    public static void main(String [] args) throws UnknownHostException,
    NumberFormatException,
    IOException {
        if (args.length != 3) {
            System.out.println("usage: myftp host cport tport");
            System.exit(1);
        }
        InetAddress addr = InetAddress.getByName(args[0]);
        int cport = Integer.parseInt(args[1]);
        int tport = Integer.parseInt(args[2]);
        Socket commandSocket = new Socket(addr, cport);
        Socket termSocket = new Socket(addr, tport);
        (new ClientMain(commandSocket, termSocket)).run();
    }
}
