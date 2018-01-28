package x780p2.client;
import java.io.Closeable;
interface DataXfer extends Closeable, Runnable{
    int getId();
}
