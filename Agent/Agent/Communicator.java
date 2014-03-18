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
  private static final String DELIM = ",";
  // Internal message prefixes.
  private static final String PREFIX_CELL = "CELL::";
  private static final String PREFIX_AGENT = "AGENT::";
  private static final String PREFIX_BEACON = "BEACON::";

  private BaseAgent base;
  private Simulation sim;

  public Communicator(BaseAgent base, Simulation sim)
  {
    this.base = base;
    this.sim = sim;
  }

  public void receive(FWD_MESSAGE msg)
  {
    if(msg.getFromAgentID().getGID() != sim.getSelfID().getGID())
      return;
    String[] split = msg.getMessage().split("::");
    if(split[0].equals(PREFIX_CELL))
    {

    } else if(split[0].equals(PREFIX_AGENT))
    {
      parseAgent(split[1]);
    } else if(split[0].equals(PREFIX_BEACON))
    {
      parseBeacon(split[1]);
    }
  }

  public void send(AgentCommand command)
  {
    base.send(command);
    base.log(LogLevels.Always, "Sending " + command);
  }

  public void send(AgentID id, Cell cell)
  {
    send(id, format(cell));
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

  private String format(Agent agent)
  {
    Location loc = agent.getLocation();
    AgentID id = agent.getAgentID();
    long energy = agent.getEnergyLevel();
    int alive = agent.isAlive() ? 1 : 0;
    return String.format("%s%d,%d,%d,%d,%d,%d", PREFIX_AGENT, id.getGID(), id.getID(),
                                                energy, alive, loc.getRow(), loc.getCol());
  }

  private void parseAgent(String string)
  {
    long[] numbers = mapLong(string.split(DELIM));
    AgentID id = new AgentID((int)numbers[0], (int)numbers[1]);
    long energy = numbers[2];
    boolean alive = numbers[3] == 1;
    Location loc = new Location((int)numbers[4], (int)numbers[5]);
    sim.update(id, loc);
    sim.update(id, (int)energy);
  }

  private String format(Beacon beacon)
  {
    Location loc = beacon.getLocation();
    long type = beacon.getType();
    long round = beacon.getRound();
    AgentID id = beacon.getSenderID();
    long agents = beacon.getAgentCount();
    return String.format("%s%d,%d,%d,%d,%d,%d,%d",
        PREFIX_BEACON, id.getID(), id.getGID(), type, round, agents, loc.getRow(), loc.getCol());
  }

  private void parseBeacon(String string)
  {
    long[] numbers = mapLong(string.split(DELIM));
    AgentID id = new AgentID((int)numbers[0], (int)numbers[1]);
    long type = numbers[2];
    long round = numbers[3];
    long agents = numbers[4];
    Location loc = new Location((int)numbers[5], (int)numbers[6]);
    Beacon beacon = new Beacon(type, id, loc, round, agents);
    sim.update(beacon);
  }

  private String format(Cell cell)
  {
    return PREFIX_CELL + cell.getCellInfo().toString();
  }

  // Pretend this is a functional language and use map.
  private long[] mapLong(String[] list)
  {
    long[] result = new long[list.length];
    for(int i = 0; i < list.length; i++)
      result[i] = Long.parseLong(list[i]);

    return result;
  }
}
