import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorClient {

    /*  Main function for the main client. This Creates a new client, connects to the server pushes a couple of
        values, and an operator, and returns the operator. This Class will also reattempt to connect to the server 3
        Times */
    public static void main(String[] args) {
        String HOST = "localhost";
        int PORT_NUMBER = 1098;
        int RETRY_INTERVAL = 500;
        int MAX_RETRIES = 3;

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {

                Registry registry = LocateRegistry.getRegistry(HOST, PORT_NUMBER);
                Calculator calculator = (Calculator) registry.lookup("calculatorServer");

                calculator.pushValue(30);
                Thread.sleep(1000);
                calculator.pushValue(35);
                Thread.sleep(1000);

                calculator.pushOperation("min");

                System.out.println("Output value: " + calculator.pop());

                return;

            } catch (Exception error) {

                try {
                    Thread.sleep(RETRY_INTERVAL);
                    continue;
                } catch (InterruptedException error2) {
                    System.err.println("Error" + error2.getMessage());
                    error.printStackTrace();
                }

                System.err.println("Error" + error.getMessage());
                error.printStackTrace();
            }
        }
    }
}
