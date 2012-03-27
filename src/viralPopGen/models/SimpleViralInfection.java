package viralPopGen.models;

import viralPopGen.*;

/**
 * A simple model of within-host viral infection dynamics.
 * 
 * @author Tim Vaughan
 *
 */
public class SimpleViralInfection {

	public static void main (String[] argv) {

		/*
		 * Assemble model:
		 */

		Model model = new Model();

		// Define populations:

		// Uninfected cell:
		Population X = new Population("X");
		model.addPopulation(X);

		// Infected cell:
		Population Y = new Population("Y");
		model.addPopulation(Y);

		// Virion:
		Population V = new Population("V");
		model.addPopulation(V);

		// Define reactions:

		// 0 -> X
		Reaction cellBirth = new Reaction();
		cellBirth.setReactantSchema();
		cellBirth.setProductSchema(X);
		cellBirth.setRate(2.5e8);
		model.addReaction(cellBirth);

		// X + V -> Y

		Reaction infection = new Reaction();
		infection.setReactantSchema(X,V);
		infection.setProductSchema(Y);
		infection.setRate(5e-13);
		model.addReaction(infection);

		// Y -> Y + V
		Reaction budding = new Reaction();
		budding.setReactantSchema(Y);
		budding.setProductSchema(Y,V);
		budding.setRate(1e3);
		model.addReaction(budding);

		// X -> 0
		Reaction cellDeath = new Reaction();
		cellDeath.setReactantSchema(X);
		cellDeath.setProductSchema();
		cellDeath.setRate(1e-3);
		model.addReaction(cellDeath);

		// Y -> 0
		Reaction infectedDeath = new Reaction();
		infectedDeath.setReactantSchema(Y);
		infectedDeath.setProductSchema();
		infectedDeath.setRate(1.0);
		model.addReaction(infectedDeath);

		// V -> 0
		Reaction virionDeath = new Reaction();
		virionDeath.setReactantSchema(V);
		virionDeath.setProductSchema();
		virionDeath.setRate(3.0);
		model.addReaction(virionDeath);

		// Define moments:

		// <N_X>
		Moment mX = new Moment("X", X);
		model.addMoment(mX);

		// <N_Y>
		Moment mY = new Moment("Y", Y);
		model.addMoment(mY);

		// <N_V>
		Moment mV = new Moment("V", V);
		model.addMoment(mV);

		/*
		 *  Set initial state:
		 */

		State initState = new State(model);
		initState.set(X, 2.5e11);
		initState.set(Y, 0);
		initState.set(V, 100.0);

		/*
		 * Define simulation:
		 */

		Simulation simulation = new Simulation();

		simulation.setModel(model);
		simulation.setSimulationTime(10.0); // days
		simulation.setnTimeSteps(1001);
		simulation.setnSamples(1001);
		simulation.setnTraj(100);
		simulation.setSeed(42);
		simulation.setInitState(initState);

		/*
		 * Generate summarised ensemble:
		 */

		EnsembleSummary ensemble = new EnsembleSummary(simulation);

		/*
		 * Dump results (JSON):
		 */

		ensemble.dump();
	}
}
