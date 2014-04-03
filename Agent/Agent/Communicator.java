/*
 * This class handles agent-to-agent communications.
 */

package Agent;

import Agent.Core.*;
import Agent.Role.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.World.Objects.*;
import Ares.Parsers.*;
import Ares.Commands.*;
import Ares.Commands.AresCommands.*;
import Ares.Commands.AgentCommands.*;
import java.util.List;

public class Communicator
{
  private static final String DELIM = ",";
  // Internal message prefixes.
  private static final String PREFIX_CELL = "CELL";
  private static final String PREFIX_SURROUND = "SURROUND";
  private static final String PREFIX_ENERGY = "ENERGY";
  private static final String PREFIX_STATE = "STATE";
  private static final String PREFIX_BEACON = "BEACON";
  private static final String PREFIX_ROLE = "ROLE";
  private static final String PREFIX_DELIM = "::";

  private BaseAgent base;
  private Simulation sim;

  public int getEnergyCost(AgentCommand cmd)
  {
    if(cmd instanceof MOVE)
    {
      Direction dir = ((MOVE)cmd).getDirection();
      Location loc = sim.getAgentLocation(sim.getSelfID());
      Location next =
        new Location(loc.getRow() + dir.getRowInc(), loc.getCol() + dir.getColInc());
      return sim.getMoveCost(next);
    } else if(cmd instanceof OBSERVE)
    {
      return 5;
    } else if(cmd instanceof SAVE_SURV)
    {
      return 1;
    } else if(cmd instanceof TEAM_DIG)
    {
      Location loc = sim.getAgentLocation(sim.getSelfID());
      return sim.getEnergyRequired(loc);
    } else if(cmd instanceof SLEEP)
    {
      return -5;
    } else
    {
      return 0;
    }
  }

  public Communicator(BaseAgent base, Simulation sim)
  {
    this.base = base;
    this.sim = sim;
  }

  public void receive(FWD_MESSAGE msg)
  {
    // Ignore messages from other groups.
    if(msg.getFromAgentID().getGID() != sim.getSelfID().getGID())
      return;
    String[] split = msg.getMessage().split("::");
    if(split[0].equals(PREFIX_CELL))
    {
      parseCell(split[1]);
    } else if(split[0].equals(PREFIX_SURROUND))
    {
      parseSurroundInfo(split[1]);
    } else if(split[0].equals(PREFIX_ENERGY))
    {
      parseEnergy(split[1]);
    } else if(split[0].equals(PREFIX_STATE))
    {
      parseState(split[1]);
    } else if(split[0].equals(PREFIX_BEACON))
    {
      parseBeacon(split[1]);
    } else if(split[0].equals(PREFIX_ROLE))
    {
      parseRole(split[1]);
    }
  }

  public void sendInsult()
  {
    AgentIDList ids = new AgentIDList(sim.getEnemies());
    send(ids, PREFIX_SURROUND + PREFIX_DELIM + "Go and boil your bottoms, you sons of a silly person! I blow my nose at you!");
  }

  public void send(AgentCommand command)
  {
    base.send(command);
    base.log(LogLevels.Always, "Sending " + command);

    if(command instanceof MOVE)
    {
      Direction dir = ((MOVE)command).getDirection();
      // Send a beacon.
      Location loc = sim.getAgentLocation(sim.getSelfID());
      Location next =
        new Location(loc.getRow() + dir.getRowInc(), loc.getCol() + dir.getColInc());
      send(new Beacon(Beacon.MOVE, sim.getSelfID(), next, sim.getRound() + 1, 0));
    }
    
    if(command instanceof MOVE
    || command instanceof OBSERVE
    || command instanceof SAVE_SURV
    || command instanceof SLEEP
    || command instanceof TEAM_DIG)
      send(sim.getSelfID(),
        sim.getAgentEnergy(sim.getSelfID()) - getEnergyCost(command));
  }

  public void sendState(AgentID id, int state)
  {
    send(formatState(id, state));
  }

