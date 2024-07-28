import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorClient {
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
                calculator.pushValue(35);

                calculator.pushOperation("gcd");

                System.out.println("Output value: " + calculator.pop());

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
