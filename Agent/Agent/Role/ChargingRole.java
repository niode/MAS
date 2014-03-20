package Agent.Role;

import java.util.ArrayList;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.Rule;

/**
 *
 */
public class ChargingRole extends Role
	{

	/**
	 * @param sim
	 * @param com
	 * @param base
	 */
	public ChargingRole(Simulation sim, Communicator com, BaseAgent base)
		{
		super(sim, com, base);
		// TODO Auto-generated constructor stub
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Role#setupRules(java.util.ArrayList)
	 */
	@Override
	public void setupRules(ArrayList<Rule> rules)
		{
		// TODO Auto-generated method stub

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
    return "ChargingRole";
  }

	}
