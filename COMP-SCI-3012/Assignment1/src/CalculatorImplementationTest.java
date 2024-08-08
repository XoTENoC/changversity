import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.rmi.RemoteException;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorImplementationTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() throws RemoteException {
        calculator = new CalculatorImplementation();
        resetClientStack();
    }

    private void resetClientStack() throws RemoteException {
        Stack<Integer> stack = ((CalculatorImplementation) calculator).getClientStack();
        stack.clear();
    }

    @Test
    @DisplayName("Testing push and pop on the stack")
    void pushValue() throws RemoteException {
        assertAll(() -> assertEquals(2, pushValueDriver(2)),
                () -> assertEquals(10000, pushValueDriver(10000)),
                () -> assertEquals(0, pushValueDriver(0)),
                () -> assertEquals(-1, pushValueDriver(-1)));
    }

    int pushValueDriver(int a) throws RemoteException {
        calculator.pushValue(a);
        return calculator.pop();
    }

    @Test
    @DisplayName("Testing Operator")
    void pushOperator() throws RemoteException {
        assertAll(() -> assertEquals(35, pushOperatorDriver(35, 30, "max")),
                () -> assertEquals(30, pushOperatorDriver(35, 30, "min")),
                () -> assertEquals(30, pushOperatorDriver(30, 30, "max")),
                () -> assertEquals(30, pushOperatorDriver(30, 30, "min")),
                () -> assertEquals(-2, pushOperatorDriver(-2, 30, "min")),
                () -> assertEquals(-2, pushOperatorDriver(35, -2, "min")),
                () -> assertEquals(-29, pushOperatorDriver(-3, -29, "min")),
                () -> assertEquals(-1, pushOperatorDriver(-1, -20, "max")),

                () -> assertEquals(35, pushOperatorDriver(35, 35, "gcd")),
                () -> assertEquals(5, pushOperatorDriver(0, 5, "gcd")),
                () -> assertEquals(7, pushOperatorDriver(7, 0, "gcd")),
                () -> assertEquals(1, pushOperatorDriver(11, 17, "gcd")),
                () -> assertEquals(12, pushOperatorDriver(12, 36, "gcd")),
                () -> assertEquals(12, pushOperatorDriver(36, 12, "gcd")),
                () -> assertEquals(6, pushOperatorDriver(18, 48, "gcd")),
                () -> assertEquals(12, pushOperatorDriver(123456, 789012, "gcd")),
                () -> assertEquals(5, pushOperatorDriver(-10, 5, "gcd")),
                () -> assertEquals(5, pushOperatorDriver(-10, -5, "gcd")),
                () -> assertEquals(0, pushOperatorDriver(0, 0, "gcd")),

                () -> assertEquals(35, pushOperatorDriver(35, 35, "lcm")),
                () -> assertEquals(0, pushOperatorDriver(0, 5, "lcm")),
                () -> assertEquals(0, pushOperatorDriver(7, 0, "lcm")),
                () -> assertEquals(187, pushOperatorDriver(11, 17, "lcm")),
                () -> assertEquals(36, pushOperatorDriver(12, 36, "lcm")),
                () -> assertEquals(36, pushOperatorDriver(36, 12, "lcm")),
                () -> assertEquals(144, pushOperatorDriver(18, 48, "lcm")),
                () -> assertEquals(974032845, pushOperatorDriver(12345, 78901, "lcm")),
                () -> assertEquals(10, pushOperatorDriver(-10, 5, "lcm")),
                () -> assertEquals(10, pushOperatorDriver(-10, -5, "lcm")),
                () -> assertEquals(0, pushOperatorDriver(0, 0, "lcm")));
    }

    int pushOperatorDriver(int a, int b, String operator) throws RemoteException {
        calculator.pushValue(a);
        calculator.pushValue(b);
        calculator.pushOperation(operator);
        return calculator.pop();
    }

    @Test
    @DisplayName("Testing for Errors pop")
    void error() throws RemoteException {
        // Where there is nothing in the stack
        try {
            calculator.pop();
            fail("Expected RemoteException to be thrown.");
        } catch (RemoteException e) {
            assertEquals("the stack is empty", e.getMessage());
        }
    }

    @Test
    @DisplayName("Testing for Errors Push Operator")
    void errorPushOperator() throws RemoteException {
        // where there are not enough operands
        try {
            calculator.pushOperation("min");
            fail("Expected RemoteException to be thrown.");
        } catch (RemoteException e) {
            assertEquals("There are now enough operands to calculate the operator", e.getMessage());
        }

        try {
            calculator.pushValue(2);
            calculator.pushOperation("min");
            fail("Expected RemoteException to be thrown.");
        } catch (RemoteException e) {
            assertEquals("There are now enough operands to calculate the operator", e.getMessage());
        }
    }

    @Test
    @DisplayName("Testing is empty")
    void empty() throws RemoteException {
        assertTrue(calculator.isEmpty());

        calculator.pushValue(3);
        assertFalse(calculator.isEmpty());
    }
}