package Agent.SAP;

import Agent.*;
import Ares.*;
import java.util.List;
import java.util.ArrayList;

public abstract class Data
{
  protected List<Pair> list = new ArrayList<Pair>();

  public Action getAction(Simulation sim, AgentID id)
  {
    Coordinate sit = new Coordinate(sim, id);
    double min = Double.MAX_VALUE;
    Pair current = null;
    for(Pair pair : list)
    {
      double tmp = sit.getDistance(pair.coord);
      if(tmp < min)
      {
        min = tmp;
        current = pair;
      }
    }
    return current.action;
  }

  public void add(long[] coord, Action act)
  {
    list.add(new Pair(new Coordinate(coord), act));
  }
}
