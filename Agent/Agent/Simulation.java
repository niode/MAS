package Agent;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.World.Objects.*;
import java.util.List;

public class Simulation
{
  private int round = 0;
  private World world;
  private int saved = 0;
  private List<Agent> agents;
  private boolean[][] visited;
  private int totalCost = 0;
  private int cellsVisited = 0;
  private List<Location> chargers;

  public int getRound()
  {
    return round;
  }

  public int getSaved()
  {
    return saved;
  }

  public float getAverageCost()
  {
    return (float)totalCost/cellsVisited;
  }

  public Agent getAgent(AgentID id)
  {
    for(Agent agnt : agents)
    {
      if(agnt.getAgentID().equals(id))
        return agnt;
    }
    return null;
  }

  public List<Location> getChargers()
  {
    return chargers;
  }

/* ----------------------------------------------------------------------------
 * Wrapper functions around the world.get... functions to make it
 * easier to quickly access information about the world without
 * going through the Cell object.
 * --------------------------------------------------------------------------*/
  public int getRowCount()
  {
    return world.getRows();
  }

  public int getColCount()
  {
    return world.getCols();
  }

  public Cell getCell(int x, int y)
  {
    return getCell(new Location(x, y));
  }

  public Cell getCell(Location location)
  {
    return world.getCell(location);
  }

  public boolean isKiller(int x, int y)
  {
    return isKiller(new Location(x, y));
  }

  public boolean isKiller(Location location)
  {
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
    }
  }

  public void update(CellInfo info)
  {
    visited[info.getLocation().getRow()][info.getLocation().getCol()] = true;
    cellsVisited++;
    totalCost += info.getMoveCost();
  }

  public void update(Location location, LifeSignals info)
  {
    Cell cell = world.getCell(location);
    if(cell == null) return;
    cell.setLifeSignals(info);
  }
}
