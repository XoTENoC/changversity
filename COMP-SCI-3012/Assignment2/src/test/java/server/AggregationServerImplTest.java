package test.java.server;

import main.java.server.AggregationServerImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class AggregationServerImplTest {

    private AggregationServerImpl aggregationServer;
    private Thread serverThread;

    /**
     * The following is to make sure that we aren't loading and previous data from a test to make sure that we
     * are starting from a clean state.
     */
    @BeforeEach
    void setUp() throws IOException {
        File file = new File("weather_data.json");
        if (file.exists()) {
            file.delete();
        }

        aggregationServer = new AggregationServerImpl(4567);
    }

    /**
     * This after function is to make sure that if there agg server was started on the main thread that it is closed
     * and removes any files created by the aggregation server.
     * @throws IOException
     */
    @AfterEach
    void tearDown() throws IOException {
        // Perform cleanup or reset operations
        File file = new File("weather_data.json");
        if (file.exists()) {
            file.delete();
        }

        // Shut down the aggregation server
        aggregationServer.stopServer();
    }

    /**
     * Starts the Agg server on a separate thread so that tests are able to be run on the socket to verify that it is
     * working as expected.
     * @throws IOException
     * @throws InterruptedException
     */
    void startServer() throws IOException, InterruptedException {
        serverThread = new Thread(() -> {
            try {
                aggregationServer.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();

        Thread.sleep(500);
    }

    /**
     * Stops the server on the separate thread and closes that thread once agg server is no longer needed.
     * @throws InterruptedException
     * @throws IOException
     */
    void stopServer() throws InterruptedException, IOException {
        aggregationServer.stopServer();
        serverThread.interrupt();
        serverThread.join();
    }

    /**
     * Test to make sure the Get is working when there is a valid ID outside of the socket calls
     */
    @Test
    void testHandleGetRequest_WithValidStationId() {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";
        aggregationServer.handlePutRequest(dummyData);

        String responseData = aggregationServer.handleGetRequest(stationId);
        assertEquals(dummyData, responseData);
    }

    /**
     * Test to make sure PUT with a new station is working outside of socket calls
     */
    @Test
    void testHandlePutRequest_NewStation() {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        int result = aggregationServer.handlePutRequest(dummyData);
        assertEquals(1, result);  // 1 indicates new entry

        String storedData = aggregationServer.handleGetRequest(stationId);
        assertEquals(dummyData, storedData);
    }

    /**
     * Test to make sure that PUT is return expected outside the socket calls
     */
    @Test
    void testHandlePutRequest_noStation() {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        int result = aggregationServer.handlePutRequest(dummyData);
        assertEquals(1, result);  // 1 indicates new entry

        String storedData = aggregationServer.handleGetRequest("");
        assertNull(storedData);
    }

    /**
     * Test to make sure that PUT with existing station is working outside of socket calls
     * @throws InterruptedException
     */
    @Test
    void testHandlePutRequest_ExistingStation() throws InterruptedException {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        aggregationServer.handlePutRequest(dummyData);
        int result = aggregationServer.handlePutRequest(dummyData);
        assertEquals(2, result);  // 1 indicates new entry

        String storedData = aggregationServer.handleGetRequest(stationId);
        assertEquals(dummyData, storedData);
    }

    /**
     * Test to make sure that PUT with no conect is getting expected response outside of socket calls
     * @throws InterruptedException
     */
    @Test
    void testHandlePutRequest_NoContent() throws InterruptedException {
        String stationId = "IDS60901";
        String dummyData = "";

        int result = aggregationServer.handlePutRequest(dummyData);
        assertEquals(0, result);  // 1 indicates new entry

        String storedData = aggregationServer.handleGetRequest(stationId);
        assertNull(storedData);
    }

    /**
     * Test to make sure that Put with invalid data is getting Expected server response
     * @throws InterruptedException
     */
    @Test
    void testHandlePutRequest_ServerError() throws InterruptedException {
        String stationId = "IDS60901";
        String dummyData = "{\"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        int result = aggregationServer.handlePutRequest(dummyData);
        assertEquals(-1, result);  // 1 indicates new entry
    }

    /**
     * Test to make sure that PUT with existing station is working outside of socket calls
     * @throws InterruptedException
     */
    @Test
    void testExpungeExpiredData() throws InterruptedException {
        String stationId = "IDS60901";
        String dummyData = "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";

        aggregationServer.handlePutRequest(dummyData);

        Thread.sleep(30000);

        String storedData = aggregationServer.handleGetRequest(stationId);
        assertNull(storedData);
    }

    /**
     * The following function tests that the socket is able to send and receive.
     * As all the other functions are working we are able to just test that the socket is sending and receiving.
     * Also verify that the correct Responses are sent and received as expected.*
     * @throws Exception
     */
    @Test
    void testSocketCommunication() throws Exception {
        startServer();

        // Following are tests for PUT request
        testSocketPut("HTTP/1.1 201 CREATED", getJsonData());
        testSocketPut("HTTP/1.1 200 OK", getJsonData());
        testSocketPut("HTTP/1.1 204 NO CONTENT", "");
        testSocketPut("HTTP/1.1 500 INTERNAL SERVER ERROR", "{}");

        // Following are tests for GET Request
        testSocketGet("HTTP/1.1 200 OK", "IDS60901");
        testSocketGet("HTTP/1.1 400 BAD REQUEST", "");

        stopServer();
    }

    /**
     * Function that tests the response of the PUT request against a specified expected result
     * @param ExpectedResponse the expected response of the server
     * @param jsonData data that is going to be sent to the server in json format in string form.
     * @throws Exception
     */
    private void testSocketPut(String ExpectedResponse, String jsonData) throws Exception {
        String request = buildRequestPut("PUT", "application/json", 1, jsonData);
        String ActualResponse = sendAndReceive(request);
        assertEquals(ExpectedResponse, ActualResponse);
    }

    /**
     * Function that tests the response of the get request against a specified expected result
     * @param ExpectedResponse Expected HTTP response of the server
     * @param stationID ID that is used in the request of the server
     * @throws Exception
     */
    private void testSocketGet(String ExpectedResponse, String stationID) throws Exception {
        String request = buildRequestGet("GET", 1, stationID);
        String actualResponse = sendAndReceive(request);
        assertEquals(ExpectedResponse, actualResponse);
    }

    /**
     * Function to build the mock PUT request that will be generated by the content server
     * @param Method Method of the HTTP Request
     * @param ContentType Content Type of the data that is going to be sent
     * @param Clock Example Time for the clock
     * @param jsonData Example json Data in a string to the send to server
     * @return Stringifyed Request that will be sent to the server
     * @throws Exception
     */
    private String buildRequestPut(String Method, String ContentType, int Clock, String jsonData) throws Exception {
        StringBuilder request = new StringBuilder();
        request.append(Method).append(" / HTTP/1.1\r\n");
        request.append("Content-Type: ").append(ContentType).append("\r\n");
        request.append("Clock: ").append(Clock).append("\r\n");
        request.append("Content-Length: ").append(jsonData.length()).append("\r\n");
        request.append("\r\n");
        request.append(jsonData).append("\r\n");
        return request.toString();
    }

    /**
     * Function that builds the Expected GET Request to emulate the client
     * @param Method Method of the HTTP Request
     * @param Clock Example Lamport Clock Time
     * @param StationID Example Station ID that is already on the server
     * @return Stringifyed Request that can be sent with sendAndReceive()
     * @throws Exception
     */
    private String buildRequestGet(String Method, int Clock, String StationID) throws Exception {
        StringBuilder request = new StringBuilder();
        request.append(Method).append(" / HTTP/1.1\r\n");
        request.append("Clock: ").append(Clock).append("\r\n");
        request.append("StationID: ").append(StationID).append("\r\n");
        request.append("\r\n");
        return request.toString();
    }

    /**
     * Opens a Socket and sends the request to the server. and returns the HTTP response of the server to make sure
     * that we are getting what we expected.
     * @param request String of the request that was built with the two different functions above.
     * @return
     * @throws Exception
     */
    private String sendAndReceive(String request) throws Exception {
        try (Socket clientSocket = new Socket("localhost", 4567);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            out.println(request);
            out.flush();

            String responseLine;
            String HTTPResponse = "";

            while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                if (responseLine.startsWith("HTTP")) {
                    HTTPResponse = responseLine;
                }
            }

            return HTTPResponse;
        }
    }

    /**
     * @return Predefined json string that will be used for testing.
     */
    private String getJsonData() {
        return "{\"id\" : \"IDS60901\", \"name\" : \"Adelaide (West Terrace /  ngayirdapira)\", \"state\" : \"SA\", \"time_zone\" : \"CST\", \"lat\": -34.9, \"lon\": 138.6, \"local_date_time\": \"15/04:00pm\", \"local_date_time_full\": \"20230715160000\", \"air_temp\": 13.3, \"apparent_t\": 9.5, \"cloud\": \"Partly cloudy\", \"dewpt\": 5.7, \"press\": 1023.9, \"rel_hum\": 60, \"wind_dir\": \"S\", \"wind_spd_kmh\": 15, \"wind_spd_kt\": 8}";
    }
}