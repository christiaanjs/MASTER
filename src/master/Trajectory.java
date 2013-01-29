/*
 * Copyright (C) 2012 Tim Vaughan <tgvaughan@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package master;

import beast.util.Randomizer;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * Class of objects representing trajectories through the state space of the
 * birth-death model.
 *
 * @author Tim Vaughan
 *
 */
public class Trajectory {

    // List of sampled states:
    List<PopulationState> sampledStates;
    List<Double> sampledTimes;
    
    // Simulation specification:
    private TrajectorySpec spec;

    /**
     * Generate trajectory of birth-death process.
     *
     * @param spec Simulation specification.
     */
    public Trajectory(TrajectorySpec spec) {

        // Keep copy of simulation parameters with trajectory:
        this.spec = spec;
        
        // Set seed if defined:
        if (spec.seed>=0 && !spec.seedUsed) {
            Randomizer.setSeed(spec.seed);
            spec.seedUsed = true;
        }

        // Initialise sampled state and time lists:
        sampledStates = Lists.newArrayList();
        sampledTimes = Lists.newArrayList();

        // Initialise system state:
        PopulationState currentState = new PopulationState(spec.initPopulationState);

        if (spec.evenlySpacedSampling) {
            // Sample at evenly spaced times

            double sampleDt = spec.getSampleDt();

            // Integration loop:
            for (int sidx = 0; sidx<spec.nSamples; sidx++) {                
                
                // Check for end conditions:
                PopulationEndCondition endConditionMet = null;
                for (PopulationEndCondition endCondition : spec.populationEndConditions) {
                    if (endCondition.isMet(currentState)) {
                        endConditionMet = endCondition;
                        break;
                    }
                }
                if (endConditionMet != null) {
                    if (endConditionMet.isRejection()) {
                        currentState = new PopulationState(spec.initPopulationState);
                        clearSamples();
                        sidx = -1;
                        continue;
                    } else
                        break;
                }
                
                // Report trajectory progress:
                if (spec.verbosity>1)
                    System.err.println("Recording sample time point "
                            +String.valueOf(sidx+1)+" of "
                            +String.valueOf(spec.nSamples));    

                // Sample state:
                sampleState(currentState, sampleDt*sidx);

                // Integrate to next sample time:
                double t = 0;
                while (t<sampleDt)
                    t += spec.stepper.step(currentState, spec.model,
                            sampleDt-t);

            }
        } else {
            // Sample following every integration step

            double t = 0;
            while (t<spec.simulationTime) {
                
                // Check for end conditions:
                PopulationEndCondition endConditionMet = null;
                for (PopulationEndCondition endCondition : spec.populationEndConditions) {
                    if (endCondition.isMet(currentState)) {
                        endConditionMet = endCondition;
                        break;
                    }
                }
                if (endConditionMet != null) {
                    if (endConditionMet.isRejection()) {
                        if (spec.verbosity>0)
                            System.err.println("Rejection end condition met "
                                    + "at time " + t);                        
                        
                        currentState = new PopulationState(spec.initPopulationState);
                        clearSamples();                        
                        t = 0;
                        
                        continue;
                    } else {
                        if (spec.verbosity>0)
                            System.err.println("Truncation end condition met "
                                    + "at time " + t);  
                        break;
                    }
                }
                                               
                t += spec.stepper.step(currentState, spec.model,
                        spec.simulationTime-t);
                
                // Report trajectory progress:
                if (spec.verbosity>1)
                    System.err.println("Recording sample at time "
                            + String.valueOf(t)); 
                
                sampleState(currentState, t);
            }
        }
    }
    
    /**
     * Default constructor.  Creates an empty trajectory object.  Must be
     * present to allow classes to derive from this one.
     */
    public Trajectory() {
        sampledStates = Lists.newArrayList();
        sampledTimes = Lists.newArrayList();
    }
    
    /**
     * Retrieve trajectory simulation specification.
     * 
     * @return TrajectorySpec object.
     */
    public TrajectorySpec getSpec() {
        return spec;
    }
    
    /**
     *  Sample current population size state and time.
     * 
     * @param currentState
     * @param time 
     */
    public final void sampleState(PopulationState currentState, double time) {
        sampledStates.add(new PopulationState(currentState));
        sampledTimes.add(time);
    }
    
    /**
     * Clear sampled population size states and times.
     */
    public final void clearSamples() {
        sampledStates.clear();
        sampledTimes.clear();
    }
}