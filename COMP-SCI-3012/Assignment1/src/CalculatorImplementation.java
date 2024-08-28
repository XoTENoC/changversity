import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private static final ConcurrentHashMap<Long, Stack<Integer>> clientStacks = new ConcurrentHashMap<>();

    public CalculatorImplementation() throws RemoteException {
        super();
    }

    /*  Implementing Multi stack based on the threadID.
        Function returns the stack the is associated with the client */
    public Stack<Integer> getClientStack() {
        Long clientID = Thread.currentThread().getId();
        return clientStacks.computeIfAbsent(clientID, k -> new Stack<>());
    }

    /* Pushing the value on the stack */
    public synchronized void pushValue(int val) throws RemoteException {
        getClientStack().push(val);
    }

    /*  Implementing pushOperation
        Expecting 3 letter operation, and pushes result to the stack
        Using the standard maths library for Min and Max functions, Implementation of GCD and LCM are in the Binary
        Operators, as there are only needed here. */
    public synchronized void pushOperation(String Operator) throws RemoteException {
        int calculatorResult;

        if (getClientStack().size() < 2) {
            throw new RemoteException("There are now enough operands to calculate the operator");
        }

        int value1 = pop();
        int value2 = pop();

        BinaryOperator<Integer> gcd = (a, b) -> {

            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }

            return Math.abs(a);
        };

        BinaryOperator<Integer> lcm = (a, b) -> {
            if (a == 0 && b == 0) {
                return 0;
            }

            return Math.abs(a * b) / gcd.apply(a, b);
        };

        switch (Operator) {
            case "min":
                calculatorResult = Math.min(value1, value2);
                break;
            case "max":
                calculatorResult = Math.max(value1, value2);
                break;
            case "gcd":
                calculatorResult = gcd.apply(value1, value2);
                break;
            case "lcm":
                calculatorResult = lcm.apply(value1, value2);
                break;
            default:
                calculatorResult = 0;
                break;
        }

        pushValue(calculatorResult);
    }

    /* Pop the value from the stack */
    public synchronized int pop() throws RemoteException {
        if (isEmpty()) {
            throw new RemoteException("the stack is empty");
        }

        return getClientStack().pop();
    }

    /* checking if the stack is empty */
    public synchronized boolean isEmpty() throws RemoteException {
        return getClientStack().isEmpty();
    }

    /* Delay the pop of the stack */
    public synchronized int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
            return pop();
        } catch (InterruptedException error) {
            throw new RemoteException(error.getMessage());
        }
    }
}
