/**
 * 
 */
package Agent.Role.ExplorerRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import Agent.Beacon;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;

/**
 * If there are any help dig beacons in range, move to the
 * closest one.
 * 
 * @author Daniel
 */
public class RuleGoToDigBeacon implements Rule
	{
	Location target = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		target = null;
		
		// Find the closest accessible dig beacon.
		Set<Beacon> allBeacons = sim.getBeaconType(Beacon.HELP_DIG);
		
		ArrayList<Location> beaconLocs = new ArrayList<Location>();
		for (Beacon beacon : allBeacons)
			beaconLocs.add(beacon.getLocation());
		
		//Get paths to all beacons.
		Location selfLoc = sim.getAgentLocation(sim.getSelfID());
		PathOptions opt = new PathOptions(selfLoc);
		opt.shortest = false;
		opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
		List<Path> allPaths = Pathfinder.getPaths(sim, opt, beaconLocs);
		
		//Get only paths in range.
		ArrayList<Path> pathsInRange = new ArrayList<Path>();
		for (Path path : allPaths)
			if (path != null && path.getLength() > 0)
				pathsInRange.add(path);
		
		//False if no paths in range.
		if (pathsInRange.isEmpty())
			return false;
		
		//Paths exist. Sort paths by length.
		Collections.sort(pathsInRange, new Comparator<Path>(){
			@Override
			public int compare(Path path1, Path path2)
				{
				return (int)(path1.getLength() - path2.getLength());
				}
			});
		
		//Save the first step of the nearest path.
		target = pathsInRange.get(0).getNext();
		return true;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		return new MOVE(Pathfinder.getDirection(loc, target));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}

	}
