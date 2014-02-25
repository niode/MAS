package Agent;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.World.Objects.*;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Simulation
{
  public static final int NUM_AGENTS = 7;
  public static final int NUM_TEAMS = 2;
  public static final int MAX_ENERGY = 1000;
  public static final int MAX_LENGTH = 50*50;

  private World world = null;
  private List<Agent> agents = new ArrayList<Agent>(NUM_AGENTS * NUM_TEAMS);
  private List<Location> chargers = new LinkedList<Location>();
  private boolean[][] visited = null;
  private AgentID self = null;

  private int round = 0;
  private int totalCost = 0;
  private int cellsVisited = 0;
  private int saved = 0;

  public Simulation()
  {
    for(int i = 0; i < NUM_TEAMS; i++)
      for(int j = 0; j < NUM_AGENTS; j++)
        agents.set(i, new Agent(new AgentID(j+1, i+1), new Location(-1,-1), -1));
  }

  public void setSelf(AgentID id)
  {
    this.self = agents.get(getIndex(id)).getAgentID();
  }

  public AgentID getSelf()
  {
    return self;
  }

  public int getRound()
  {
    return round;
  }

  public int getSaved()
  {
    return saved;
  }

  public int getAverageCost()
  {
    return totalCost/cellsVisited;
  }

  public Agent getAgent(AgentID id)
  {
    return agents.get(getIndex(id));
  }

  public List<Location> getChargers()
  {
    return chargers;
  }

  private int getIndex(AgentID id)
  {
    return (id.getGID() * id.getID()) - 1;
  }

/* ----------------------------------------------------------------------------
 * Wrapper functions around the world.get... functions to make it
 * easier to quickly access information about the world without
 * going through the Cell object.
 * --------------------------------------------------------------------------*/
  public int getRowCount()
  {
    if(world == null) return -1;
    return world.getRows();
  }

  public int getColCount()
  {
    if(world == null) return -1;
    return world.getCols();
  }

  public Cell getCell(int x, int y)
  {
    return getCell(new Location(x, y));
  }

  public Cell getCell(Location location)
  {
    if(world == null) return null;
    return world.getCell(location);
  }

  public boolean isKiller(int x, int y)
  {
    return isKiller(new Location(x, y));
  }

  public boolean isKiller(Location location)
  {
    if(world == null) return false;
    Cell cell = world.getCell(location);
    if(cell == null) return false;
    return cell.isOnFire() || cell.isKiller();
  }

  public int getMoveCost(int x, int y)
  {
    return getMoveCost(new Location(x, y));
  }

  public int getMoveCost(Location location)
  {
    if(world == null) return Integer.MAX_VALUE;
    Cell cell = world.getCell(location);
    if(cell == null) return Integer.MAX_VALUE;
    return cell.getMoveCost();
  }

  public int getPercentage(int x, int y)
  {
    return getPercentage(new Location(x, y));
  }

  public int getPercentage(Location location)
  {
    if(world == null) return 0;
    Cell cell = world.getCell(location);
    if(cell == null) return 0;
    return cell.getPercentChance();
  }

  public WorldObject getTopLayer(int x, int y)
  {
    return getTopLayer(new Location(x, y));
  }

  public WorldObject getTopLayer(Location location)
  {
    if(world == null) return null;
    Cell cell = world.getCell(location);
    if(cell == null) return null;
    return cell.getTopLayer();
  }

  public LifeSignals getLifeSignals(int x, int y)
  {
    return getLifeSignals(new Location(x, y));
  }

  public LifeSignals getLifeSignals(Location location)
  {
    if(world == null) return null;
    Cell cell = world.getCell(location);
    if(cell == null) return null;
    return cell.getLifeSignals();
  }


/* ----------------------------------------------------------------------------
 * Update functions.
 * These functions update the state of the simulation.
 --------------------------------------------------------------------------- */

  // Advance to the next round.
  public void advance()
  {
    round++;
  }

  public void addSaved(int saved)
  {
    this.saved += saved;
  }

  public void update(World world)
  {
    this.world = world;
    visited = new boolean[world.getRows()][world.getCols()];
    for(int i = 0; i < world.getRows(); i++)
      for(int j = 0; j < world.getCols(); j++)
      {
        Cell cell = world.getCell(new Location(i, j));
        if(cell.isChargingCell())
          chargers.add(new Location(i, j));
      }
  }

  // Update an agent's energy level.
  public void update(AgentID id, int energy)
  {
    for(Agent agnt : agents)
    {
      if(agnt.getAgentID().equals(id))
        agnt.setEnergyLevel(energy);
    }
  }

  public void update(AgentID id, Location location)
  {
    if(world == null) return;
    Agent agent = getAgent(id);
    world.getCell(agent.getLocation()).removeAgent(id);
    world.getCell(location).addAgent(id);
    agent.setLocation(location);
  }

  public void update(SurroundInfo info)
  {
    // Update the world.
    for(Direction d : Direction.All())
    {
      CellInfo ci = info.getSurroundInfo(d);
      update(ci);
    }
  }

  public void update(CellInfo info)
  {
    if(world == null) return;
    if(!visited[info.getLocation().getRow()][info.getLocation().getCol()])
    {
      visited[info.getLocation().getRow()][info.getLocation().getCol()] = true;
      cellsVisited++;
      totalCost += info.getMoveCost();
    }
    for(AgentID id : info.getAgentIDList())
    {
      update(id, info.getLocation());
    }
    update(info.getLocation(), info.getTopLayerInfo());
  }

  public void update(Location location, LifeSignals info)
  {
    if(world == null) return;
    Cell cell = world.getCell(location);
    if(cell == null) return;
    cell.setLifeSignals(info);
  }

  public void update(Location location, WorldObjectInfo info)
  {
    if(world == null) return;
    WorldObject layer = null;
    if(info instanceof SurvivorInfo)
    {
      SurvivorInfo si = (SurvivorInfo)info;
      layer = new Survivor(si.getEnergyLevel(),
                           si.getDamageFactor(),
                           si.getBodyMass(),
                           si.getMentalState());
    } else if(info instanceof SurvivorGroupInfo)
    {
      SurvivorGroupInfo sgi = (SurvivorGroupInfo)info;
      layer = new SurvivorGroup(sgi.getEnergyLevel(),
                                sgi.getNumberOfSurvivors());
    } else if(info instanceof RubbleInfo)
    {
      RubbleInfo ri = (RubbleInfo)info;
      layer = new Rubble(ri.getRemoveEnergy(),
                         ri.getRemoveAgents());
    } else
    {
      layer = new BottomLayer();
    }
    world.getCell(location).setTopLayer(layer);
  }
}