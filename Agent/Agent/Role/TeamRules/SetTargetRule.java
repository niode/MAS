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

public class SetTargetRule implements Rule
{
  private TeamFinder finder;
  private TeamState state;
  private Path path;
  public SetTargetRule(TeamFinder finder, TeamState state)
  {
    super();
    this.finder = finder;
    this.state = state;
  }

  public boolean checkConditions(Simulation sim)
  {
    Location selfLoc = sim.getAgentLocation(sim.getSelfID());
    if(finder.getTeammate() == null) return false;

    // Get a new target if there is no current target.
    if(state.target == null) return true;

    // Get a new target if there no survivors left.
    if(sim.getAgentLocation(sim.getSelfID()).equals(state.target)
    && sim.getPercentage(sim.getAgentLocation(sim.getSelfID())) == 0)
      return true;

    // Get a new target if there are too many agents at this location
    // and the other teams' minimum ID is lower.
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
    if(otherTeam > thisTeam
    || (otherTeam == thisTeam
    && minID != sim.getSelfID().getID()
    && minID != finder.getTeammate().getID()))
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
        cost = cost > a.getLength() ? cost : a.getLength();

        if(minTeam == null || cost < minCost 
        || (cost == minCost && minTeam.low.getID() > team.low.getID()))
        {
          minTeam = team;
          minCost = cost;
        }
      }

      if(minTeam != null
      && (minTeam.low.equals(sim.getSelfID()) || minTeam.high.equals(sim.getSelfID())))
      {
        if(state.target == null
        || minCost < bestCost
        || (minCost == bestCost && beacon.getLocation().compareTo(bestLocation) < 0))
        {
          state.target = beacon.getLocation();
          bestCost = minCost;
          bestLocation = beacon.getLocation();
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

    // Send the message if the agent has the lower id in the team.
    if(sim.getSelfID().getID() < finder.getTeammate().getID())
    {
      // Clear other TEAM_MOVEs and replace them with HELP_DIGs.
      for(Beacon beacon : sim.getBeaconType(Beacon.TEAM_MOVE))
      {
        if(beacon.getSenderID().equals(sim.getSelfID())
        || beacon.getSenderID().equals(finder.getTeammate()))
        {
          com.send(Beacon.deleteBeacon(beacon));
          com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), beacon.getLocation(), beacon.getRound(), 2));
        }
      }

      // Remove all dig beacons at the target so that other teams don't go there.
      for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
        if(beacon.getLocation().equals(state.target))
          com.send(Beacon.deleteBeacon(beacon));

      // Finally, set the beacon.
      com.send(new Beacon(Beacon.TEAM_MOVE, sim.getSelfID(), state.target, Long.MAX_VALUE, 2));
    }
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
