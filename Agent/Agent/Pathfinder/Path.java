package Agent.Pathfinder;

import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.*;

public class Path
{
  private LinkedList<Location> path;
  
  // Protected as only the Pathfinder should be calling
  // the constructor.
  protected Path(LinkedList<Location> path)
  {

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
