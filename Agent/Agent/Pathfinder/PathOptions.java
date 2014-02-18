package Agent.Pathfinder;

import Ares.*;

public class PathOptions
{
  public Location start;
  public Location end;
  public boolean cheapest;
  public boolean shortest;
  public boolean withinRange;
  public long maxLength;
  public long maxCost;
  public long maxTurns;
  public boolean visitCharger;

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
