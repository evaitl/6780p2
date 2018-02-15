package x780p2.server;
import java.net.Socket;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.nio.file.Files;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.UncheckedIOException;
class CommandHandler implements Runnable, Closeable {
    Socket commandSocket;
    private PrintStream ps;
    Path cwd;
    synchronized void println(String s){
        ps.println(s);
    }
    CommandHandler(Socket commandSocket){
        this.commandSocket = commandSocket;
        try{
            ps = new PrintStream(commandSocket.getOutputStream());
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        cwd = Paths.get(".").normalize().toAbsolutePath();
    }
    private void doList(Command c){
        (new Thread(new ListXfer(this, c.cid, cwd))).start();
    }
    private void doStor(Command c){
        Path p = cwd.resolve(c.arg).normalize().toAbsolutePath();;

        new StorXfer(this, c.cid, p);
    }
    private void doRetr(Command c){
        Path p = cwd.resolve(c.arg).normalize().toAbsolutePath();

        new RetrXfer(this, c.cid, p);
    }
    public void run(){
        Scanner sin;

        try{
            sin = new Scanner(commandSocket.getInputStream());
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        while (sin.hasNextLine()) {
            Command c = new Command(sin.nextLine());
            switch (c.command) {
            case "mkd":
            {
                Path p = cwd.resolve(c.arg).normalize().toAbsolutePath();
                try{
                    Files.createDirectory(p);
                    println(c.cid + " 200 Directory created");
                }catch (Exception e) {
                    println(c.cid + " 500 dir not created");
                }
            }
            break;
            case "cdup":
                cwd = cwd.resolve("..").normalize().toAbsolutePath();
                println(c.cid + " 200 moved up");
                break;
            case "cwd":
            {
                Path p = cwd.resolve(c.arg).normalize().toAbsolutePath();
                if (Files.isDirectory(p)) {
                    cwd = p;
                    println(c.cid + " 200 changed directory");
                }else{
                    println(c.cid + " 500 not a directory");
                }
            }
            break;
            case "dele":
            {
                Path p = cwd.resolve(c.arg).normalize().toAbsolutePath();
                if (!Files.exists(p)) {
                    println(c.cid + " 500 file " + p + " doesn't exist?");
                }else if (Files.isDirectory(p)) {
                    println(c.cid + " 500 " + p + " is a directory");
                }else{
                    try{
                        Files.delete(p);
                    }catch (Exception e) {
                        println(c.cid + " 500 Couldn't delete for some reason");
                        break;
                    }
                    println(c.cid + " 200 deleted");
                }
            }
            break;
            case "pwd":
                println(c.cid + " 200 " + cwd);
                break;
            case "list":
                doList(c);
                break;
            case "retr":
                doRetr(c);
                break;
            case "stor":
                doStor(c);
                break;
            }
        }
    }
    public void close(){
        try{
            commandSocket.close();
        }catch (IOException e) {
        }
    }
}