  public void send(AgentID id, int energy)
  {
    send(formatEnergy(id, energy));
  }

  public void send(AgentID id, Cell cell)
  {
    send(id, format(cell));
  }

  public void send(SurroundInfo info)
  {
    send(format(info));
  }

  public void send(CellInfo info)
  {
    send(format(info));
  }

  public void send(Cell cell)
  {
    send(format(cell));
  }

  public void send(Role role)
  {
    send(format(role));
  }

  public void send(AgentID id, String str)
  {
    AgentIDList idList = new AgentIDList();
    idList.add(id);
    base.send(new SEND_MESSAGE(idList, str));
  }

  public void send(Beacon beacon)
  {
    send(format(beacon));
  }

  public void send(String str)
  {
    AgentIDList idList = new AgentIDList();
    base.send(new SEND_MESSAGE(idList, str));
  }

  public void send(AgentIDList idList, String str)
  {
    base.send(new SEND_MESSAGE(idList, str));
  }

  private void parseSurroundInfo(String string)
  {
    try{
      SurroundInfo info = AresParser.SurroundInfo(AresParser.getTokenizer(string));
      sim.update(info);
    } catch(AresParserException e)
    {
      base.log(LogLevels.Always, "Parser exception at parseSurroundInfo: " + e.getMessage());
    }
  }

  private void parseCell(String string)
  {
    try{
      CellInfo info = AresParser.CellInfo(AresParser.getTokenizer(string));
      sim.update(info);
    } catch (AresParserException e)
    {
      System.out.println("Parser exception at parseCell.");
    }
  }

  private void parseState(String string)
  {
    long[] numbers = mapLong(string.split(DELIM));
    AgentID id = new AgentID((int)numbers[0], (int)numbers[1]);

    // Do not update the agent's own state.
    if(id.equals(sim.getSelfID())) return;

    int state = (int)numbers[2];
    sim.setAgentState(id, state);
  }

  private void parseEnergy(String string)
  {
    long[] numbers = mapLong(string.split(DELIM));
    AgentID id = new AgentID((int)numbers[0], (int)numbers[1]);

    // Do not update the agent's own energy.
    if(id.equals(sim.getSelfID())) return;

    int energy = (int)numbers[2];
    sim.update(id, energy);
  }

  private String formatState(AgentID id, int state)
  {
    return String.format("%s%d,%d,%d",
      PREFIX_STATE + PREFIX_DELIM, id.getID(), id.getGID(), state);
  }

  private String formatEnergy(AgentID id, int energy)
  {
    return String.format("%s%d,%d,%d",
      PREFIX_ENERGY + PREFIX_DELIM, id.getID(), id.getGID(), energy);
  }

  private String format(Beacon beacon)
  {
    Location loc = beacon.getLocation();
    long type = beacon.getType();
    long round = beacon.getRound();
    AgentID id = beacon.getSenderID();
    long agents = beacon.getAgentCount();
    return String.format("%s%d,%d,%d,%d,%d,%d,%d",
        PREFIX_BEACON + PREFIX_DELIM, id.getID(), id.getGID(),
        type, round, agents, loc.getRow(), loc.getCol());
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

  private void parseRole(String string)
  {
    String[] split = string.split(DELIM);
    AgentID id = new AgentID(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    sim.update(id, Role.ID.getRoleID(split[2]));
  }

  private String format(SurroundInfo info)
  {
    return PREFIX_SURROUND + PREFIX_DELIM + info.toString();
  }

  private String format(CellInfo cell)
  {
    return PREFIX_CELL + PREFIX_DELIM + cell.toString();
  }

  private String format(Cell cell)
  {
    return PREFIX_CELL + PREFIX_DELIM + cell.getCellInfo().toString();
  }

  private String format(Role role)
  {
    AgentID id = sim.getSelfID();
    return String.format("%s%d,%d,%s",
        PREFIX_ROLE + PREFIX_DELIM, id.getID(), id.getGID(),
        role.toString());
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
