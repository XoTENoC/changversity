import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorServer {

    /*  Setting up the main functions for the server, assigning the Calculator implementation and creating the
        server. Port of the server is also assigned in the PORT_NUMBER variable*/
    public static void main(String[] args) {
        try {

            int PORT_NUMBER = 1098;

            Calculator calculator = new CalculatorImplementation();
            Registry registry = LocateRegistry.createRegistry(PORT_NUMBER);

            registry.rebind("calculatorServer", calculator);
            System.out.println("Server ready");

        } catch (RemoteException error) {

            System.err.println("RemoteException: " + error);
            error.printStackTrace();

        } catch (Exception error) {

            System.err.println("Exception: " + error);
            error.printStackTrace();

        }
    }
}
