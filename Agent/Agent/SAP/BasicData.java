package Agent.SAP;

import Agent.SAP.Actions.*;
import Agent.Pathfinder.*;

public class BasicData extends Data
{
  final long MAX = Long.MAX_VALUE;
  public BasicData()
  {
    super();
    add(new long[]{0, 0, 0  }, new SleepAction());
    add(new long[]{0, 1, -1 }, new MoveAction(MoveAction.Option.NEAREST_CHARGER, new PathOptions(PathOptions.CHEAPEST)));
  }
}
