package Agent.SAP.Actions;

import Agent.*;
import Agent.SAP.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.World.Objects.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

public class SaveAction implements Action
{
  public AgentCommand getCommand(Simulation sim, Coordinate coord)
  {
    Location loc = sim.getAgentLocation(sim.getSelfID());
    if(sim.getTopLayer(loc) instanceof Rubble)
      return new TEAM_DIG();
    else if(sim.getTopLayer(loc) == null)
      return new MOVE(Ares.Direction.STAY_PUT);
    else
      return new SAVE_SURV();
  }
}
