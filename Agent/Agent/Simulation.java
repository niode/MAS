package Agent;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.World.Objects.*;
import Agent.*;
import java.util.*;

public class Simulation
{
  public static final int NUM_AGENTS = 7;
  public static final int NUM_TEAMS = 2;
  public static final int MAX_ENERGY = 1000;
  public static final int MAX_LENGTH = 50*50;
  public static final int DEFAULT_COST = 10;

  private World world = null;
  private List<Agent> agents;
  private Map<AgentID, ArrayList<TimeLocation>> locations =
      new HashMap<AgentID, ArrayList<TimeLocation>>();
  private List<Location> chargers = new LinkedList<Location>();
  private Set<Beacon> beacons = new HashSet<Beacon>();
  private boolean[][] visited = null;
  private AgentID self = null;

  private int round = 0;
  private int totalCost = 0;
  private int cellsVisited = 0;
  private int saved = 0;

  public Simulation()
  {
    for(int i = 1; i <= NUM_TEAMS; i++)
      for(int j = 1; j <= NUM_AGENTS; j++)
        locations.put(new AgentID(j, i), new ArrayList<TimeLocation>());
        
    agents = new ArrayList<Agent>(NUM_AGENTS * NUM_TEAMS);
    for(int i = 0; i < NUM_TEAMS; i++)
      for(int j = 0; j < NUM_AGENTS; j++)
        agents.add(new Agent(new AgentID(j+1, i+1), new Location(-1,-1), -1));
  }

  public void setSelf(AgentID id)
  {
    this.self = agents.get(getIndex(id)).getAgentID();
  }

  public AgentID getSelfID()
  {
    return self;
  }

  public Agent getSelf()
  {
    return getAgent(self);
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

  public Location getAgentLocation(AgentID id)
  {
    return getAgentLocation(id, round); 
  }

  public Location getAgentLocation(AgentID id, long round)
  {
    // Binary search for the location.
    List<TimeLocation> list = locations.get(id);
    if(list.size() == 0) return null;
    int start = 0;
    int end = list.size() - 1;
    while(start < end)
    {
      int mid = start + (end - start)/2;
      TimeLocation current = list.get(mid);
      if(current.round == round) return current.location;
      if(current.round < round) start = mid;
      else end = mid;
    }
    return list.get(start).location;
  }

  public List<Location> getChargers()
  {
    return chargers;
  }

  public Set<Beacon> getBeacons()
  {
    return beacons;
  }

  public Set<Beacon> getBeaconType(long type)
  {
    HashSet<Beacon> result = new HashSet<Beacon>();
    for(Beacon beacon : beacons)
      if(beacon.getType() == type) result.add(beacon);

    return result;
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

  public boolean getVisited(int x, int y)
  {
    return visited[x][y];
  }

  public boolean getVisited(Location loc)
  {
    return getVisited(loc.getRow(), loc.getCol());
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
    if(!visited[location.getRow()][location.getCol()])
      return DEFAULT_COST;
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

    System.out.println("Location: " + location);
    if(cell == null) System.out.println("Cell is null");
    System.out.println("Top Layer:" + cell.getTopLayer());

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

    // Delete anything that happened before this round.
    for(List<TimeLocation> list : locations.values())
    {
      if(list.size() == 0) continue;
      TimeLocation head = list.get(0);
      while(list.size() > 0 && head.round < round)
      {
        list.remove(0);
        if(list.size() == 0) break;
        head = list.get(0);
      }
    }
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

    // Check if the agent is in a valid location (e.g. ensure it
    // has been initialized.
    if(agent.getLocation().valid(world.getRows(), world.getCols()))
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
    if(world == null || info.getLocation().getRow() < 0 || info.getLocation().getCol() < 0) return;

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
    }
    else if( info instanceof SurvivorGroupInfo){
        SurvivorGroupInfo sig = (SurvivorGroupInfo)info;
        layer = new SurvivorGroup(sig.getID(),sig.getEnergyLevel(),sig.getNumberOfSurvivors());
    }
    else if(info instanceof RubbleInfo){
        RubbleInfo ri = (RubbleInfo)info;
        layer = new Rubble(ri.getRemoveEnergy(),
                ri.getRemoveAgents());
    }
    else
    {
      layer = new BottomLayer();
    }
    world.getCell(location).setTopLayer(layer);
  }

  public void update(Beacon beacon)
  {
    if(beacon.getType() == Beacon.MOVE)
    {
      update(beacon.getSenderID(), beacon.getRound(), beacon.getLocation());
    } else if(beacon.getAgentCount() == 0)
    {
      beacons.remove(beacon);
    } else
    {
      beacons.add(beacon);
    }
  }

  public void update(AgentID id, long round, Location loc)
  {
    List<TimeLocation> list = locations.get(id);
    // Binary search the list.
    int start = 0; 
    int end = list.size() - 1;
    int mid = start + (end - start)/2;
    while(start < end)
    {
      TimeLocation current = list.get(mid);
      if(current.round == round)
      {
        current.location = loc;
        return;
      } else if(current.round > round)
      {
        end = mid;
      } else
      {
        start = mid;
      }
      mid = start + (end - start)/2;
    }
    list.add(mid, new TimeLocation(round, loc));
  }

  private class TimeLocation
  {
    public Location location;
    public long round;
    public TimeLocation(long round, Location location)
    {
      this.round = round;
      this.location = location;
    }
  }
}
