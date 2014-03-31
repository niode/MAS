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
  private TeamState state;
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
    // These private variables need to be initialized here
    // instead of the constructor as the superclass' constructor
    // calls this function.
    finder = new TeamFinder(getSimulation());
    state = new TeamState();

    rules.add(new RuleChargeRequired());
    rules.add(new NotifyRule(finder));
    rules.add(new DismissRule(finder));
    rules.add(new DigRule(finder));
    rules.add(new GetTargetRule(finder, state));
    rules.add(new SetTargetRule(finder, state));
    rules.add(new TeamMoveRule(finder, state));
    rules.add(new WaitRule(finder, state));
    rules.add(new FindTeamRule(finder));
  }

  /* (non-Javadoc)
   * @see Agent.Role.Role#noRuleMatch()
   */
  @Override
  public void noActionUsed()
  {
    setNextRole(new ExplorerRole(getSimulation(), getCommunicator(), getBase()));
    getSimulation().removeAgentState(getSimulation().getSelfID(), State.TEAM_SEARCH);
    getCommunicator().send(new MOVE(Direction.STAY_PUT));
  }

  public String toString()
  {
    return CODE;
  }
}
