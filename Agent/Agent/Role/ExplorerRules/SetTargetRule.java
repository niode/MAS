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

public class SetTargetRule implements Rule
{
  private ExplorerState state;
  private Path path;
  public SetTargetRule(ExplorerState state)
  {
    super();
    this.state = state;
  }

  public boolean checkConditions(Simulation sim)
  {
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());

    // Get a new target if there is no current target.
    if(state.target == null) return true;

    // Get a new target if there no survivors left.
    if(sim.getAgentLocation(sim.getSelfID()).equals(state.target)
    && sim.getPercentage(sim.getAgentLocation(sim.getSelfID())) == 0)
      return true;

    // Get a new target if there are too many agents at this location
    // and the other agents' minimum ID is lower.
    int thisTeam = 0;
    int otherTeam = 0;
    int minID = sim.getSelfID().getID();
    for(AgentID id : sim.getAgentsAt(selfLoc))
    {
      if(id.equals(sim.getSelfID()))
        thisTeam++;
      else
        otherTeam++;
      if(id.getID() < minID) minID = id.getID();
    }
    if(otherTeam > thisTeam
    || (otherTeam == thisTeam
    && minID != sim.getSelfID().getID()))
      return true;

    return false;
  }

  private void setTarget(Simulation sim)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);

    long bestCost = Integer.MAX_VALUE;
    Location bestLocation = new Location(Integer.MAX_VALUE, Integer.MAX_VALUE);
    state.target = null;
    path = null;
    for(Location loc : sim.getUnvisited())
    {
      int minAgent = 0;
      long minCost = 0;
      for(AgentID id : sim.getTeammates(Role.ID.EXPLORER))
      {
        if(!id.equals(sim.getSelfID())
        && sim.getPercentage(sim.getAgentLocation(id)) > 0)
            continue;

        opt.end = loc;
        opt.start = sim.getAgentLocation(id);
        Path a = Pathfinder.getPath(sim, opt);
        if(a == null) continue;
        long cost = a.getLength();

        if(minAgent == 0 || cost < minCost 
        || (cost == minCost && minAgent > id.getID()))
        {
          minAgent = id.getID();
          minCost = cost;
        }
      }

      if(minAgent != 0
      && minAgent == sim.getSelfID().getID())
      {
        if(state.target == null
        || minCost < bestCost
        || (minCost == bestCost && loc.compareTo(bestLocation) < 0))
        {
          state.target = loc;
          bestCost = minCost;
          bestLocation = loc;
        }
      }
    }
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    setTarget(sim);
    System.out.println("Setting target at " + state.target);
    if(state.target == null) return null;


    Location start = sim.getAgentLocation(sim.getSelfID());

    // Clear other EXPLORER_MOVEs.
    for(Beacon beacon : sim.getBeaconType(Beacon.EXPLORER_MOVE))
    {
      if(beacon.getSenderID().equals(sim.getSelfID()))
      {
        com.send(Beacon.deleteBeacon(beacon));
      }
    }

    // Finally, set the beacon.
    com.send(new Beacon(Beacon.EXPLORER_MOVE, sim.getSelfID(), state.target, Long.MAX_VALUE, 2));
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
