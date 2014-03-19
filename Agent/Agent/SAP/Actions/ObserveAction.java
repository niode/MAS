package Agent.SAP.Actions;

import Agent.*;
import Agent.SAP.Action;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

public class ObserveAction implements Action
{
  public enum Option
  {
    BEST_SURVIVOR
  }

  private Option opt;
  private PathOptions path;
  public ObserveAction(Option opt, PathOptions path)
  {
    this.opt = opt;
    this.path = path;
  }

  public AgentCommand getCommand(Simulation sim, Coordinate coord)
  {
    Location loc;
    path.start = sim.getSelf().getLocation();
    switch(opt)
    {
      case BEST_SURVIVOR:
        loc = Pathfinder.getNearestSurvivor(sim, path).getLast();
        break;
      default:
        loc = new Location(0,0);
    }
    return new OBSERVE(loc);
  }
}
