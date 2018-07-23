package stateprop;

import soot.PackManager;
import soot.Transform;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Syntax: java " + "stateprop.Main --app mainClass "
					+ "[soot options]");
			System.exit(0);
		}

		PackManager.v().getPack("jtp").add(
				new Transform("jtp.stateprop", StatePropagationPass.v()));
		soot.Main.main(args);

	}

}
