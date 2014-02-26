package Agent.SAP;

import Agent.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;

public class Intelligence extends Agent.Intelligence
{
  private Data data;
  public Intelligence(Simulation sim, Communicator com)
  {
    super(sim, com);
    data = new BasicData();
  }

  public void think()
  {
    Simulation sim = getSimulation();
    getCommunicator().send(data.getAction(sim, sim.getSelfID()).getCommand(sim));
  }
}
