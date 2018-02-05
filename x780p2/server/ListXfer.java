package x780p2.server;

import java.net.Socket;
import java.nio.file.Path;
import java.util.Set;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import java.io.IOException;

import static java.lang.System.out;

class ListXfer extends DataXfer{
    Path cwd;
    ListXfer(CommandHandler ch, int cid, Path cwd){
	super(ch,cid);
	this.cwd=cwd;
	out.println("listXfer: "+cid+" "+cwd);
    }

    /**
       I don't see how to get setuid, setgid, or sticky bits in
       Java. Just ignore for now.
     */
    static String posix2string(Set<PosixFilePermission> ps){
        char [] perms = "---------".toCharArray();

        if (ps.contains(PosixFilePermission.OWNER_READ)) {
            perms[0] = 'r';
        }
        if (ps.contains(PosixFilePermission.OWNER_WRITE)) {
            perms[1] = 'w';
        }
        if (ps.contains(PosixFilePermission.OWNER_EXECUTE)) {
            perms[2] = 'x';
        }

        if (ps.contains(PosixFilePermission.GROUP_READ)) {
            perms[3] = 'r';
        }
        if (ps.contains(PosixFilePermission.GROUP_WRITE)) {
            perms[4] = 'w';
        }
        if (ps.contains(PosixFilePermission.GROUP_EXECUTE)) {
            perms[5] = 'x';
        }

        if (ps.contains(PosixFilePermission.OTHERS_READ)) {
            perms[6] = 'r';
        }
        if (ps.contains(PosixFilePermission.OTHERS_WRITE)) {
            perms[7] = 'w';
        }
        if (ps.contains(PosixFilePermission.OTHERS_EXECUTE)) {
            perms[8] = 'x';
        }

        return new String(perms);
    }
    static char fType(PosixFileAttributes attrs){
        // Not standard, but I dont' want to check for pipes, fifos, sockets, devices, etc.
        char ret = '?';

        if (attrs.isSymbolicLink()) {
            ret = 'l';
        }else if (attrs.isDirectory()) {
            ret = 'd';
        }else if (attrs.isRegularFile()) {
            ret = '-';
        }else if (attrs.isOther()) {
            ret = 'o';
        }
        return ret;
    }
    static String ls(Path p) throws IOException {
        StringBuffer sb = new StringBuffer();
        // Only needs to work on Linux, so posix should be OK.
        PosixFileAttributes attrs =
            Files.getFileAttributeView(p,
                                       PosixFileAttributeView.class)
            .readAttributes();

        sb.append(fType(attrs));
        sb.append(posix2string(attrs.permissions()) + " ");
        sb.append(attrs.owner().getName() + "\t");
        sb.append(attrs.group().getName() + "\t");
        sb.append(Files.size(p) + "\t");
        sb.append(p.getName(p.getNameCount() - 1));
        sb.append('\n');
        return sb.toString();
    }
    
    public void run(){
	out.println("lx run");
	if(!accept()){
	    return;
	}
	out.println("lx accepted");
	try(DirectoryStream<Path> d = Files.newDirectoryStream(cwd)){
	    StringBuilder sb=new StringBuilder();
	    for(Path p: d){
		sb.append(ls(p));
	    }
	    dataSocket.getOutputStream().write(sb.toString().getBytes());
	    close();
	}catch(IOException e){
	    close();
	    ch.println(cid+" 500 something went worng");
	}
	ch.println(cid+" 200 All good");
    }
}
