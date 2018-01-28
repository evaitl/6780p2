package x780p2.server;
import java.io.Closeable;

interface DataXfer extends Runnable, Closeable{
    int getId();
}
