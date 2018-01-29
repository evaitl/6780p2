package x780p2.client;

class FtpException extends Exception {
    FtpException(String s){
        super(s);
    }
    FtpException(String s, Throwable cause){
        super(s, cause);
    }
}
