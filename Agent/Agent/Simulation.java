package Agent;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.World.Objects.*;
import Agent.*;
import Agent.Role.*;
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
      new TreeMap<AgentID, ArrayList<TimeLocation>>();
  private List<Location> chargers = new LinkedList<Location>();
  private Set<Beacon> beacons = new TreeSet<Beacon>();
  private boolean[][] visited = null;
  private long[][] turnVisited = null;
  private Map<AgentID, Role.ID> roles = new TreeMap<AgentID, Role.ID>();
  private Map<AgentID, Integer> states = new TreeMap<AgentID, Integer>();
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

  public int getAgentEnergy(AgentID id)
  {
    return agents.get(getIndex(id)).getEnergyLevel();
  }

  public boolean isAlive(AgentID id)
  {
    return agents.get(getIndex(id)).getEnergyLevel() > 0;
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
      int mid = (start + end)/2;
      TimeLocation current = list.get(mid);
      if(current.round == round) start = end = mid;
      if(current.round < round) start = mid + 1;
      else end = mid;
    }

    return list.get(end).location;
  }

  public int getAgentState(AgentID id)
  {
    if(states.containsKey(id))
    {
      return states.get(id);
    }
    else
    {
      return 0;
    }
  }

  public void setAgentState(AgentID id, int state)
  {
    states.put(id, state);
  }

  public void addAgentState(AgentID id, State state)
  {
    int s;
    if(states.containsKey(id))
      s = states.get(id);
    else
      s = 0;
    s |= state.value();
    states.put(id, s);
  }

  public void removeAgentState(AgentID id, State state)
  {
    if(states.containsKey(id))
    {
      int s = states.get(id);
      if((s & state.value()) > 0)
      {
        s ^= state.value();
        states.put(id, s);
      }
    } else
    {
      states.put(id, 0);
    }
  }

  public List<AgentID> getTeammates()
  {
    List<AgentID> result = new LinkedList<AgentID>();
    for(Agent agnt : agents)
    {
      if(agnt.getAgentID().getGID() == self.getGID() && isAlive(agnt.getAgentID()))
        result.add(agnt.getAgentID());
    }
    return result;
  }

  public List<AgentID> getTeammates(Role.ID role)
  {
    List<AgentID> result = new LinkedList<AgentID>();
    for(Agent agnt : agents)
    {
      if(agnt.getAgentID().getGID() == self.getGID() && isAlive(agnt.getAgentID())
      && roles.get(agnt.getAgentID()) == role)
        result.add(agnt.getAgentID());
    }
    return result;
  }

  public List<AgentID> getTeammates(int state)
  {
    List<AgentID> result = new LinkedList<AgentID>();
    for(Agent agnt : agents)
    {
      AgentID id = agnt.getAgentID();
      if(id.getGID() == self.getGID() 
      && isAlive(id)
      && ((state & getAgentState(id)) == state))
        result.add(id);
    }
    return result;
  }

  public List<AgentID> getAgentsAt(Location location)
  {
    return getAgentsAt(location, round);
  }

  public List<AgentID> getAgentsAt(Location location, long round)
  {
    List<AgentID> result = new LinkedList<AgentID>();
    for(Agent agt : agents)
    {
      if(agt.getAgentID().getGID() != getSelfID().getGID() || !isAlive(agt.getAgentID()))
        continue;
      AgentID id = agt.getAgentID();
      Location loc = getAgentLocation(id, round);
      if(loc == null) continue;
      if(loc.equals(location)) result.add(id);
    }
    return result;
  }

  public Role.ID getAgentRole(AgentID id)
  {
    if(roles.containsKey(id))
      return roles.get(id);
    else
      return Role.ID.UNKNOWN;
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

  // Get all unvisited locations.
  public Set<Location> getUnvisited()
  {
    return getUnvisited(round);
  }

  // Get all locations that have never been visited or have not
  // been visited in the last time turns.
  public Set<Location> getUnvisited(long time)
  {
    HashSet<Location> result = new HashSet<Location>();
    for(int i = 0; i < getRowCount(); i++)
      for(int j = 0; j < getColCount(); j++)
        if(!visited[i][j] || turnVisited[i][j] < round - time)
          result.add(new Location(i, j));
    return result;
  }

  public Set<Location> getVisited()
  {
    return getVisited(round);
  }

  public Set<Location> getVisited(long time)
  {
    HashSet<Location> result = new HashSet<Location>();
    for(int i = 0; i < getRowCount(); i++)
      for(int j = 0; j < getColCount(); j++)
        if(visited[i][j] && turnVisited[i][j] >= round - time)
          result.add(new Location(i, j));
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

  public int getAgentsRequired(int x, int y)
  {
    return getAgentsRequired(new Location(x, y));
  }

  public int getAgentsRequired(Location location)
  {
    WorldObject layer = getTopLayer(location);
    if(layer instanceof Rubble)
      return ((Rubble)layer).getRemoveAgents();
    else
      return 1;
  }

  public int getEnergyRequired(int x, int y)
  {
    return getEnergyRequired(new Location(x, y));
  }

  public int getEnergyRequired(Location location)
  {
    WorldObject layer = getTopLayer(location);
    if(layer instanceof Rubble)
      return ((Rubble)layer).getRemoveEnergy();
    else return 0;
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

    // Delete anything that happened before this round.
    for(List<TimeLocation> list : locations.values())
    {
      // If the size is 1, no new location information has
      // been received; assume the agent is still there.
      if(list.size() <= 1) continue;
      TimeLocation head = list.get(0);
      while(list.size() > 1 && head.round < round)
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
    turnVisited = new long[world.getRows()][world.getCols()];
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
    getAgent(id).setEnergyLevel(energy);
  }

  public void update(AgentID id, int energy, int state)
  {
    getAgent(id).setEnergyLevel(energy);
    states.put(id, state);
  }

  public void update(AgentID id, Location location)
  {
    update(id, location, round);
  }

  public void update(SurroundInfo info)
  {
    // Update the world.
    for(Direction d : Direction.All())
    {
      CellInfo ci = info.getSurroundInfo(d);
      update(ci);
    }

    update(info.getSurroundInfo(Direction.STAY_PUT).getLocation(), info.getLifeSignals());
  }

  public void update(CellInfo info)
  {
    if(world == null || info.getLocation().getRow() < 0 || info.getLocation().getCol() < 0)
      return;

    if(!visited[info.getLocation().getRow()][info.getLocation().getCol()])
    {
      visited[info.getLocation().getRow()][info.getLocation().getCol()] = true;
      getCell(info.getLocation()).setMoveCost(info.getMoveCost());
      cellsVisited++;
      totalCost += info.getMoveCost();
    }

    turnVisited[info.getLocation().getRow()][info.getLocation().getCol()] = round - 1;

    update(info.getLocation(), info.getTopLayerInfo());
  }

  public void update(Location location, LifeSignals info)
  {
    if(world == null) return;
    Cell cell = world.getCell(location);
    if(cell == null) return;
    cell.setLifeSignals(info);

    int totalSignals = 0;
    for(int i = 0; i < info.size(); i++)
      totalSignals += info.get(i);
    if(totalSignals > 0)
      cell.setPercentChance(100);
    else
      cell.setPercentChance(0);
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
      update(beacon.getSenderID(), beacon.getLocation(), beacon.getRound());
    } else if(beacon.getAgentCount() == 0)
    {
      beacons.remove(beacon);
    } else
    {
      beacons.add(beacon);
    }
  }

  public void update(AgentID id, Location loc, long round)
  {
    List<TimeLocation> list = locations.get(id);

    // Binary search the list.
    int start = 0; 
    int end = list.size();
    while(start < end)
    {
      int mid = (end + start)/2;
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
        start = mid + 1;
      }
    }
    list.add(end, new TimeLocation(round, loc));

  }

  public void update(AgentID id, Role.ID roleID)
  {
    roles.put(id, roleID);   
  }

  public void printWorld()
  {
    System.out.printf("-----------------------------------------------------\n", self.getID());
    System.out.printf("AGENT %d -- ENERGY: %d -- ROLE: %s ------------------\n", self.getID(),
      getAgentEnergy(self), getAgentRole(self));
    System.out.printf("-----------------------------------------------------\n", self.getID());
    for(int i = 0; i < getRowCount(); i++)
    {
      for(int j = 0; j < getColCount(); j++)
      {
        Location loc = new Location(i, j);
        String selfStr = getAgentLocation(self).equals(loc) ? "*" : " ";
        String agntStr = String.format("%2d", getAgentsAt(loc).size()).substring(0, 2);
        String costStr = String.format("%3d", getMoveCost(loc)).substring(0, 3);
        String percentStr = String.format("%3d", getPercentage(loc)).substring(0, 3) + "%";
        System.out.printf("(%s, %s, %s, %s, %d, %d) ",
          selfStr, agntStr, costStr, percentStr, getVisited(i, j) ? 1 : 0, isKiller(i, j) ? 1 : 0);
      }
      System.out.println();
    }
    System.out.printf("-----------------------------------------------------\n", self.getID());
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

    public String toString()
    {
      return "(" + location.toString() + ", " + round + ")";
    }
  }
  
  



/* ----------------------------------------------------------------------------
 * helper functions intended for observer "navigation", and whatever else if you find these useful
 * These provide descriptive navigation functionality
 --------------------------------------------------------------------------- */

    public enum Direc{ NORTH, EAST, SOUTH, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST }

    public Location navGet(Direc direction, int stepSize){ return navGet(direction,self,stepSize); }
    public Location navGet(Direc direction)              { return navGet(direction,self,1); }
    public Location navGet(Direc direction, AgentID id, int stepSize){

        Location origin = getAgentLocation( id );

        switch (direction){

            case NORTH :
                if( origin.getRow() - stepSize < 1 )
                    return new Location( origin.getRow() - stepSize, origin.getCol() );
                break;

            case SOUTH :
                if( origin.getRow() + stepSize > getRowCount() )
                    return new Location( origin.getRow() + stepSize, origin.getCol() );
                break;

            case WEST :
                if( origin.getCol() - stepSize < 1  )
                    return new Location( origin.getRow(), origin.getCol() - stepSize );
                break;

            case EAST :
                if( origin.getCol() + stepSize > getColCount() )
                    return new Location( origin.getRow(), origin.getCol() + stepSize );
                break;

            case NORTHEAST :
                if( navGet( Direc.NORTH, id, stepSize ) != null  &&
                        navGet( Direc.EAST , id, stepSize ) != null  )
                    return new Location( origin.getRow() - stepSize, origin.getCol() + stepSize );
                break;

            case NORTHWEST :
                if( navGet( Direc.NORTH, id, stepSize ) != null  &&
                        navGet( Direc.WEST , id, stepSize ) != null  )
                    return new Location( origin.getRow() - stepSize, origin.getCol() - stepSize );
                break;

            case SOUTHEAST :
                if( navGet( Direc.SOUTH, id, stepSize ) != null  &&
                        navGet( Direc.EAST , id, stepSize ) != null  )
                    return new Location( origin.getRow() + stepSize, origin.getCol() + stepSize );
                break;

            case SOUTHWEST :
                if( navGet( Direc.SOUTH, id, stepSize ) != null  &&
                        navGet( Direc.WEST , id, stepSize ) != null  )
                    return new Location( origin.getRow() + stepSize, origin.getCol() - stepSize );
                break;

        } // close switch body
        return null;

    }


    public List<Location> navGetArea(int radius)                       { return navGetArea(self, radius, radius); }
    public List<Location> navGetArea(int width, int height)            { return navGetArea(self, width , height); }
    public List<Location> navGetArea(AgentID id, int radius)           { return navGetArea(id  , radius, radius); }
    public List<Location> navGetArea(AgentID id, int width, int height){

        assert( width >= 0 && height >= 0 && id.getID() > 0 && id.getID() <= NUM_AGENTS );

        List<Location> locList = new LinkedList<Location>();
        Location origin = getAgentLocation( id );

        for( int i = origin.getCol() - width ; i <= origin.getCol() + width ; i++ ){
            for( int j = origin.getRow() - height ; j <= origin.getRow() + height ; j++){

                Location newLoc =  new Location(j,i);
                if(newLoc.valid(j,i)){ locList.add(newLoc); }

            }
        }
        return locList;
    }

    /*
        perimeters of different radius' : 0, 1, 2, 3
        3 3 3 3 3 3 3
        3 2 2 2 2 2 3   Say our origin is at 0, then calling navGetPerimeter with a radius of 0 will only give us the origin.
        3 2 1 1 1 2 3   Calling navGetPerimeter with 1 will return the list of locations represented by 1's ( if they exist )
        3 2 1 0 1 2 3   And so on... You can specify width and height separately instead of radius as well.
        3 2 1 1 1 2 3   You can also decide which agent to use as origin.
        3 2 2 2 2 2 3
        3 3 3 3 3 3 3
    */

    public List<Location> navGetPerimeter(int radius)                       { return navGetPerimeter(self, radius, radius); }
    public List<Location> navGetPerimeter(int width, int height)            { return navGetPerimeter(self, width , height); }
    public List<Location> navGetPerimeter(AgentID id, int radius)           { return navGetPerimeter(id, radius  , radius); }
    public List<Location> navGetPerimeter(AgentID id, int width, int height){

        assert( width >= 0 && height >= 0 && id.getID() > 0 && id.getID() <= NUM_AGENTS );


        List<Location> L_BIG = navGetArea(id, width, height);

        int innerWidth;
        int innerHeight;

        if(width - 1 < 0 ) innerWidth = 0;
        else innerWidth = width - 1;

        if(height - 1 < 0 ) innerHeight = 0;
        else innerHeight = height - 1;

        List<Location> L_SMALL = navGetArea( id , innerWidth , innerHeight );

        for( Location LOC_S : L_SMALL ){
            for( Location LOC_B : L_BIG ){
                if( LOC_S.getCol() == LOC_B.getCol() && LOC_S.getRow() == LOC_B.getRow() ){
                    L_BIG.remove(LOC_B);
                }
            }
        }

        return L_BIG;
    }
  
}
