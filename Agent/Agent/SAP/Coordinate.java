package Agent.SAP;

import Agent.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.World.*;

public class Coordinate
{
  // All dimensions should be normalized to [0,100] so that distances can be calculated uniformly.

  private long[] dimensions = new long[3];

  public Coordinate(long[] dimensions)
  {
    this.dimensions = dimensions;
  }

  public Coordinate(Simulation sim, AgentID id)
  {
    Agent self = sim.getAgent(id);

    // Energy level.
    dimensions[0] = (100*self.getEnergyLevel())/Simulation.MAX_ENERGY;

    // Nearest charger.
    PathOptions opt = new PathOptions(self.getLocation());
    Path path = Pathfinder.getNearestCharger(sim, opt);
    if(path == null) dimensions[1] = Long.MAX_VALUE;
    else             dimensions[1] = (100 * path.getLength())/Simulation.MAX_LENGTH;

    // Nearest survivor that can be saved individually.
    path = Pathfinder.getNearestCharger(sim, opt);
    if(path == null) dimensions[2] = Long.MAX_VALUE;
    else             dimensions[2] = (100 * path.getLength())/Simulation.MAX_LENGTH;
  }

  public long getDistance(Coordinate other)
  {
    long tmp = 0;
    for(int i = 0; i < dimensions.length; i++)
    {
      long diff = dimensions[i] - other.dimensions[i];
      tmp += diff*diff;
    }
    return (long)Math.sqrt(tmp);
  }
}
