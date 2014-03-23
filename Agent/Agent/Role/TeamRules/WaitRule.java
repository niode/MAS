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

    if(sim.getAgentLocation(teammate).equals(loc) && sim.getSelfID().compareTo(teammate) < 0)
    {
      if(sim.getTopLayer(loc) instanceof Survivor) return true;
      if(sim.getTopLayer(loc) instanceof SurvivorGroup) return true;
      return false;
    } else if(sim.getTopLayer(loc) instanceof Rubble)
    {
      return true;
    }

    return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    Location loc = sim.getAgentLocation(sim.getSelfID());

    PathOptions opt = new PathOptions(PathOptions.SHORTEST);
    opt.start = loc;

    Path path = Pathfinder.getNearestSurvivor(sim, opt, 1);

    if(path != null) return new OBSERVE(path.getLast());
    else return new MOVE(Direction.STAY_PUT);
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
