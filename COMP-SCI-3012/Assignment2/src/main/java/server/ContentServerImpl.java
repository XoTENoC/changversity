package main.java.server;

import main.java.interfaces.ContentServer;
import main.java.utils.LamportClock;

import java.io.*;
import java.net.Socket;

public class ContentServerImpl implements ContentServer {

    private final String serverHost;
    private final int serverPort;

    private final LamportClock clock;

    private final File dataFile = new File("weather_data_content.json");

    public ContentServerImpl(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        clock = new LamportClock();
    }

    @Override
    public void setContent() {
        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            clock.tick();

            String weatherData = getWeatherData();

            String request = buildRequest(weatherData);

            out.print(request);
            out.flush();

            String responseLine;

            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                if (responseLine.startsWith("HTTP")) {
                    System.out.println(responseLine);
                }

                if (responseLine.startsWith("Clock:")) {
                    clock.receiveTime(Integer.parseInt(responseLine.split(":")[1].trim()));
                    System.out.println(responseLine);
                }
            }

        } catch (IOException e) {
            System.err.println("Can't connect to server");
            e.printStackTrace();
        }
    }

    @Override
    public String getWeatherData() {
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile); BufferedReader br = new BufferedReader(reader)) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonContent.append(line);
                }
                String weatherData = jsonContent.toString();

                System.out.println("JSON Content: " + weatherData);

                return weatherData;
            } catch (IOException e) {
                System.err.println("Error loading data: " + e.getMessage());
            }
        }

        return "";
    }

    public String buildRequest(String data) {
        return "PUT / HTTP/1.1\r\n" +
                "Clock: " + clock.getTime() + "\r\n" +
                "Content-Length: " + data.length() + "\r\n" +
                "\r\n" +
                data;
    }

    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 4567;

        ContentServer contentServer = new ContentServerImpl(serverHost, serverPort);
        contentServer.setContent();
    }
}
