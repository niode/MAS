package Agent.SAP.Actions;

import Agent.*;
import Agent.SAP.Action;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

public class SleepAction implements Action
{
  public AgentCommand getCommand(Simulation sim)
  {
    return new SLEEP();
  }
}
