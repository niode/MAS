package Agent;

import Agent.Core.BaseAgent;
import Ares.Commands.AgentCommand;
import java.util.Queue;

public abstract class Intelligence
{
  private Simulation sim;
  private Communicator com;
  private BaseAgent base;

  public Intelligence(Simulation sim, Communicator com,BaseAgent base)
  {
    this.com = com;
    this.sim = sim;
    this.base = base;
  }

  public Simulation getSim()
  {
    return sim;
  }

  public Communicator getCom()
  {
    return com;
  }
    public BaseAgent getBase(){
        return base;
    }

  public abstract void think();
}
