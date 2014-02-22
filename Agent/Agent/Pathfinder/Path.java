package Agent.Pathfinder;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.*;

public class Path
{
  private int moveCost;
  public static compare(Path a, Path b, PathOptions opt)
  {
    if(opt.cheapest)
    {
      return a.getMoveCost() - b.getMoveCost();
    } else if(opt.shortest)
    {
      return a.getLength() - b.getLength();
    } else
    {
      // Default behavior.
      return a.getLength() - b.getLength();
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
  
    // Placeholder.
    return 0;
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
