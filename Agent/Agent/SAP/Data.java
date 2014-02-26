package Agent.SAP;

import Agent.*;
import java.util.List;
import java.util.ArrayList;

public abstract class Data
{
  protected List<Pair> list = new ArrayList<Pair>();

  public Action getAction(Simulation sim, AgentID id)
  {
    Coordinate sit = new Coordinate(sim, id);
    long min = Long.MAX_VALUE;
    Pair current = null;
    for(Pair pair : list)
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
}
