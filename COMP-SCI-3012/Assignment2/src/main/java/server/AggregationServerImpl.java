package main.java.server;

import main.java.interfaces.AggregationServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;

import main.java.utils.LamportClock;

public class AggregationServerImpl implements AggregationServer {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson;

    private LamportClock clock;

    public AggregationServerImpl(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        gson = new Gson();
        clock = new LamportClock();
    }

    /**
     * Starts Aggregation server.
     */
    @Override
    public void startServer() throws IOException {
        System.out.println("Server is listening on port " + serverSocket.getLocalPort());

        while (true) {
            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            handleHttpRequest();
        }
    }

    public void handleHttpRequest() throws IOException {
        System.out.println("Testing");
    }

    /**
     * @param stationId ID of the weather station that the client is requesting data from.
     * @return returns a string of data that will be parsed into json to get sent to the client
     */
    @Override
    public String handleGetRequest(String stationId) {
        return "";
    }

    /**
     * @param jsonData input json from the content server
     * @return returns the string that will be store in a cache for data backup.
     */
    @Override
    public int handlePutRequest(String jsonData) {
        return 0;
    }

    /**
     * Removes that weather data from the aggregation server if there hasn't been an update for 30 sec.
     */
    @Override
    public void expungeExpiredData() {

    }

    /**
     *  Stops the aggregation server.
     */
    @Override
    public void stopServer() {

    }

    public static void main(String[] args) {
        try {
            AggregationServer server = new AggregationServerImpl(4567);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
