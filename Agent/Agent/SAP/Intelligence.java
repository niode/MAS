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
    Simulation sim = getSimulation();
    AgentCommand cmd = data.getCommand(sim, sim.getSelfID());
    base.log(LogLevels.Always, "Sending: " + cmd.toString());
    getCommunicator().send(cmd);
  }
}
