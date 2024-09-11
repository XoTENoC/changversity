package main.java.server;

import main.java.interfaces.AggregationServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import main.java.utils.LamportClock;

public class AggregationServerImpl implements AggregationServer {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson;

    private final LamportClock clock;

    private final PriorityQueue<ClientRequest> requestQueue;

    private final Map<String, String> weatherDataStore = new HashMap<>();

    private final File dataFile = new File("weather_data.json");

    public AggregationServerImpl(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        gson = new Gson();
        clock = new LamportClock();

        requestQueue = new PriorityQueue<>(Comparator.comparingInt(ClientRequest::getLamportClock));

        loadDataFromFile();
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

    /**
     * Getting handling all the requests in the order then they are coming in.
     */
    public void handleHttpRequest() throws IOException {
        // As first line has request type store that.
        String requestLine = in.readLine();

        if (requestLine != null) {
            int clientLamportClock = 0;
            String line;

            // Ignore all lines until the Content-Length or Clock.
            while (!(line = in.readLine()).isEmpty()) {
                System.out.println("http handle: " + line);
                if (line.startsWith("Clock:")) {
                    clientLamportClock = Integer.parseInt(line.substring("Clock:".length()).trim());
                    break;
                }
            }

            clock.receiveTime(clientLamportClock);

            ClientRequest request = new ClientRequest(clientLamportClock, requestLine, out, in, clientSocket);
            requestQueue.add(request);

            processQueue();
        }
    }

    private void processQueue() throws IOException {
        while (!requestQueue.isEmpty()) {
            ClientRequest request = requestQueue.poll();

            clock.tick();

            if (request.getRequestLine().startsWith("GET")) {
                BufferedReader dataStream = request.getIn();
                String line;
                String StationID = "";

                while ((line = dataStream.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                    if (line.startsWith("StationID:")) {
                        StationID = line.substring("StationID:".length()).trim();
                    }
                }

                System.out.println("Client Request: " +StationID);

                String DataBody = handleGetRequest(StationID);

                if (DataBody.isEmpty()) {
                    request.getOut().println("HTTP/1.1 400 Bad Request");
                    request.getClientSocket().close();
                    continue;
                }

                request.getOut().println("HTTP/1.1 200 OK");
                request.getOut().println("Content-Type: application/json");
                request.getOut().println("Clock: " + clock.getTime());
                request.getOut().println("Content-Length: " + DataBody.length());
                request.getOut().println();
                request.getOut().println(DataBody);

                request.getClientSocket().close();
            }

            if (request.getRequestLine().startsWith("PUT")) {
                BufferedReader dataStream = request.getIn();
                String line;
                int contentLength = 0;

                while (!(line = dataStream.readLine()).isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                    }
                }

                // Get the body of the request based on the Content length.
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars);

                int returnCode = handlePutRequest(body);

                switch (returnCode) {
                    case -1:
                        request.getOut().println("HTTP/1.1 500 INTERNAL SERVER ERROR");
                        break;
                    case 0:
                        request.getOut().println("HTTP/1.1 204 NO CONTENT");
                        break;
                    case 1:
                        request.getOut().println("HTTP/1.1 201 CREATED");
                        break;
                    case 2:
                        request.getOut().println("HTTP/1.1 200 OK");
                        break;
                }

                request.getOut().println("Clock: " + clock.getTime());

                request.getClientSocket().close();
            }
        }
    }

    /**
     * @param stationId ID of the weather station that the client is requesting data from.
     * @return returns a string of data that will be parsed into json to get sent to the client
     */
    @Override
    public String handleGetRequest(String stationId) {
        return weatherDataStore.getOrDefault(stationId, "");
    }

    /**
     * @param jsonData input json from the content server
     * @return returns the string that will be store in a cache for data backup.
     */
    @Override
    public int handlePutRequest(String jsonData) {
        if (jsonData.isEmpty()) {
            return 0;
        }

        try {
            JsonObject weatherData = gson.fromJson(jsonData, JsonObject.class);
            String stationId = weatherData.get("id").getAsString();

            if (!weatherDataStore.containsKey(stationId)) {
                weatherDataStore.put(stationId, jsonData);
                saveDataToFile();
                return 1;
            } else {
                weatherDataStore.put(stationId, jsonData);
                saveDataToFile();
                return 2;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Cache the data so that we are able to recover from crash
     */
    private void saveDataToFile() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(weatherDataStore, writer);
        } catch (IOException e) {
            System.err.println("Error writing data: " + e.getMessage());
        }
    }

    /**
     *  Checking for the cache and loading data from cache.
     */
    private void loadDataFromFile() {
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                weatherDataStore.putAll(gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType()));
            } catch (IOException e) {
                System.err.println("Error loading data: " + e.getMessage());
            }
        }
    }

    /**
     * Removes weather data from the aggregation server if there hasn't been an update for 30 sec.
     */
    @Override
    public void expungeExpiredData() {

    }

    /**
     * Stops the aggregation server.
     */
    @Override
    public void stopServer() throws IOException {
        serverSocket.close();
    }

    /**
     * main Starting the server on port 4567 as specified in the assignment documentation
     */
    public static void main(String[] args) {
        try {
            AggregationServer server = new AggregationServerImpl(4567);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Class to store the request, so that we are able to queue the requests.
     */
    private static class ClientRequest {
        private final int lamportClock;
        private final String requestLine;
        private final PrintWriter out;
        private final BufferedReader in;
        private final Socket clientSocket;

        public ClientRequest(int lamportClock, String requestLine, PrintWriter out, BufferedReader in, Socket clientSocket) {
            this.lamportClock = lamportClock;
            this.requestLine = requestLine;
            this.out = out;
            this.in = in;
            this.clientSocket = clientSocket;
        }

        public int getLamportClock() {
            return lamportClock;
        }

        public String getRequestLine() {
            return requestLine;
        }

        public PrintWriter getOut() {
            return out;
        }

        public BufferedReader getIn() {
            return in;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }
    }
}