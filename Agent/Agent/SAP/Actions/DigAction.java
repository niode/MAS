package Agent.SAP.Actions;

import Agent.*;
import Agent.SAP.*;
import Agent.Pathfinder.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

public class DigAction implements Action
{
  public AgentCommand getCommand(Simulation sim, Coordinate coord)
  {
    return new TEAM_DIG();
  }
}
