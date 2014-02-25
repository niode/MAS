package Agent.SAP;

import Agent.*;
import Agent.Pathfinder.*;
import Ares.Commands.AgentCommand;

public interface Action
{
  public abstract AgentCommand getCommand(Simulation sim);
}
