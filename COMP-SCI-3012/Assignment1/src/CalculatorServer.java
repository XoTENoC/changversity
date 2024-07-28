import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorServer {

    public static void main(String[] args) {
        try {

            int PORT_NUMBER = 1098;

            Calculator calculator = new CalculatorImplementation();
            Registry registry = LocateRegistry.createRegistry(PORT_NUMBER);

            registry.rebind("calculatorServer", calculator);
            System.out.println("Server ready");

            synchronized (CalculatorServer.class) {
                CalculatorServer.class.wait();
            }

        } catch (RemoteException error) {

            System.err.println("RemoteException: " + error);
            error.printStackTrace();

        } catch (Exception error) {

            System.err.println("Exception: " + error);
            error.printStackTrace();

        }
    }
}
