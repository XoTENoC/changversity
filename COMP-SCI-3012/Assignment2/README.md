# Assignment 2
Author: Nathaniel Chang
Class: COMP SCI 3012 (Distributed Systems)

The following is the instructions on how to run my assignment code, there are two packages in this src, there is a main and a test. The folder structure is as follows:

```text
├── src
│   ├── main
│   │   └── java
│   │       ├── client
│   │       │   └── GETClientImpl.java
│   │       ├── interfaces
│   │       │   ├── AggregationServer.java
│   │       │   ├── ContentServer.java
│   │       │   └── GETClient.java
│   │       ├── server
│   │       │   ├── AggregationServerImpl.java
│   │       │   └── ContentServerImpl.java
│   │       └── utils
│   │           └── LamportClock.java
│   └── test
│       └── java
│           ├── client
│           ├── server
│           │   └── AggregationServerImplTest.java
│           └── utils
│               └── LamportClockTest.java
└── weather_data.json
```

To the best of my knowledge at the moment I have tried to build all the unit tests within junit, but there are some that I have been unable to create. These tests will be in the `GETClient` and the `ContentServer`. These tests will mainly be of the responses from the serve.

At this point in time the bonus marks for the assignment haven't been implemented. With that the following dependencies are requires

- google.code.gson
  - error_prone_annotations-2.27.0.jar
  - json-2.11.0.jar

The other dependency is jUnit but the make file will install and pull the correct files to run all the test.

## Run Steps

The following is how to run the code within this assignment



## Testing Challenges

Throughout this assignment the hardest part was coming up with a good way to test the server. Testing the functions that return the data to the socket was simple as we are able to just assert that it is returning a string but making sure that the socket it sending the correct data is harder.

