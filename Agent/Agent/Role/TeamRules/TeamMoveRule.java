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
    boolean move = true;
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());

    AgentID teammate = finder.getTeammate();
    if(teammate == null) return false;

    System.out.println("Teammate: " + teammate.getID());

    Location teamLoc = sim.getAgentLocation(teammate);

    // Don't move if there are still survivors here to save.
    if(move && selfLoc.equals(teamLoc) && sim.getPercentage(selfLoc) > 0)
      move = false;

    // Move if there are too many agents here.
    int thisTeam = 0;
    int otherTeam = 0;
    int minID = sim.getSelfID().getID();
    for(AgentID id : sim.getAgentsAt(selfLoc))
    {
      if(sim.getAgentRole(id) == Role.ID.TEAM)
      {
        if(id.equals(finder.getTeammate()) || id.equals(sim.getSelfID()))
          thisTeam++;
        else
          otherTeam++;
        if(id.getID() < minID) minID = id.getID();
      }
    }
    if(otherTeam > thisTeam)
      move = true;
    else if(otherTeam == thisTeam && minID != sim.getSelfID().getID()
    && minID != finder.getTeammate().getID())
      move = true;

    if(move)
    {
      path = null;
      target = getTarget(sim);
      if(target == null) return setTarget(sim);
      else return true;
    }
    return false;
  }

  private Location getTarget(Simulation sim)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);
    Location teamLoc = sim.getAgentLocation(finder.getTeammate());
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());

    for(Beacon beacon : sim.getBeaconType(Beacon.TEAM_MOVE))
    {
      if(beacon.getSenderID().equals(sim.getSelfID()) ||
         beacon.getSenderID().equals(finder.getTeammate()))
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

  private boolean setTarget(Simulation sim)
  {
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);

    Location selfLoc = sim.getAgentLocation(sim.getSelfID());
    Location teamLoc = sim.getAgentLocation(finder.getTeammate());

    long bestCost = Integer.MAX_VALUE;
    int bestSender = 0;
    path = null;
    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      TeamFinder.Team minTeam = null;
      long minCost = 0;
      for(TeamFinder.Team team : finder.getTeams())
      {
        if(!team.low.equals(sim.getSelfID()) && !team.high.equals(sim.getSelfID()))
        {
          if(sim.getPercentage(sim.getAgentLocation(team.low)) > 0
          || sim.getPercentage(sim.getAgentLocation(team.high)) > 0)
            continue;
        }
        opt.end = beacon.getLocation();
        opt.start = sim.getAgentLocation(team.low);
        Path a = Pathfinder.getPath(sim, opt);
        if(a == null) continue;
        long cost = a.getLength();
        
        opt.end = beacon.getLocation();
        opt.start = sim.getAgentLocation(team.high);
        Path b = Pathfinder.getPath(sim, opt);
        if(b == null) continue;
        cost += a.getLength();

        if(minTeam == null || cost < minCost)
        {
          minTeam = team;
          minCost = cost;
        }
      }

      if(minTeam != null
      && (minTeam.low.equals(sim.getSelfID()) || minTeam.high.equals(sim.getSelfID())))
      {
        if(path == null
        || minCost < bestCost
        || (minCost == bestCost && beacon.getSenderID().getID() < bestSender))
        {
          opt.end = beacon.getLocation();
          opt.start = selfLoc;
          path = Pathfinder.getPath(sim, opt);
          bestCost = minCost;
          bestSender = beacon.getSenderID().getID();
        }
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

    // Send the message if the agent has the lower id in the team.
    if(target == null && sim.getSelfID().getID() < finder.getTeammate().getID())
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
