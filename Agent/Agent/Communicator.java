/*
 * This class handles agent-to-agent communications.
 */

package Agent;

import Agent.Core.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Objects.*;
import Ares.Commands.*;
import Ares.Commands.AresCommands.*;
import Ares.Commands.AgentCommands.*;
import java.util.List;


public class Communicator
{
  // Internal message prefixes.
  private static final String PREFIX_WO = "WO::";
  private static final String PREFIX_CELL = "CELL::";

  private BaseAgent base;
  private Simulation sim;

  public Communicator(BaseAgent base, Simulation sim)
  {
    this.base = base;
    this.sim = sim;
  }

  public void receive(FWD_MESSAGE msg)
  {
    
  }

  public void send(AgentCommand command)
  {
    base.send(command);
    base.log(LogLevels.Always, "Sending " + command);
  }

  // Send to target.
  public void send(AgentID id, WorldObject obj)
  {
    send(id, format(obj));
  }

  // Send all.
  public void send(WorldObject obj)
  {
    send(format(obj));
  }

  public void send(Cell cell)
  {
    send(format(cell));
  }

  public void send(AgentID id, String str)
  {
    AgentIDList idList = new AgentIDList();
    idList.add(id);
    base.send(new SEND_MESSAGE(idList, str));
  }

  public void send(String str)
  {
    AgentIDList idList = new AgentIDList();
    base.send(new SEND_MESSAGE(idList, str));
  }

  private String format(Cell cell)
  {
    return PREFIX_CELL + cell.toString();
  }

  private String format(WorldObject obj)
  {
    return PREFIX_WO + obj.toString();
  }
}
