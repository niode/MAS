package Agent.SAP.Actions;

import Agent.*;
import Agent.SAP.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

public class MoveAction implements Action
{
  public enum Option
  {
    BEST_SURVIVOR,
    NEAREST_CHARGER
  }

  private Option opt;
  private PathOptions path;
  public MoveAction(Option opt, PathOptions path)
  {
    this.opt = opt;
    this.path = path;
  }

  public AgentCommand getCommand(Simulation sim, Coordinate coord)
  {
    Location loc = sim.getAgentLocation(sim.getSelfID());
    path.start = sim.getAgentLocation(sim.getSelfID());
    switch(opt)
    {
      case BEST_SURVIVOR:
        if(coord.getSurvivorPath().getLength() > 0) loc = coord.getSurvivorPath().getNext();
        break;
      case NEAREST_CHARGER:
        if(coord.getChargerPath().getLength() > 0) loc = coord.getChargerPath().getNext();
        break;
      default:
        loc = new Location(0,0);
    }

    return new MOVE(Pathfinder.getDirection(sim.getAgentLocation(sim.getSelfID()), loc));
  }
}
