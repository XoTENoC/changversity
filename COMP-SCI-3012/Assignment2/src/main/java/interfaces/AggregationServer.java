package main.java.interfaces;

import main.java.utils.LamportClock;

import java.io.IOException;

public interface AggregationServer {
    void startServer() throws IOException;

    void handleHttpRequest() throws IOException;

    String handleGetRequest(String stationId);

    int handlePutRequest(String jsonData);

    void expungeExpiredData();

    void stopServer();
}
