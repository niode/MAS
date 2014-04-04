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

public class WaitRule implements Rule
{
  private TeamFinder finder;
  private TeamState state;
  public WaitRule(TeamFinder finder, TeamState state)
  {
    super();
    this.finder = finder;
    this.state = state;
  }

  public boolean checkConditions(Simulation sim)
  {
    Location loc = sim.getAgentLocation(sim.getSelfID());
    if(finder.getTeammate() == null) return false;
    if(sim.getPercentage(loc) == 0) return false;
    if(state.target == null) return false;
    if(state.target.equals(loc)) return true;

    int count = 0;
    for(AgentID id : sim.getAgentsAt(loc))
      if(sim.getAgentRole(id) == Role.ID.TEAM)
        count++;
    if(count > 2) return false;
    return true;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    for(Location loc : sim.getUnvisited())
    {
      if(sim.getPercentage(loc) > 0)
      {
        return new OBSERVE(loc);
      }
    }

    return new SAVE_SURV();
    //return new MOVE(Direction.STAY_PUT);
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
