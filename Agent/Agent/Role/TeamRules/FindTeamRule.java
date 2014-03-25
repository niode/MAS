package Agent.Role.TeamRules;

import Agent.*;
import Agent.Core.*;
import Agent.Pathfinder.*;
import Agent.Role.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import Ares.World.*;
import Ares.World.Objects.*;
import java.util.*;

public class FindTeamRule implements Rule
{
  private TeamFinder finder;
  private int rounds;
  public FindTeamRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
    rounds = 0;
  }

  public boolean checkConditions(Simulation sim)
  {
    if(finder.getTeammate() == null) rounds++;
    return true;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    if(finder.getTeammate() == null && rounds < 5)
      return null;
    else
      return new ExplorerRole(sim, com, base);
  }
}
