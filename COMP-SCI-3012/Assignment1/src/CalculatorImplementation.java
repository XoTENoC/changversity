import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Stack;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private Stack<Integer> stack;

    public CalculatorImplementation() throws RemoteException {
        stack = new Stack<>();
    }

    public synchronized void pushValue(int val) throws RemoteException {

    }

    public synchronized void pushOperation(String Operator) throws RemoteException {

    }

    public synchronized int pop() throws RemoteException {
        return 0;
    }

    public synchronized boolean isEmpty() throws RemoteException {
        return false;
    }

    public synchronized int delayPop(int millis) throws RemoteException {
        return 0;
    }
}
