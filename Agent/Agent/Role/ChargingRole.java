package Agent.Role;

import java.util.ArrayList;
import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.ChargingRules.RuleChargeOnLowEnergy;
import Agent.Role.ChargingRules.RuleExploreIfAlone;
import Agent.Role.ChargingRules.RuleStopCharging;
import Agent.Role.ChargingRules.RuleWaitForAnother;
import Agent.Role.Rules.Rule;
import Ares.Location;

/**
 *
 */
public class ChargingRole extends Role
	{
	public static final String CODE = "ChargingRole";

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
		rules.add(new RuleChargeOnLowEnergy());
		rules.add(new RuleExploreIfAlone());
		rules.add(new RuleWaitForAnother());
		rules.add(new RuleStopCharging());
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Role#noRuleMatch()
	 */
	@Override
	public void noActionUsed()
		{
		// TODO Auto-generated method stub
		}
	
	/**
	 * Get the energy the role should stop charging at.
	 * 
	 * @param sim object representing agent world knowledge
	 * @return the required energy of the charging role
	 */
	public static int getRequiredEnergy(Simulation sim)
		{
		Location currentLoc = sim.getAgentLocation(sim.getSelfID());
		int currentEnergy = sim.getSelf().getEnergyLevel();
		
		//Calculate the average move cost of nearby non-kill cells.
		int totalCost = sim.getMoveCost(currentLoc);
		int count = 1;
		Set<Location> near = Pathfinder.getValidNeighbors(sim, currentLoc);
		for (Location loc : near)
			if (sim.getMoveCost(loc) < currentEnergy) //Ignore kill cell.
				{
				count++;
				totalCost += sim.getMoveCost(loc);
				}
		int average = totalCost / count;
		
		int multiplier = sim.getRowCount() > sim.getColCount() ? sim.getRowCount() : sim.getColCount();
		return (multiplier * average * 3);
		}

  public String toString()
  {
    return CODE;
  }

	}
