package x780p2.client;
import java.net.Socket;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import static java.lang.System.out;
class RetrData extends DataXfer {
    OutputStream os;
    InputStream is;
    String fname;
    RetrData(ClientMain cm, int id, Socket dataSocket,
             OutputStream os, int xid,
             String fname){
        super(cm, id, dataSocket, xid);
        this.os = os;
        this.fname = fname;
    }
    public void run(){
        int len;

        try{
            InputStream is = dataSocket.getInputStream();
            byte [] buffer = new byte[2000];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }catch (IOException e) {}
        close();
        Response r = Responses.get(getCid());
        if (r.result() == Response.ERROR) {
            try{
                Files.deleteIfExists(Paths.get(fname));
            }catch (IOException e) {
                out.println("Error deleting " + fname);
                out.println(e.toString());
            }
        }
        cm.println(r);
    }
    public void close(){
        super.close();
        try{
            os.close();
        }catch (IOException e) {}
    }
};
