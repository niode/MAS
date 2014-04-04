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

public class GetTargetRule implements Rule
{
  private TeamFinder finder;
  private TeamState state;
  public GetTargetRule(TeamFinder finder, TeamState state)
  {
    super();
    this.finder = finder;
    this.state = state;
  }

  public boolean checkConditions(Simulation sim)
  {
    if(finder.getTeammate() == null) return false;
    //return state.target == null;
    state.target = null;
    return true;
  }

  private Location getTarget(Simulation sim)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);
    Location teamLoc = sim.getAgentLocation(finder.getTeammate());
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());

    int minID = sim.getSelfID().getID();
    Path path = null;
    for(Beacon beacon : sim.getBeaconType(Beacon.TEAM_MOVE))
    {
      if(beacon.getSenderID().equals(sim.getSelfID()) ||
         beacon.getSenderID().equals(finder.getTeammate()))
      {
        opt.start = teamLoc;
        opt.end = beacon.getLocation();
        opt.maxCost = sim.getAgentEnergy(finder.getTeammate());
        Path teamPath = Pathfinder.getPath(sim, opt);

        opt.start = selfLoc;
        opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
        Path selfPath = Pathfinder.getPath(sim, opt);
        if(selfPath == null || teamPath == null) continue;
        
        if(beacon.getSenderID().getID() <= minID)
        {
          minID = beacon.getSenderID().getID();
          path = selfPath;
        }
      }
    }
    if(path == null) return null;
    return path.getLength() > 0 ? path.getLast() : selfLoc;
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
