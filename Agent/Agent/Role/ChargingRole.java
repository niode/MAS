package Agent.Role;

import java.util.ArrayList;
import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Pathfinder.*;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.ChargingRules.RuleChargeOnLowEnergy;
import Agent.Role.ChargingRules.RuleExploreIfAlone;
import Agent.Role.ChargingRules.RuleStopCharging;
import Agent.Role.ChargingRules.RuleWaitForAnother;
import Agent.Role.ChargingRules.RuleLeave;
import Agent.Role.ExplorerRules.RulePlaceDigBeacon;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 *
 */
public class ChargingRole extends Role
	{
	public static final String CODE = "ChargingRole";
	private static final int MIN_CHARGE = 100;

	/**
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 */
	public ChargingRole(Simulation sim, Communicator com, BaseAgent base)
		{
		super(sim, com, base);
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Role#setupRules(java.util.ArrayList)
	 */
	@Override
	public void setupRules(ArrayList<Rule> rules)
		{
		//TODO rules.add(new RulePlaceDigBeacon());
		rules.add(new RuleChargeOnLowEnergy());
		rules.add(new RuleExploreIfAlone());
		rules.add(new RuleWaitForAnother());
		rules.add(new RuleStopCharging());
		rules.add(new RuleLeave());
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Role#noRuleMatch()
	 */
	@Override
	public void noActionUsed()
		{
		}
	
	/**
	 * Ensures the agent would still be able to reach the nearest charging
	 * cell from a given location, after losing an amount of energy.
	 * 
	 * @param energyCost how much less energy the agent should have
	 * @param fromLoc the location to path to charger from
	 * @return true if the agent can still charge, otherwise false.
	 */
	public static boolean canStillCharge(Simulation sim, int energyCost, Location fromLoc, AgentID id)
		{
		//Ensure cost would not prevent agent from charging.
		PathOptions opt = new PathOptions(fromLoc);
		opt.shortest = true;
		opt.maxCost = sim.getAgentEnergy(id) - energyCost - 1;
		Path toCharger = Pathfinder.getNearestCharger(sim, opt);
		
		return !(toCharger == null);
		}
	
	/**
	 * Get the energy the role should stop charging at.
	 * 
	 * @param sim object representing agent world knowledge
	 * @return the required energy of the charging role
	 */
	public static int getRequiredEnergy(Simulation sim)
		{
		Location chargingLoc = sim.getAgentLocation(sim.getSelfID());
		int currentEnergy = sim.getSelf().getEnergyLevel();
		System.out.println("\tHAVE ENERGY: "+currentEnergy); //TODO

		//Create opt for all pathfinding tests.
		PathOptions opt = new PathOptions(chargingLoc);
		opt.shortest = false;
		opt.maxCost = currentEnergy;
		
		//Get path to nearest charging cell.
		Path toCharging = Pathfinder.getNearestCharger(sim, opt);
		
		//If null, can't reach charging cell.
		if (toCharging == null)
			return 0;
		
		//If path has length, charging location is elsewhere.
		if (!(toCharging.getLength() == 0))
			{
			System.out.println("\tMOVING TO: "+toCharging.getLast()); //TODO
			System.out.println("\tPATH COST: "+toCharging.getMoveCost());
			//Return energy above current, to ensure moving to charging cell.
			return currentEnergy + 100;
			}
		
		//Get path from charging location to nearest survivor.
		Path toSurvivor = Pathfinder.getNearestSurvivor(sim, opt, 1);
		
		//If no path to survivor, use area around charger for costs.
		if (toSurvivor == null)
			{
			//Calculate the average move cost of nearby non-kill cells.
			int totalCost = 0, count = 0;
			Set<Location> near = Pathfinder.getValidNeighbors(sim, chargingLoc, sim.getAgentEnergy(sim.getSelfID()));
			
			//If near is null, agent's energy must have gotten so low that all neighbors would kill it.
			//Just return 0. Maybe it can do something before it dies haha.
			if (near == null)
				return 0;
			
			near.add(chargingLoc);
			for (Location loc : near)
				if (sim.getMoveCost(loc) < currentEnergy) //Ignore kill cell.
					{
					count++;
					totalCost += sim.getMoveCost(loc);
					}
			//Include remove cost of any near rubble.
			for (Location loc : near)
				{
				WorldObject top = sim.getTopLayer(loc);
				if (top instanceof Rubble)
					{
					int removeEnergy = ((Rubble)top).getRemoveEnergy();
					if (removeEnergy < currentEnergy) //Ignore kill rubble.
						{
						count++;
						totalCost += removeEnergy;
						}
					}
				}
			int average = totalCost / count;
			int multiplier = sim.getRowCount() > sim.getColCount() ? sim.getRowCount() : sim.getColCount();
			System.out.println("\tCHARGING TO: "+(multiplier * average * 2)); //TODO
			return (multiplier * average * 2);
			}
		
		//Path to survivor exists. Use path for costs.
		double pathCost = 0;
		Location survivorLoc = null;
		if (toSurvivor.getLength() == 0)
			survivorLoc = chargingLoc;
		else
			{
			pathCost = ((double)toSurvivor.getMoveCost()) * 2.5;
			survivorLoc = toSurvivor.getLast();
			}
		
		int rubbleCost = 0;
		WorldObject top = sim.getTopLayer(survivorLoc);
		if (top instanceof Rubble)
			rubbleCost = ((Rubble)top).getRemoveEnergy() * 2;
		else
			rubbleCost = 100;
		
		int totalEnergy = ((int)pathCost) + rubbleCost;
		System.out.println("\tCHARGING TO: "+(totalEnergy < MIN_CHARGE ? MIN_CHARGE : totalEnergy)); //TODO
		return (totalEnergy < MIN_CHARGE ? MIN_CHARGE : totalEnergy);
		}

  public String toString()
  {
    return CODE;
  }

	}
