package Agent.Role;

import java.util.*;
import Agent.*;
import Agent.Pathfinder.*;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.*;
import Agent.Role.TeamRules.*;
import Ares.*;
import Ares.Commands.AgentCommands.*;

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
  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#setupRules(java.util.ArrayList)
   */
  @Override
  public void setupRules(ArrayList<Rule> rules)
  {
    finder = new TeamFinder(getSimulation());
    rules.add(new NotifyRule(finder));
    rules.add(new DigRule(finder));
    rules.add(new TeamMoveRule(finder));
    rules.add(new MoveRule(finder));
  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#noRuleMatch()
   */
  @Override
  public void noRuleMatch()
  {
    //setNextRole(new ExplorerRole(getSimulation(), getCommunicator(), getBase()));
    getCommunicator().send(new MOVE(Direction.STAY_PUT));
  }

  public String toString()
  {
    return CODE;
  }
}
