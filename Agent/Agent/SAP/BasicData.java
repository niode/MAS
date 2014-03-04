package Agent.SAP;

import Agent.SAP.Actions.*;

public class BasicData extends Data
{
  public BasicData()
  {
    super();
    list.add(new Pair(new Coordinate(new long[]{0,0,0}), new SleepAction()));
  }
}
