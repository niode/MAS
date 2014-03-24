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

public class TeamMoveRule implements Rule
{
  Path path = null;
  Location target = null;

  private TeamFinder finder;
  public TeamMoveRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());

    AgentID teammate = finder.getTeammate();
    if(teammate == null) return false;

    Location teamLoc = sim.getAgentLocation(teammate);

    // Don't move if there are still survivors here to save.
    if(selfLoc.equals(teamLoc) && sim.getPercentage(selfLoc) > 0) return false;

    path = null;
    target = null;

    target = getTarget(sim, teammate, selfLoc, teamLoc, energy);

    System.out.println("Target: " + target);

    if(target == null) return setTarget(sim, teammate, selfLoc, teamLoc, energy);
    else return true;
  }

  private Location getTarget(Simulation sim, AgentID teammate, Location selfLoc, Location teamLoc, int energy)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);

    for(Beacon beacon : sim.getBeaconType(Beacon.TEAM_MOVE))
    {
      if(beacon.getSenderID().equals(sim.getSelfID()) ||
         beacon.getSenderID().equals(teammate))
      {
        opt.start = teamLoc;
        opt.end = beacon.getLocation();
        Path teamPath = Pathfinder.getPath(sim, opt);

        opt.start = selfLoc;
        Path selfPath = Pathfinder.getPath(sim, opt);
        if(teamPath == null || teamPath == null) continue;
        
        path = selfPath;
        return path.getLength() > 0 ? path.getLast() : selfLoc;
      }
    }
    return null;
  }

  private boolean setTarget(Simulation sim, AgentID teammate, Location selfLoc, Location teamLoc, int energy)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);

    long bestCost = 0;
    int sender = 0;
    path = null;
    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      long pathCost = 0;
      opt.end = beacon.getLocation();

      opt.start = teamLoc;
      Path teamPath = Pathfinder.getPath(sim, opt);
      if(teamPath == null) continue;
      pathCost += teamPath.getLength();

      opt.start = selfLoc;
      Path selfPath = Pathfinder.getPath(sim, opt);
      if(selfPath == null) continue;
      pathCost += selfPath.getLength();

      // Break ties with sender ID.
      if(path == null || pathCost < bestCost || (pathCost == bestCost && beacon.getSenderID().getID() < sender))
      {
        bestCost = pathCost;
        path = selfPath;
        sender = beacon.getSenderID().getID();
      }
    }
    if(path != null) return true;
    else return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    Location start = sim.getAgentLocation(sim.getSelfID());
    Location end = path.getLength() > 0 ? path.getNext() : start;
    Location t = path.getLength() > 0 ? path.getLast() : start;



    if(target == null)
    {
      Beacon msg = new Beacon(Beacon.TEAM_MOVE, sim.getSelfID(), t, Long.MAX_VALUE, 2);

      // Clear other TEAM_MOVES.
      for(Beacon beacon : sim.getBeaconType(Beacon.TEAM_MOVE))
      {
        if(beacon.getSenderID().equals(sim.getSelfID()) || beacon.getSenderID().equals(finder.getTeammate()))
        {
          com.send(Beacon.deleteBeacon(beacon));
          com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), beacon.getLocation(), beacon.getRound(), 2));
        }
      }

      // Set the target beacon.
      com.send(msg);

      // Remove all dig beacons at the target so that other teams don't go there.
      for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
      {
        if(beacon.getLocation().equals(t))
        {
          com.send(Beacon.deleteBeacon(beacon));
        }
      }
    }

    // Observe if the agent is already at the destination and is therefore
    // waiting for its teammate.
    if(end.equals(start))
    {
      for(Location loc : sim.getUnvisited())
        if(sim.getPercentage(loc) > 0)
          return new OBSERVE(loc);
    }

    return new MOVE(Pathfinder.getDirection(start, end));
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
