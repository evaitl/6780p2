package x780p2.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileOutputStream;
class StorXfer extends DataXfer {
    private OutputStream os = null;
    private boolean closed = false;
    StorXfer(CommandHandler ch,
             int cid,
             Path path){
        super(ch, cid, path);
        PathReadWrite.write(path, this);
    }
    public void run(){
        if (!accept()) {
            return;
        }
        try{
            os = new FileOutputStream(path.toFile());
        }catch (IOException e) {
            ch.println(getCid() + " 500 STOR can't open file " + path);
            close();
            return;
        }
        try{
            InputStream is = null;
            byte [] buffer = new byte[1000];
            int len;
            if (!terminated) {
                is = dataSocket.getInputStream();
            }
            while (!terminated && (len = is.read(buffer)) != -1) {
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
            ch.println(getCid() + " 500 STOR " + getXid() + " terminated");
            try{
                Files.deleteIfExists(path);
            }catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }else{
            ch.println(getCid() + " 200 STOR completed");
        }
    }
    public void close(){
        if (closed) {
            return;
        }
        closed = true;
        super.close();
        PathReadWrite.writeDone(path);
        try{
            if (os != null) {
                os.close();
                os = null;
            }
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
