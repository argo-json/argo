package argo.token;

import java.io.PrintStream;
import java.util.Stack;

class JsonOutJsonListener implements JsonListener {

    private final PrintStream out;
    private Stack<Boolean> firstElementProcessedStack = new Stack<Boolean>();
    private Stack<Boolean> inArray = new Stack<Boolean>();

    public JsonOutJsonListener(final PrintStream out) {
        this.out = out;
    }

    public void startDocument() {
        out.print("{");
    }

    public void endDocument() {
        out.print("}");
    }

    public void startArray() {
        out.print("[");
        firstElementProcessedStack.push(false);
        inArray.push(true);
    }

    public void startValue() {
    }

    public void endValue() {
    }

    public void endArray() {
        out.print("]");
        firstElementProcessedStack.pop();
        inArray.pop();
    }

    public void startObject() {
        out.print(",");
        inArray.push(false);
        firstElementProcessedStack.push(false);
        out.print("{");
    }

    public void endObject() {
        out.println("}");
        firstElementProcessedStack.pop();
        inArray.pop();
    }

    public void numberValue(final String value) {
        out.print(value);
    }

    public void trueValue() {
        out.print("true");
    }

    public void falseValue() {
        out.print("false");
    }

    public void nullValue() {
        out.print("null");
    }

    public void stringValue(final String value) {
        out.print("\"" + value + "\"");
    }

    public void startField(final String name) {
        if (firstElementProcessedStack.peek()) {
            out.print(",");
        }
        out.print("\"" + name + "\":");
    }

    public void endField() {
        firstElementProcessedStack.pop();
        firstElementProcessedStack.push(true);
    }
}