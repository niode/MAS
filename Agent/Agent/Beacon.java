package Agent;

import Ares.*;

public class Beacon implements Comparable
{
  public static final long HELP_RESCUE = 0;
  public static final long HELP_OBSERVE = 1;
  public static final long HELP_DIG = 2;
  public static final long HELP_MOVE = 3;
  public static final long RESCUE = 4;
  public static final long OBSERVE = 5;
  public static final long DIG = 6;
  public static final long MOVE = 7;

  private long type;
  private Location location;
  private long agents;
  private long round;
  private AgentID sender;
  public Beacon(long type, AgentID sender, Location location, long round, long agents)
  {
    this.type = type;
    this.sender = sender;
    this.location = location;
    this.round = round;
    this.agents = agents;
  }

  public long getType()
  {
    return type;
  }

  public Location getLocation()
  {
    return location;
  }

  public long getAgentCount()
  {
    return this.agents;
  }

  public AgentID getSenderID()
  {
    return this.sender;
  }

  public long getRound()
  {
    return this.round;
  }

  public String toString()
  {
    return String.format("(%d,%d,%d,%s)", type, round, agents, location.toString());
  }

  public boolean equals(Object obj)
  {
    Beacon other = null;
    if(!(obj instanceof Beacon))
      return false;

    other = (Beacon)obj;
    return type == other.type &&
           round == other.round &&
           sender.equals(other.sender) &&
           location.equals(other.location);
  }

  public int compareTo(Object obj)
  {
    Beacon other = null;
    if(!(obj instanceof Beacon))
      return 0;

    other = (Beacon)obj;

    if(type < other.type) return -1;
    if(type > other.type) return 1;
    if(sender.compareTo(other.sender) != 0) return sender.compareTo(other.sender);
    if(location.compareTo(other.location) != 0) return location.compareTo(other.location);
    if(round < other.round) return -1;
    if(round > other.round) return 1;
    return 0;
  }
}
