package Agent.SAP;

import Agent.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;

public class Intelligence extends Agent.Intelligence
{
  public Intelligence(Simulation sim, Communicator com)
  {
    super(sim, com);
  }

  public void think()
  {
    Simulation sim = getSimulation();
    Action act = Data.getAction(sim);
    Location current = sim.getAgent(sim.getSelf()).getLocation();
    act.opt.start = current;
    Path path = null;
    path = Pathfinder.getPath(sim, act.opt);
    AgentCommand cmd = null;
    if(act.target.equals(current))
    {
      switch(act.type)
      {
        case SLEEP:
          cmd = new SLEEP();
          break;
        case SAVE:
          cmd = new SAVE_SURV();
          break;
        case OBSERVE:
          cmd = new OBSERVE(act.target);
          break;
        case TEAM_DIG:
          cmd = new TEAM_DIG();
          break;
      }
    } else
    {
      cmd = new MOVE(Pathfinder.getDirection(current, path.getNext()));
    }

    getCommunicator().send(cmd);
  }
}
