package main.java.interfaces;

public interface GETClient {
    void getWeatherData();

    void displayWeatherData(String jsonData);

    String buildMessage(String stationID);
}
