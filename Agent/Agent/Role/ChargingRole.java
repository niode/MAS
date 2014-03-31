package Agent.Role;

import java.util.ArrayList;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.ChargingRules.RuleStillCharging;
import Agent.Role.ChargingRules.RuleExploreIfAlone;
import Agent.Role.ChargingRules.RuleStopCharging;
import Agent.Role.ChargingRules.RuleWaitForAnother;
import Agent.Role.Rules.Rule;

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
		rules.add(new RuleStillCharging());
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
		//Use nearest survivor for energy to charge to.
		PathOptions opt = new PathOptions(sim.getAgentLocation(sim.getSelfID()));
		opt.cheapest = true;
		Path path = Pathfinder.getNearestSurvivor(sim, opt, 1);
		long average = path.getMoveCost() / path.getLength();
		
		//Rather than the average world cost, use the cost to get to
		//The nearest survivor.
		int multiplier = sim.getRowCount() > sim.getColCount() ? sim.getRowCount() : sim.getColCount();
		return (int)(multiplier * average * 3);
		}

  public String toString()
  {
    return CODE;
  }

	}
