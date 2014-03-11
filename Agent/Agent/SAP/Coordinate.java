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
    dimensions[0] = normalize(self.getEnergyLevel(), 0, 100);

    // Nearest charger.
    PathOptions opt = new PathOptions(self.getLocation());
    Path path = Pathfinder.getNearestCharger(sim, opt);
    if(path == null) dimensions[1] = Long.MAX_VALUE;
    else             dimensions[1] = normalize(path.getLength(), 0, 10);


    // Nearest survivor that can be saved individually.
    path = Pathfinder.getNearestCharger(sim, opt);
    if(path == null) dimensions[2] = Long.MAX_VALUE;
    else             dimensions[2] = normalize(path.getLength(), 0, 10);
  }

  private long normalize(long value, long min, long max)
  {
    value -= min;
    max -= min;

    return (long)Math.ceil((double)value/(double)max);
  }

  public double getDistance(Coordinate other)
  {
    long tmp = 0;
    for(int i = 0; i < dimensions.length; i++)
    {
      if(dimensions[i] >= 0 && other.dimensions[i] >= 0)
      {
        long diff = dimensions[i] - other.dimensions[i];
        tmp += diff*diff;
      }
    }

    return Math.sqrt(tmp);
  }

  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    for(int i = 0; i < dimensions.length; i++)
    {
      builder.append(dimensions[i]);
      if(i + 1 < dimensions.length) builder.append(", ");
    }
    builder.append(")");
    return builder.toString();
  }
}
