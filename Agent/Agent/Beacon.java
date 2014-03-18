package Agent;

import Ares.*;

public class Beacon
{
  public final long RESCUE = 0;
  public final long OBSERVE = 1;
  public final long HELP = 2;
  public final long MOVE = 3;

  private long type;
  private Location location;
  public Beacon(long type, Location location)
  {
    this.type = type;
    this.location = location;
  }

  public long getType()
  {
    return type;
  }

  public Location getLocation()
  {
    return location;
  }

  public String toString()
  {
    return String.format("(%d,%s)", type, location.toString());
  }

  public boolean equals(Object obj)
  {
    Beacon other = null;
    if(obj instanceof Beacon)
      other = (Beacon)obj;
    else
      return false;
    
    return type == other.type && location.equals(other.location);
  }
}
