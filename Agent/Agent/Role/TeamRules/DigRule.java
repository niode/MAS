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

public class DigRule implements Rule
{
  private TeamFinder finder;
  public DigRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getAgentLocation(sim.getSelfID());
    WorldObject topLayer = sim.getTopLayer(loc);

    // Don't dig if there aren't any life signals.
    if(sim.getPercentage(loc) == 0) return false;

    // Only dig if the teammate is in the same spot.
    if(finder.getTeammate() == null) return false;
    if(!sim.getAgentLocation(finder.getTeammate()).equals(loc)) return false;

    PathOptions opt = new PathOptions(loc);
    opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
    Path charger = Pathfinder.getNearestCharger(sim, opt);

    if(charger != null) energy -= charger.getMoveCost();
    int energyCost = sim.getEnergyRequired(sim.getAgentLocation(sim.getSelfID()));

    if(energy < energyCost) return false;

    // Check if there are other Team agents on this cell.
    int agentCount = 0;
    int minID = sim.getSelfID().getID();
    int minEnergy = sim.getAgentEnergy(sim.getSelfID());
    List<AgentID> agents = sim.getAgentsAt(loc);
    for(AgentID id : agents)
      if(sim.getAgentRole(id) == Role.ID.TEAM)
      {
        agentCount++;
        if(sim.getAgentEnergy(id) < minEnergy)
        {
          minEnergy = sim.getAgentEnergy(id);
          minID = id.getID();
        } else if(sim.getAgentEnergy(id) == minEnergy && minID > id.getID())
        {
          minID = id.getID();
        }
      }
    if(agentCount > 1 && agentCount <= 2 && topLayer instanceof Rubble) return true;
    else if(minEnergy == sim.getAgentEnergy(sim.getSelfID())
    && sim.getSelfID().getID() == minID)
    {
      if(sim.getTopLayer(loc) instanceof Survivor) return true;
      if(sim.getTopLayer(loc) instanceof SurvivorGroup) return true;
    }
    return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    System.out.println("Doing DigRule.");
    Location loc = sim.getAgentLocation(sim.getSelfID());

    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      if(beacon.getLocation().equals(loc))
      {
        // Update the beacon, it now needs no more agents.
        com.send(new Beacon(beacon.getType(), beacon.getSenderID(), loc, beacon.getRound(), 0));
      }
    }

    for(Beacon beacon : sim.getBeaconType(Beacon.TEAM_MOVE))
    {
      if(beacon.getLocation().equals(loc))
      {
        // Update the beacon, it now needs no more agents.
        com.send(new Beacon(beacon.getType(), beacon.getSenderID(), loc, beacon.getRound(), 0));
      }
    }

    if(sim.getTopLayer(loc) instanceof Survivor) return new SAVE_SURV();
    if(sim.getTopLayer(loc) instanceof SurvivorGroup) return new SAVE_SURV();
    else return new TEAM_DIG();
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
