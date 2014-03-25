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
  public WaitRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getAgentLocation(sim.getSelfID());

    AgentID teammate = finder.getTeammate();
    if(teammate == null) return false;

    if(sim.getPercentage(loc) == 0) return false;

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

    return new MOVE(Direction.STAY_PUT);
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
