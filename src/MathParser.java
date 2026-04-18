import java.util.*;
import java.lang.Math;

public class MathParser {

    public static double f(String expr, double xValue) {
        expr = preprocess(expr);
        List<String> postfix = toPostfix(expr);
        return evalPostfix(postfix, xValue);
    }

    // Handle implicit multiplication: 2x → 2*x, )(  → )*(, x( → x*(
    private static String preprocess(String expr) {
        expr = expr.replaceAll("\\s+", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            sb.append(c);
            if (i + 1 < expr.length()) {
                char next = expr.charAt(i + 1);
                boolean currentIsNum  = Character.isDigit(c) || c == '.';
                boolean currentIsX    = (c == 'x');
                boolean currentClose  = (c == ')');
                boolean nextIsX       = (next == 'x');
                boolean nextIsOpen    = (next == '(');
                boolean nextIsAlpha   = Character.isLetter(next);

                if ((currentIsNum || currentIsX || currentClose) &&
                        (nextIsX || nextIsOpen || (nextIsAlpha && !isFuncStart(expr, i + 1)))) {
                    sb.append('*');
                }
            }
        }
        return sb.toString();
    }

    private static boolean isFuncStart(String expr, int idx) {
        String[] funcs = {"sin", "cos", "tan", "log", "sqrt", "exp"};
        for (String f : funcs) {
            if (expr.startsWith(f, idx)) return true;
        }
        return false;
    }

    private static List<String> toPostfix(String expr) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        Map<String, Integer> prec = new HashMap<>();
        prec.put("+", 1); prec.put("-", 1);
        prec.put("*", 2); prec.put("/", 2);
        prec.put("^", 3);

        String[] funcs = {"sin", "cos", "tan", "log", "sqrt", "exp"};

        StringBuilder token = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isLetterOrDigit(c) || c == '.') {
                token.append(c);
            } else {
                if (token.length() > 0) {
                    String t = token.toString();
                    boolean isFunc = false;
                    for (String f : funcs) {
                        if (t.equalsIgnoreCase(f)) { isFunc = true; break; }
                    }
                    if (isFunc) stack.push(t);
                    else output.add(t);
                    token.setLength(0);
                }

                String op = String.valueOf(c);

                if (op.equals("(")) {
                    stack.push(op);
                } else if (op.equals(")")) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        output.add(stack.pop());
                    }
                    if (!stack.isEmpty()) stack.pop(); // pop "("
                    // pop function if on top
                    if (!stack.isEmpty()) {
                        String top = stack.peek();
                        for (String f : funcs) {
                            if (top.equalsIgnoreCase(f)) { output.add(stack.pop()); break; }
                        }
                    }
                } else if (prec.containsKey(op)) {
                    // Handle unary minus: if op is '-' and previous token is operator or '(' or start
                    // We'll push a 0 to simulate unary minus
                    while (!stack.isEmpty() &&
                            prec.containsKey(stack.peek()) &&
                            prec.get(stack.peek()) >= prec.get(op)) {
                        output.add(stack.pop());
                    }
                    stack.push(op);
                }
            }
        }

        if (token.length() > 0) output.add(token.toString());
        while (!stack.isEmpty()) output.add(stack.pop());

        return output;
    }

    private static double evalPostfix(List<String> postfix, double xValue) {
        Stack<Double> stack = new Stack<>();

        for (String t : postfix) {
            switch (t.toLowerCase()) {
                case "x":
                    stack.push(xValue); break;
                case "+": { double b = stack.pop(), a = stack.pop(); stack.push(a + b); break; }
                case "-": { double b = stack.pop(), a = stack.pop(); stack.push(a - b); break; }
                case "*": { double b = stack.pop(), a = stack.pop(); stack.push(a * b); break; }
                case "/": { double b = stack.pop(), a = stack.pop(); stack.push(a / b); break; }
                case "^": { double b = stack.pop(), a = stack.pop(); stack.push(Math.pow(a, b)); break; }
                case "sin":  stack.push(Math.sin(stack.pop())); break;
                case "cos":  stack.push(Math.cos(stack.pop())); break;
                case "tan":  stack.push(Math.tan(stack.pop())); break;
                case "sqrt": stack.push(Math.sqrt(stack.pop())); break;
                case "log":  stack.push(Math.log(stack.pop())); break;
                case "exp":  stack.push(Math.exp(stack.pop())); break;
                default:
                    stack.push(Double.parseDouble(t));
            }
        }

        return stack.pop();
    }
}