package Agent.SAP;

import Agent.SAP.Actions.*;
import Agent.Pathfinder.*;

public class BasicData extends Data
{
  final long MAX = Long.MAX_VALUE;
  public static final long CUTOFF_HIGH = 10;

  public BasicData()
  {
    super();
    PathOptions rescue1 = new PathOptions(PathOptions.CHEAPEST);
    rescue1.cutoff = 10;

    add(new long[]{0, 0, 0  }, new SleepAction());
    add(new long[]{0, 1, 1 }, new MoveAction(MoveAction.Option.NEAREST_CHARGER, new PathOptions(PathOptions.CHEAPEST)));
    add(new long[]{1, 0, 1}, new MoveAction(MoveAction.Option.BEST_SURVIVOR, rescue1));
    add(new long[]{1, 0, 0}, new SaveAction());
  }
}
