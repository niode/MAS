package Agent.SAP;

import Agent.*;
import Agent.Pathfinder.*;
import Ares.*;
import Ares.World.*;
import java.util.*;

public class Coordinate
{
  // All dimensions should be normalized to [0,100] so that distances can be calculated uniformly.
  private long[] dimensions = new long[3];
  private Path chargerPath;
  private Path survivorPath;

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
    chargerPath = Pathfinder.getNearestCharger(sim, opt);

    System.out.println("Setting charger path: " + chargerPath);

    if(chargerPath == null) dimensions[1] = Long.MAX_VALUE;
    else                    dimensions[1] = normalize(chargerPath.getLength(), 0, 10);

    // Nearest survivor that can be saved individually.
    int cutoff = 5;
    survivorPath = Pathfinder.getNearestSurvivor(sim, opt, cutoff);

    if(survivorPath == null) dimensions[2] = Long.MAX_VALUE;
    else                     dimensions[2] = normalize(survivorPath.getLength(), 0, 10);

    // Test
    System.out.printf("Coordinate: %d, %d, %d\n", dimensions[0], dimensions[1], dimensions[2]);
  }

  public Path getChargerPath()
  {
    return chargerPath;
  }

  public Path getSurvivorPath()
  {
    return survivorPath;
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
