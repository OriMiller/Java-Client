# Java File Transfer Server Client
*This is a file transfer server built in java by Ori Miller during his independent study in computer science during his Junior year of high school*

## What is it?
This project was started during my Junior year of high school (2017) to fulfil my independent study of Computer Science. This is a two part project consiting of a server and a client. The server portion will run on the computer that you want to share folders and files from. The client portion will run on any other computer that you want to download files to.

## How does it work?
The server is given an absolute directory before startup. The server then has a thread dedicated to wait for incoming Java socket connections. Once a socket is connected, it is then handed over to a new Handler thread whose job is to handle to transfer of information between the server and the client. There is currently no limit on the amount of client that can connect to a single server. On the client side, after startup a GUI pops up asking for the IP of the server you are wanting to connect to. Once connected, the GUI then displays the directory explorer rooted in the servers sharing directory. You are then able to explore folders, return to root directory, download single files, and disconnect. When you click the disconnect button, a quit signal is sent to the server where the socket is gracefully terminated.  
