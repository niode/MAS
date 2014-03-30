package Agent.Role.ExplorerRules;

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

public class GetTargetRule implements Rule
{
  private ExplorerState state;
  public GetTargetRule(ExplorerState state)
  {
    super();
    this.state = state;
  }

  public boolean checkConditions(Simulation sim)
  {
    return state.target == null;
  }

  private Location getTarget(Simulation sim)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());

    for(Beacon beacon : sim.getBeaconType(Beacon.EXPLORER_MOVE))
    {
      if(beacon.getSenderID().equals(sim.getSelfID()))
      {
        opt.start = selfLoc;
        Path selfPath = Pathfinder.getPath(sim, opt);
        if(selfPath == null) continue;
        
        return selfPath.getLength() > 0 ? selfPath.getLast() : selfLoc;
      }
    }
    return null;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    state.target = getTarget(sim);
    System.out.printf("Got target %s\n", state.target);
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
