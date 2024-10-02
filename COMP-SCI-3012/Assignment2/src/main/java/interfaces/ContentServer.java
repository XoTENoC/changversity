package main.java.interfaces;

public interface ContentServer {
    void setContent();
    String getWeatherData();
    String buildRequest(String data);
}
