package Agent.Role;

import java.util.ArrayList;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.ChargingRules.RuleChargeOnLowEnergy;
import Agent.Role.ChargingRules.RuleExploreIfAlone;
import Agent.Role.ChargingRules.RuleWaitForAnother;
import Agent.Role.Rules.Rule;

/**
 *
 */
public class ChargingRole extends Role
	{
	/**
	 * The amount of energy the agent must be equal to or above to stop charging.
	 */
	public static final int REQUIRED_ENERGY = 400;
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
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Role#noRuleMatch()
	 */
	@Override
	public void noRuleMatch()
		{
		// TODO Auto-generated method stub
		}

  public String toString()
  {
    return CODE;
  }

	}
