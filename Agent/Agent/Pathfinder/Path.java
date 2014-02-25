package Agent.Pathfinder;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.*;

public class Path
{
  private int moveCost;
  public static int compare(Path a, Path b, PathOptions opt)
  {
    if(opt.cheapest)
    {
      if(a.getMoveCost() - b.getMoveCost() < 0) return -1;
      else if(a.getMoveCost() == b.getMoveCost()) return 0;
      else return 1;
    } else
    {
      if(a.getLength() - b.getLength() < 0) return -1;
      if(a.getLength() == b.getLength()) return 0;
      return 1;
    }
  }

  private LinkedList<Location> path;
  
  // Protected as only the Pathfinder should be calling
  // the constructor.
  protected Path(LinkedList<Location> path, int cost)
  {
    this.path = path;
    this.moveCost = cost;
  }

  public long getMoveCost()
  {
    return moveCost;
  }

  public long getTurnCount()
  {

    // Placeholder.
    return 0;
  }

  public long getLength()
  {
    return path.size();
  }

  public Location getNext()
  {
    return path.peek();
  }
}
