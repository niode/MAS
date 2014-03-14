package Agent.Pathfinder;

import Ares.*;
import java.util.List;
import java.util.LinkedList;

public class PathOptions
{
  public static final int CHEAPEST      = 1;
  public static final int SHORTEST      = 1 << 1;
  public static final int WITHIN_RANGE  = 1 << 2;
  public static final int VISIT_CHARGER = 1 << 3;

  public Location start;
  public Location end;
  public List<Location> midpoints = new LinkedList<Location>();
  public boolean cheapest;
  public boolean shortest;
  public boolean withinRange;
  public long maxLength = Long.MAX_VALUE;
  public long maxCost = Long.MAX_VALUE;
  public long maxTurns = Long.MAX_VALUE;
  public boolean visitCharger;
  public long unknownCellCost = -1;
  public int cutoff;

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

  public PathOptions(long opt)
  {
    setupOptions(opt);
  }

  public PathOptions(long opt, long length, long cost, long turns, long unknown, int cutoff)
  {
    setupOptions(opt);
    maxLength = length;
    maxCost = cost;
    maxTurns = turns;
    unknownCellCost = unknown;
    this.cutoff = cutoff;
  }

  private void setupOptions(long opt)
  {
    if((opt & CHEAPEST) > 0) cheapest = true;
    if((opt & SHORTEST) > 0) shortest = true;
    if((opt & WITHIN_RANGE) > 0) withinRange = true;
    if((opt & VISIT_CHARGER) > 0) visitCharger = true;
  }
}
