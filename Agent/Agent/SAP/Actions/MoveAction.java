package Agent.SAP.Actions;

import Agent.*;
import Agent.SAP.Action;
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

  public AgentCommand getCommand(Simulation sim)
  {
    Location loc = null;
    path.start = sim.getSelf().getLocation();
    switch(opt)
    {
      case BEST_SURVIVOR:
        loc = Pathfinder.getNearestSurvivor(sim, path).getNext();
        break;
      case NEAREST_CHARGER:
        loc = Pathfinder.getNearestCharger(sim, path).getNext();
        break;
      default:
        loc = new Location(0,0);
    }

    return new MOVE(Pathfinder.getDirection(sim.getSelf().getLocation(), loc));
  }
}
