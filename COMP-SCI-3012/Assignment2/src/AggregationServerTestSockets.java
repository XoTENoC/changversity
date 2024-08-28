import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.java.utils.LamportClock;

public class AggregationServerTestSockets {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson;

    private LamportClock clock;

    public AggregationServerTestSockets(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        gson = new Gson();
        clock = new LamportClock();
    }

    public void start() throws IOException {
        System.out.println("Server is listening on port " + serverSocket.getLocalPort());

        while (true) {
            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            handleHttpRequest();
        }
    }

    private void handleHttpRequest() throws IOException {
        String line;
        String requestLine = in.readLine();
        String method = "";
        StringBuilder headers = new StringBuilder();
        int contentLength = 0;

        // Parse the request line to extract the HTTP method (GET, PUT, etc.)
        if (requestLine != null && !requestLine.isEmpty()) {
            method = requestLine.split(" ")[0];
        }

        // Read and store the headers
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            headers.append(line).append("\n");

            // Extract Content-Length header to know how much JSON data to read
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
            }
        }

        // Print the request headers (for debugging)
        System.out.println("Received Request:");
        System.out.println(requestLine);
        System.out.println(headers.toString());

        // Handle the request based on the method
        if ("PUT".equalsIgnoreCase(method)) {
            System.out.println("Received PUT request " + contentLength);
        } else if ("GET".equalsIgnoreCase(method)) {
            System.out.println("Received GET request");
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("message", "This is a response to a GET request.");
            responseJson.addProperty("status", "success");
            sendJson(responseJson);
        }
    }

    private void sendJson(JsonObject jsonObject) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + gson.toJson(jsonObject).length());
        out.println();
        out.println(gson.toJson(jsonObject));
    }

    public static void main(String[] args) {
        try {
            AggregationServerTestSockets server = new AggregationServerTestSockets(4567);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
