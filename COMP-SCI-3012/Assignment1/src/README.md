# Assignment 1
## Nathaniel Chang (a1821595) Distributed Systems

**Note separate stacks for each client was implemented**

The following are the instructions for compiling and running assignment 1, These instructions are expecting that you have java runtime, javac, and curl installed and accesible at `java`, `javac`, and `curl` respectively. This is also assuming that you are running on a unix based machine.

## Compile and test.

The Following code is tested with junit. to compile and test the code please run the following command.

```Bash
make all
```

This command is going to download the junit libraries that are required for testing. It will put them in the folder /lib. This command will also compile all the java classes for the server and the clients.

This command will also run all the test at the same time.

## Running the Server and Single Client

Next is going to require that you have 2 terminals open, and in the same directory as the code.

Terminal 1 run the following command to start the server.
```Bash
make server
```
Please see that the server prints `Server Ready` to the terminal

in Terminal 2 run the following command
```Bash
make Client
```

Notice that after a second it will output 30.

## Running the Server and 2 Clients

The server should still be running from before. leave that running.

open a third terminal and navigate to the same directory.

rerun the client command form the last before clicking enter navigate to the third terminal and input the following command.

```Bash
make client2
```

When you are ready hit enter on the two terminals relatively quickly. after about a second each should return and Output value client 1 should return the same as before 30. client 2 should return 35. This is showing the two different stack one for each of the clients.
