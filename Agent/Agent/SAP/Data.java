package Agent.SAP;

import java.util.*;
import Agent.*;

public class Data
{
  private static ArrayList<Pair> map = new ArrayList<>();
  
  public static Action getAction(Simulation sim)
  {
    Coordinate sit = new Coordinate(sim);
    long min = Long.MAX_VALUE;
    Pair current = null;
    for(Pair pair : map)
    {
      long tmp = sit.getDistance(pair.coord);
      if(tmp < min)
      {
        min = tmp;
        current = pair;
      }
    }
    return current.action;
  }

  private static class Pair
  {
    public Coordinate coord;
    public Action action;
    public Pair(Coordinate coord, Action action)
    {
      this.coord = coord;
      this.action = action;
    }
  }
}
