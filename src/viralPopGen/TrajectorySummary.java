package viralPopGen;

import java.util.*;
import cern.jet.random.Poisson;
import cern.jet.random.engine.RandomEngine;

/**
 * Class of objects describing birth/death trajectories
 * summarised in terms of selected moments.
 * 
 * @author Tim Vaughan
 *
 */
public class TrajectorySummary {
	
	State currentState;
	
	// Moments to record:
	ArrayList<Moment> moments;

	// Simulation parameters:
	double T;
	int nTimeSteps, nSamples;
	Model model;
	
	// Poissonian RNG:
	Poisson poissonian;
	
	public TrajectorySummary(Model model, State initState,
			double T, int nTimeSteps, int nSamples,
			RandomEngine engine) {
		
		// Keep copy of simulation parameters with trajectory:
		this.model = model;
		this.T = T;
		this.nTimeSteps = nTimeSteps;
		this.nSamples = nSamples;
		
		// Initialise system state:
		currentState = new State(initState);
		
		// Derived simulation parameters:
		double dt = T/(nTimeSteps-1);
		int stepsPerSample = (nTimeSteps-1)/(nSamples-1);
		
		// Integration loop:
		int sidx = 0;
		for (int tidx=0; tidx<nTimeSteps; tidx++) {
			
			// Sample state if necessary:
			if (tidx % stepsPerSample == 0) {
				for (Moment moment : moments)
					moment.record(currentState, sidx);
				sidx++;
			}
			
			// Perform single time step:
			step(dt, poissonian);
			
		}
		
	}
	
	/**
	 * Generate a single time step.
	 * 
	 * @param dt
	 * @param poissonian
	 */
	private void step(double dt, Poisson poissonian) {
		
		// Calculate transition rates:
		for (Reaction reaction : model.reactions) {
			reaction.calcPropensities(currentState);
		}
		
		// Update state with required changes:
		for (Reaction reaction : model.reactions) {
			reaction.leap(currentState, dt, poissonian);
		}
	}
}
