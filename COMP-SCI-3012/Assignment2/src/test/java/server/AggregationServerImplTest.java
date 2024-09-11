package test.java.server;

import main.java.server.AggregationServerImpl;
import main.java.utils.LamportClock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationServerImplTest {

    private AggregationServerImpl aggregationServer;

    @BeforeEach
    void setUp() throws IOException {
        aggregationServer = new AggregationServerImpl(4567);

        File file = new File("weather_data.json");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testHandleGetRequest_WithValidStationId() {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";
        aggregationServer.handlePutRequest(dummyData);

        String responseData = aggregationServer.handleGetRequest(stationId);
        assertEquals(dummyData, responseData);
    }

    @Test
    void testHandlePutRequest_NewStation() {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        int result = aggregationServer.handlePutRequest(dummyData);
        assertEquals(2, result);  // 1 indicates new entry

        String storedData = aggregationServer.handleGetRequest(stationId);
        assertEquals(dummyData, storedData);
    }

    @Test
    void testHandlePutRequest_ExistingStation() throws InterruptedException {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";
        String dummyData2 = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 14.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        aggregationServer.handlePutRequest(dummyData);
        int result = aggregationServer.handlePutRequest(dummyData2);
        assertEquals(1, result);  // 1 indicates new entry

        String storedData = aggregationServer.handleGetRequest(stationId);
        assertEquals(dummyData, storedData);
    }
}