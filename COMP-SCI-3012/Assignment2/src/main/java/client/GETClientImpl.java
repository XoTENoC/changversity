package main.java.client;

import main.java.interfaces.GETClient;

import java.io.*;
import java.net.Socket;

import main.java.utils.LamportClock;

public class GETClientImpl implements GETClient {

    private final String serverHost;
    private final int serverPort;
    private final String stationId;

    private final LamportClock clock;

    /**
     * @param serverHost host that the server is located at
     * @param serverPort port on which we are able to access the server
     * @param stationId Stations ID that we want to get the information
     */
    public GETClientImpl(String serverHost, int serverPort, String stationId) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.stationId = stationId;

        clock = new LamportClock();
    }

    /**
     * Creating, sending the pricing the request and its return.
     */
    @Override
    public void getWeatherData() {
        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            clock.tick();

            out.print(buildMessage(stationId));
            out.flush();

            String responseLine;
            StringBuilder responseBody = new StringBuilder();
            int contentLength = -1;

            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                if (responseLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(responseLine.split(":")[1].trim());
                }

                if (responseLine.startsWith("Clock:")) {
                    clock.receiveTime(Integer.parseInt(responseLine.split(":")[1].trim()));
                }
            }

            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                int bytesRead = in.read(buffer, 0, contentLength);
                responseBody.append(buffer, 0, bytesRead);
            }

            displayWeatherData(responseBody.toString());

        } catch (IOException e) {
            System.err.println("Error: Unable to connect to the server.");
            e.printStackTrace();
        }
    }

    /**
     * Printing the data that we are receiving from the agg server
     * @param jsonData Data that is returned from the request
     */
    @Override
    public void displayWeatherData(String jsonData) {
        System.out.println("Weather data from server: ");
        System.out.println(jsonData);
    }

    /**
     * Building the request that we are going to send to the server
     * @param stationID station that we want to get the information about
     * @return HTTP request in string format.
     */
    @Override
    public String buildMessage(String stationID) {
        return "GET / HTTP/1.1\r\n" +
                "Clock: " + clock.getTime() + "\r\n" +
                "StationID: " + stationId + "\r\n" +
                "\r\n";
    }

    /**
     * Stating an instance of the server through the terminal.
     * @param args input arguments if applicable.
     */
    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 4567;
        String stationId = "IDS60901";

        GETClient client = new GETClientImpl(serverHost, serverPort, stationId);
        client.getWeatherData();
    }
}
