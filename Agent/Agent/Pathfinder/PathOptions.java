package Agent.Pathfinder;

import Ares.*;
import java.util.List;

public class PathOptions
{
  public Location start;
  public Location end;
  public List<Location> midpoints = new List<Location>();
  public boolean cheapest;
  public boolean shortest;
  public boolean withinRange;
  public long maxLength;
  public long maxCost;
  public long maxTurns;
  public boolean visitCharger;
  public int unknownCellCost = -1;

  // Allow only start location for use in functions like
  // getNearestWhatever.
  public PathOptions(Location start)
  {
    this.start = start;
  }
  public PathOptions(Location start, Location end)
  {
    this.start = start;
    this.end = end;
  }
}
