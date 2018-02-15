package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.io.FileInputStream;

class RetrXfer extends DataXfer {
    private InputStream is = null;
    private boolean closed = false;
    RetrXfer(CommandHandler ch,
             int cid, Path path){
        super(ch, cid, path);
        PathReadWrite.read(path, this);
    }
    public void run(){
        try{
            is = new FileInputStream(path.toFile());
        }catch (IOException e) {
            ch.println(getCid() + " 500 RETR can't open file" + path);
            close();
            return;
        }
        if (!accept()) {
            return;
        }
        try{
            OutputStream os = dataSocket.getOutputStream();
            byte [] buffer = new byte[1000];
            int len;
            while (!terminated && ((len = is.read(buffer)) != -1)) {
                os.write(buffer, 0, len);
            }
        }catch (IOException e) {
            if (!terminated) {
                throw new UncheckedIOException(e);
            }
        }finally{
            close();
        }
        if (terminated) {
            ch.println(getCid() + " 500 RETR " + getXid() + " terminated");
        }else{
            ch.println(getCid() + " 200 RETR completed");
        }
    }
    public void close(){
        if (closed) {
            return;
        }
        closed = true;
        super.close();
        PathReadWrite.readDone(path);
        try{
            if (is != null) {
                is.close();
                is = null;
            }
        }catch (IOException e) {
            // Ignore close errors
        }
    }
}
