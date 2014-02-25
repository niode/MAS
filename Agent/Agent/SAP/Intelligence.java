package Agent.SAP;

import Agent.*;

public class Intelligence extends Agent.Intelligence
{
  public void think()
  {
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
        case Type.SLEEP:
          cmd = new SLEEP();
          break;
        case Type.SAVE_SURV:
          cmd = new SAVE_SURV();
          break;
        case Type.OBSERVE:
          cmd = new OBSERVE(act.target);
          break;
        case Type.TEAM_DIG:
          cmd = new TEAM_DIG();
          break;
      }
    } else
    {
      cmd = new MOVE(Pathfinder.getDirection(path.getNext()));
    }

    com.send(cmd);
  }
}
