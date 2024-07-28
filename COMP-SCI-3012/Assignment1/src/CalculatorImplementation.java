import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Stack;
import java.util.function.BinaryOperator;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    private final Stack<Integer> stack;

    public CalculatorImplementation() throws RemoteException {
        stack = new Stack<>();
    }

    public synchronized void pushValue(int val) throws RemoteException {
        stack.push(val);
    }

    public synchronized void pushOperation(String Operator) throws RemoteException {
        int calculatorResult;

        if (stack.size() < 2) {
            throw new RemoteException("There are now enough operands to calculate the operator");
        }

        BinaryOperator<Integer> gcd = (a, b) -> {

            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }

            return a;
        };

        BinaryOperator<Integer> lcm = (a, b) -> {
            return Math.abs(a * b) / gcd.apply(a, b);
        };

        switch (Operator) {
            case "min" -> calculatorResult = Math.min(stack.pop(), stack.pop());
            case "max" -> calculatorResult = Math.max(stack.pop(), stack.pop());
            case "lcm" -> calculatorResult = lcm.apply(stack.pop(), stack.pop());
            case "gcd" -> calculatorResult = gcd.apply(stack.pop(), stack.pop());
            default -> calculatorResult = 0;
        };

        stack.push(calculatorResult);
    }

    public synchronized int pop() throws RemoteException {
        if (stack.isEmpty()) {
            throw new RemoteException("the stack is empty");
        }

        return stack.pop();
    }

    public synchronized boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    public synchronized int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
            pop();
        } catch (InterruptedException error) {
            throw new RemoteException(error.getMessage());
        }
    }
}
