package Agent.SAP;

import Ares.*;
import Agent.Pathfinder.*;

public class Action
{
  public enum Type
  {
    MOVE,
    OBSERVE,
    SLEEP,
    SAVE_GROUP,
    TEAM_DIG
  }

  public Location target;
  public Type type;
  public PathOptions opt;

  public Action(Location target, Type type, PathOptions opt)
  {
    this.target = target;
    this.type = type;
    this.opt = opt;
  }
}
