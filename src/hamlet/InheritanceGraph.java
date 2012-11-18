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
package hamlet;

import java.util.List;

/**
 * A class representing an inheritance graph generated under a particular
 * stochastic population dynamics model.  Inheritance trees are a special
 * case in which children have only one parent.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class InheritanceGraph {

    // List of nodes present at the start of the simulation
    public List <Node> startNodes;

    public List<Node> activeNodes;
    
    InheritanceGraphSpec spec;
    
    /**
     * Build an inheritance graph corrsponding to a set of lineages
     * embedded within populations evolving under a birth-death process.
     * 
     * @param spec Inheritanc graph simulation specification.
     */
    public InheritanceGraph(InheritanceGraphSpec spec) {
        this.spec = spec;
    }
}
