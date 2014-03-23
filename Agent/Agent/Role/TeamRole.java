package Agent.Role;

import java.util.*;
import Agent.*;
import Agent.Pathfinder.*;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.*;
import Agent.Role.TeamRules.*;
import Ares.AgentID;

/**
 *
 */
public class TeamRole extends Role
{
  public static final String CODE = "TeamRole";

  private TeamFinder finder;
  /**
   * @param sim
   * @param com
   * @param base
   */
  public TeamRole(Simulation sim, Communicator com, BaseAgent base)
  {
    super(sim, com, base);
    finder = new TeamFinder(sim);
  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#setupRules(java.util.ArrayList)
   */
  @Override
  public void setupRules(ArrayList<Rule> rules)
  {
    rules.add(new NotifyRule(finder));
    rules.add(new DigRule(finder));
    rules.add(new MoveRule(finder));
  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#noRuleMatch()
   */
  @Override
  public void noRuleMatch()
  {
    //setNextRole(new ExplorerRole(getSimulation(), getCommunicator(), getBase()));
  }

  public String toString()
  {
    return CODE;
  }
}
