Introduction
============

The code here is a solution to CSCI 6780 [Programming Project
2](./docs/Programming-Project2.pdf). This project builds on
[Programming Project 1](./docs/Programming-Project1.pdf).

While this file is human readable, it is best to view it as a
processed markdown file on the github site
[here](https://github.com/evaitl/6780p2).

Build/Run
=========


Copy the tree here to the client and server machines. On both machines
type `make`.  Start the server before using a client. 


On the server type

    ./myftpserver <sport> <tport> 

On the client type:

    ./myftp host <sport> <tport>


Design Notes
============


## Protocol


For Project 1 we pretty much followed the FTP protocol, with some
minor exceptions.  The requirements for project 2 break any
possibility with interoperation with FTP, so I'll feel free with
modifying the protocol.

Project 2 has actions running in multiple threads and can return
responses out of order. In order to match commands and responses,
every command will start with an integer CommandId.  The corresponding
responses start with the same CommandId.

Commands accepted on command socket are: MKD, CDUP, CWD, DELE,
PWD, LIST, RETR, STOR.

Format for a command is:

    CommandID Command Arguments

Every command is terminated with a new line (\n).

Responses are all preceded by the CommandId and terminated with a newline: 

   CommandId ResponseCode  Other stuff

We aren't really using the full response codes of the ftp protocol, so
we'll just use the numbers 100, 200, and 500. 100 is the intermediate
response for LIST, RETR, and STOR. 200 is a positive command
completion. 500 is a negative command completion.

There is a requirement that the server send back an ID when there is a
get or a put. In this code, we call this a transferID. TransferIDs
apply to LIST, RETR, and STOR commands. 

LIST has no arguments. RETR and STOR have a filename argument.
Response is normally a commandId, 100, followed by an the transferID,
IP address and port:

	  75 100 22,127.0.0.1,5467

If something goes wrong however (file can't be opened, socket failure,
etcetera) the response code for a RETR, STOR, or LIST would be a 500.

The transfer ID, IP address and port are seperated with commas in
case the IP address is an IPv6 address. For IPv6 a response may look
like:

          7 100 6,2001:0db8:85a3:0000:0000:8a2e:0370:7334,5467

If (for instance) the file can't be opened, we will get a 500 response:

   82 500 Can't open file. 

On completion we will get a 200 or a 500 response.

   14 200 Xfer OK

Or
   25 500 Xfer terminated. 

## Client design notes:

This project seems about threads, so I'm sure we aren't allowed to use
a reactive system with callbacks (poll/epoll/select). 

We aren't allowed to use thread-safe data structures. I'm assuming
that means that we can't use `java.util.concurrent.Concurrent\*`, but
we can still use the `synchronized` keyword and the monitors built
into every Object.

Because some commands responses can happen out of order, we need one
thread on the user interface (ClientMain), and one on the
commandSocket ClientCommandHandler.  For get/put (RETR/STOR), we may
need to kick off threads on temporary data sockets. The terminate
socket is just written to and we will assume that the buffer doesn't
fill up enough to block. So:

<pre>
   ClientMain implements Runnable   
      Owns user interface fd.
      Runs commands from the user.
      Has synchronized println() so other tasks can print
      results to the user.
      
   CommandHandler implements Runnable, Closeable   
       Owns commandSocket - which has commands and responses to the server.
       synchronized println() for commands.
       Responses are stashed away and indexed by the commandId.


   Responses is the data structure that stores responses. The
   responses are fetched and removed from the by
   commandId. Synchronized methods are used for fetching. A
   notifyAll() is used when a new response is added to wake up anyone
   waiting for the response.
   

   abstract class  DataXfer implements Closeable, Runnable
      Owns dataSocket used for RETR, STOR, LIST. 
        int  getCid()
	int getXid()

   class RetrData implements DataXfer
      Reads data from dataSocket and passes it to a
      FileOutputStream. 

   class StorData implements DataXfer
      Reads data from a FileInputStream and passes it to the dataSocket.
      
   class ListData implements DataXfer
      Reads data from data socket and passes it to the user interface. 

   Xfers is the data structure that keeps any outstanding DataXfer
   tasks. Indexable by either transferID or commandID. Protected
   by synchronized methods. 

</pre>

On a terminate command, ClientMain checks the outstanding command
list. If found sends the teminate message. If this was a get, then
`java.io.file.Files.deleteIfExists()` the target file.  If commandId
not in outstanding command list, just tell the user.

A terminate message goes out on the terminate socket. The server will
call terminate() for its side DataXfer task. This will cause the
server side task to close the dataSocket and send a failure
message. The client side DataXfer task will echo the failure message
to the user. On a get, the client side will delete the partially
transferred file.  On a put, the server side will delete the partially
transferred file.

If a get/put/list is done without the '&', ClientMain sends the
command, creates the DataXfer class, and calls `DataXfer.run()`
directly. If a get/put is done with the '&', ClientMain uses
`Thread.start()` to kick off the DataXfer `run()` method.

## Server side notes:

There are multiple threads in the server, each is to have its own
current working directory. As a process only has one current
directory, I'm storing a simulated current directory as a path in the
CommandHandler.

Apparently paths in commands (get/put/delete/cwd/mkd) can be absolute
or relative. If the path starts with "/", I'm assuming it to be
absolute, else it is relative to the current working directory.

There are several sockets to keep track of:

* serverSocket - one per process, accept()
* terminateServerServer - one per process, accept()
* terminateSocket - one per client returned 
* commandSocket - one per client. Returned by serverSocket.accept()
* dataServerSocket - Created, accept() once, and closed per RETR/STOR/LIST
* dataXferSocket - used once per RETR/STOR/LIST. Returned from
  dataServerSocket.accept.

We aren't supposed to do reactive programming here, so threads it
is. The classes:

<pre>
    ServerMain
       Kicks off a TerminateHandler thread.
       Owns serverSocket. Does serverSocket.accept(), kicks off
       new CommandHandlers every time accept() returns. 

    TerminateServer implements Runnable
       Owns the terminate server socket. Kicks off a TerminateHandler
       every time the terminate server socket returns from accept().

    TerminateHandler implements Runnable, Closeable
       Owns a terminate data socket. On any terminate command,
       looks up the appropriate task and calls its terminate() method. 



    CommandHandler implements Runnable
       Owns commandSocket. Kicks off commands as they come in.
       synchronized println() method for responses from other tasks.
       Small commands are done inline. LIST, STOR, RETR are kicked off
       in their own threads. 
       
    abstract class DataXfer extends Runnable, Closeable
      Common functions for data transfer. Creates server socket
      and sends the 100 response with the transferID, IP address,
      and port number.
      accept() on the data server socket. Closes the server socket
      after the first connection. The data socket is then
      used for the specific transfer task. 
    
    ListXfer extends DataXfer
      Sends list data over the dataSocket. Sends a 200 response when
      done if there were no problems, else closes dataSocket and sends
      a 500 response. 

    RetrXfer extends DataXfer
      Sends file data over dataSocket. On a terminate, cancels early.
      Sends 200 response if file transferred with no problems, else
      a 500 response. 
      
    StorXfer extends DataXfer
      Recieves file data over dataSocket. On a terminate, cancels early.
      Sends 200 response if file transferred with no problems, else
      a 500 response. On a 500 response also deletes the file. 
      

</pre>

Sequence Diagrams:
==================

A couple of sequence diagrams to clarify operation and protocol:

This picture simplifies things a bit. There are actually a
commandServerSocket and terminateServerSocket that are accept()ing.
The result of the 3-way accept handshake from one of these is the
connection between the client and server data and terminate sockets.

![Terminated RETR](docs/retr_term.png)



File Synchronization
====================

The correct way to synchronize reads and writes of files on a Linux
box is probably to do writes to temp files created with `mkstemp(3)`
and then `rename(2)` when the complete file is written. Reads and
writes could happen as they come in with no stepping on each other and
the files read would always be complete files because once a reader
opens a file and gets an fd, replacing the file with a rename() won't
change the data the fd is attached to.

However, I gather though from class discussions that we are supposed
to do file synchronization the hard way, by solving the
readers/writers problem. Note that this way will also leave partial
files in place if a put is terminated before completion.  

So... We create a PathReadWrite class, that allows clients to request
to read/write files by Path.  Multiple readers are allowed for each
Path, but a writer has exclusive access. Once a requester is granted
access, its run() method is kicked off. The requester needs to notify
PathReadWrite when it is done with reading/writing Path.

PathReadWrite creates a static Object as a mutex. This object is just
used for the synchronized statements that protect the multi-thread
integrity of a HashMap.  We could have just synchronized on the hash
map, or used non-static synchronized methods, but I figured that this
design was closest to the spirit of doing the same program in C/C++
with a pthread_mutex_t.

This is broken by design, un-necessarily complicated, and not what I
would do in a commercial program.

Questions:
==========




Disclaimer
==========

This project was done in its entirety by Eric Vaitl and Ankita
Joshi. We hereby state that we have not received unauthorized help of
any form.

