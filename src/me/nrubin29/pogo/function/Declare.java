package me.nrubin29.pogo.function;

import me.nrubin29.pogo.InvalidCodeException;
import me.nrubin29.pogo.Pogo;
import me.nrubin29.pogo.gui.Console;
import me.nrubin29.pogo.lang.Block;
import me.nrubin29.pogo.lang.Variable.VariableType;

import java.util.Arrays;

public class Declare extends Function {

	public Declare() {
		super("declare");
	}
	
	/*
	 * Usage: declare <variabletype> <name> [= <value>]
	 */
	public void run(Console console, Block b, String[] args) throws InvalidCodeException {
        VariableType t = VariableType.match(args[0]);
		String name = args[1];
		
		if (t == VariableType.VOID) throw new InvalidCodeException("Attempted to declare void variable.");

        Object value = null;

        if (args.length >= 4) {
            if (t == VariableType.STRING) {
                value = Pogo.implode(Arrays.copyOfRange(args, 3, args.length), b);
            }

            else {
                t.validateValue(args[3], b);
                value = args[3];
            }
        }

		b.addVariable(t, name, value);
	}
}