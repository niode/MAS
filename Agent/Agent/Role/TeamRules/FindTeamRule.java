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
  private TeamState state;
  private int rounds;
  public FindTeamRule(TeamFinder finder, TeamState state)
  {
    super();
    this.finder = finder;
    this.state = state;
    rounds = 0;
  }

  public boolean checkConditions(Simulation sim)
  {
    if(finder.getTeammate() == null) rounds++;
    return true;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    sim.addAgentState(sim.getSelfID(), State.TEAM_SEARCH);

    for(Location loc : sim.getUnvisited())
    {
      if(sim.getPercentage(loc) > 0)
      {
        return new OBSERVE(loc);
      }
    }

    return new MOVE(Direction.STAY_PUT);
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    if(state.target == null)
    {
      rounds = 0;
      sim.removeAgentState(sim.getSelfID(), State.TEAM_SEARCH);
      return new ExplorerRole(sim, com, base);
    } else if(finder.getTeammate() == null && rounds < 2)
    {
      return null;
    } else
    {
      rounds = 0;
      sim.removeAgentState(sim.getSelfID(), State.TEAM_SEARCH);
      return new ExplorerRole(sim, com, base);
    }
  }
}
