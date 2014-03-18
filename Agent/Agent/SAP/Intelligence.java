package Agent.SAP;

import Agent.*;
import Agent.Core.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;

public class Intelligence extends Agent.Intelligence
{
  private Data data;
  public Intelligence(Simulation sim, Communicator com, BaseAgent baseAgent)
  {
    super(sim, com, baseAgent);
    data = new BasicData();
  }

  public void think()
  {
    Simulation sim = getSim();
    AgentCommand cmd = data.getAction(sim, sim.getSelfID()).getCommand(sim);
    getBase().log(LogLevels.Always, "Sending: " + cmd.toString());
    getCom().send(cmd);
  }
}
