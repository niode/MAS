package Agent;

import Ares.Commands.AgentCommand;
import java.util.Queue;

public abstract class Intelligence
{
  Simulation sim;
  Communicator com;

  public Intelligence(Simulation sim, Communicator com)
  {
    this.com = com;
    this.sim = sim;
  }

  public Simulation getSimulation()
  {
    return sim;
  }

  public Communicator getCommunicator()
  {
    return com;
  }

  public abstract void think();
}
