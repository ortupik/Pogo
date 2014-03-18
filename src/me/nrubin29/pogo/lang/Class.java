package me.nrubin29.pogo.lang;

import me.nrubin29.pogo.InvalidCodeException;
import me.nrubin29.pogo.Utils.Writable;
import me.nrubin29.pogo.ide.Console.MessageType;
import me.nrubin29.pogo.lang.Variable.VariableType;
import me.nrubin29.pogo.lang.function.FunctionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Class extends Block {

    private final String[] code;
    FunctionManager functionManager;
    private ArrayList<Method> methods;

    public Class(String[] code) {
        super(null);

        this.code = code;
    }

    public void run(Writable writable) throws InvalidCodeException {
        this.methods = new ArrayList<Method>();
        this.functionManager = new FunctionManager(writable);

        Method currentMethod = null;

        for (String line : code) {
            line = trimComments(line);

            if (line.startsWith("method ")) {
                String[] args = line.split(" ");
                String[] mArgs = args[1].split(":");
                String methodName = mArgs[0];

                if (mArgs.length == 1)
                    throw new InvalidCodeException("Did not specify return type for method " + methodName + ".");

                VariableType returnType = VariableType.match(mArgs[1]);

                String[] params = Arrays.copyOfRange(args, 2, args.length);

                currentMethod = new Method(this, methodName, returnType, params);
            } else if (currentMethod != null && line.equals("end " + currentMethod.getName())) {
                methods.add(currentMethod);

                currentMethod = null;
            } else if (line.startsWith("declare")) functionManager.parse(this, line);

            else {
                if (currentMethod != null && !line.equals("") && !line.equals(" ")) currentMethod.addLine(line);
            }
        }

        Method main = getMethod("main");
        main.run();
        main.invoke(new String[0]);

        writable.write("--Terminated.", MessageType.OUTPUT);
    }

    private String trimComments(String str) {
        StringBuilder fin = new StringBuilder();

        for (String word : str.split(" ")) {
            if (word.startsWith("//")) return fin.toString().trim();
            else fin.append(word).append(" ");
        }

        return fin.toString().trim();
    }

    public Method getMethod(String name) throws InvalidCodeException {
        for (Method m : methods) {
            if (m.getName().equals(name)) return m;
        }

        throw new InvalidCodeException("Method " + name + " does not exist.");
    }

    @Override
    public void runAfterParse() throws InvalidCodeException {
        // No need to do anything here.
    }

    @Override
    public String toString() {
        return "Class methods=" + Arrays.toString(methods.toArray()) + " code=" + Arrays.toString(code);
    }
}