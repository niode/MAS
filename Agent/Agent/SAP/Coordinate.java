package SAP;

import Agent.*;
import Ares.*;
import Ares.World.*;

public class Coordinate
{
  // All dimensions should be normalized to [0,100] so that distances can be calculated uniformly.

  private int[] dimensions = new int[5];

  public Coordinate(Simulation sim)
  {
    Agent self = sim.getAgent(sim.getSelf());

    // Energy level.
    dimensions[0] = (100*self.getEnergyLevel())/Simulation.MAX_ENERGY;

    // Nearest charger.
    PathOptions opt = new PathOptions(self.getLocation());
    dimensions[1] = (100*(Pathfinder.getNearestCharger(sim, opt).getLength()))/Simulation.MAX_LENGTH;

    // Nearest survivor that can be saved individually.
    dimensions[2] = (100*(Pathfinder.getNearestSurvivor(sim, opt).getLength()))/Simulation.MAX_LENGTH;

  }

  public int getDistance(Coordinate other)
  {
    long tmp = 0;
    for(int i = 0; i < dimensions.length; i++)
    {
      int diff = dimensions[i] - other.dimensions[i];
      tmp += diff*diff;
    }
    return Math.sqrt(tmp);
  }
}
