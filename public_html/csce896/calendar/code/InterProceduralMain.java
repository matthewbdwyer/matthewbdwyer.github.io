package summaryBasedStateProp;

import soot.PackManager;
import soot.Transform;
import soot.jimple.toolkits.annotation.callgraph.CallGraphGrapher;

public class InterProceduralMain {

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Syntax: java " + "stateprop.Main --app mainClass "
					+ "[soot options]");
			System.exit(0);
		}

		// Note that we use the Whole-Jimple Transformation Pack
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.callgrapher", CallGraphGrapher.v()));
		PackManager.v().getPack("wjtp").add(
				new Transform("wjtp.wpstateprop", WholeProgramStatePropagationPass.v()));
		
		soot.Main.main(args);

	}

}
