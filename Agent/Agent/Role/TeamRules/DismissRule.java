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

public class DismissRule implements Rule
{
  private TeamFinder finder;
  public DismissRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    return true;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      if(sim.getPercentage(beacon.getLocation()) == 0)
        com.send(new Beacon(Beacon.HELP_DIG, beacon.getSenderID(), beacon.getLocation(),
          beacon.getRound(), 0));
    }
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
