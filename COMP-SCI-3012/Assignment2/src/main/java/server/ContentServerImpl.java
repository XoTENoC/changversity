package main.java.server;

import main.java.interfaces.ContentServer;
import main.java.utils.LamportClock;

import java.io.*;
import java.net.Socket;

public class ContentServerImpl implements ContentServer {

    private final String serverHost;
    private final int serverPort;

    private final LamportClock clock;

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

            String request = "PUT / HTTP/1.1\r\n" +
                    "Clock: " + clock.getTime() + "\r\n" +
                    "Content-Length: " + weatherData.length() + "\r\n" +
                    "\r\n" +
                    weatherData;

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
            System.err.println("Error: Unable to connect to the server.");
            e.printStackTrace();
        }
    }

    @Override
    public String getWeatherData() {
        String weatherData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";
        return weatherData;
    }

    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 4567;

        ContentServer contentServer = new ContentServerImpl(serverHost, serverPort);
        contentServer.setContent();
    }
}
