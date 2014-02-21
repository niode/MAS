package Agent;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.List;

public class Simulation
{
  private int round = 0;
  private World world;
  private List<Agent> agents;

  public int getRound()
  {
    return round;
  }

  // Advance to the next round.
  public void advance()
  {
    round++;
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

  public Cell getCell(Location location)
  {
    return world.getCell(location);
  }


  /*
   * Update functions.
   * These functions update the state of the simulation.
   *
   */
  public void update(World world)
  {
    this.world = world;
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
  }

  public void update(CellInfo info)
  {

  }

  public void update(Location location, LifeSignals info)
  {

  }
}
