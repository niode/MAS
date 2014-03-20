package Agent.Role;

import java.util.ArrayList;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.Rule;

/**
 *
 */
public class TeamRole extends Role
{

  /**
   * @param sim
   * @param com
   * @param base
   */
  public TeamRole(Simulation sim, Communicator com, BaseAgent base)
  {
    super(sim, com, base);
    
  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#setupRules(java.util.ArrayList)
   */
  @Override
  public void setupRules(ArrayList<Rule> rules)
  {
    

  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#noRuleMatch()
   */
  @Override
  public void noRuleMatch()
  {

  }

  }
