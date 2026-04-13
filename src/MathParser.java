import java.util.*;
import java.lang.Math;

public class MathParser {

    public static double f(String expr, double xValue) {
        List<String> postfix = toPostfix(expr);
        return evalPostfix(postfix, xValue);
    }

    private static List<String> toPostfix(String expr) {

        expr = expr.replaceAll("\\s+", "");

        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        Map<String, Integer> prec = Map.of(
                "+", 1, "-", 1,
                "*", 2, "/", 2,
                "^", 3
        );

        StringBuilder token = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isLetterOrDigit(c) || c == '.') {
                token.append(c);
            } else {

                if (token.length() > 0) {
                    String t = token.toString();

                    // ✔ FIX: functions OR variables
                    if (t.equalsIgnoreCase("sin") ||
                            t.equalsIgnoreCase("cos") ||
                            t.equalsIgnoreCase("tan") ||
                            t.equalsIgnoreCase("log") ||
                            t.equalsIgnoreCase("sqrt")) {

                        stack.push(t);
                    } else {
                        output.add(t);
                    }

                    token.setLength(0);
                }

                String op = String.valueOf(c);

                if (op.equals("(")) {
                    stack.push(op);
                }

                else if (op.equals(")")) {

                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        output.add(stack.pop());
                    }

                    if (!stack.isEmpty()) stack.pop();

                    // ✔ FIX: if function exists, pop it too
                    if (!stack.isEmpty() &&
                            (stack.peek().equalsIgnoreCase("sin") ||
                                    stack.peek().equalsIgnoreCase("cos") ||
                                    stack.peek().equalsIgnoreCase("tan") ||
                                    stack.peek().equalsIgnoreCase("log") ||
                                    stack.peek().equalsIgnoreCase("sqrt"))) {
                        output.add(stack.pop());
                    }
                }

                else {
                    while (!stack.isEmpty() &&
                            prec.containsKey(stack.peek()) &&
                            prec.get(stack.peek()) >= prec.get(op)) {
                        output.add(stack.pop());
                    }
                    stack.push(op);
                }
            }
        }

        if (token.length() > 0) {
            output.add(token.toString());
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private static double evalPostfix(List<String> postfix, double xValue) {

        Stack<Double> stack = new Stack<>();

        for (String t : postfix) {

            if (t.equals("x")) {
                stack.push(xValue);
            }

            else if (t.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(t));
            }

            else if (t.equals("+")) stack.push(stack.pop() + stack.pop());

            else if (t.equals("-")) {
                double b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            }

            else if (t.equals("*")) stack.push(stack.pop() * stack.pop());

            else if (t.equals("/")) {
                double b = stack.pop(), a = stack.pop();
                stack.push(a / b);
            }

            else if (t.equals("^")) {
                double b = stack.pop(), a = stack.pop();
                stack.push(Math.pow(a, b));
            }

            else if (t.equalsIgnoreCase("sin"))
                stack.push(Math.sin(stack.pop()));

            else if (t.equalsIgnoreCase("cos"))
                stack.push(Math.cos(stack.pop()));

            else if (t.equalsIgnoreCase("tan"))
                stack.push(Math.tan(stack.pop()));

            else if (t.equalsIgnoreCase("sqrt"))
                stack.push(Math.sqrt(stack.pop()));

            else if (t.equalsIgnoreCase("log"))
                stack.push(Math.log(stack.pop()));
        }

        return stack.pop();
    }
}