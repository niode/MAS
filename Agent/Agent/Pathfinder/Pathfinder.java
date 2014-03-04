package Agent.Pathfinder;

import Agent.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.*;

public class Pathfinder
{
  private static final int MAX = Integer.MAX_VALUE;
  public static Direction getDirection(Location a, Location b)
  {
    int SOUTH = 1;
    int NORTH = 1 << 1;
    int WEST = 1 << 2;
    int EAST = 1 << 3;
    int pos = 0;
    if(a.getRow() < b.getRow()) pos |= NORTH;
    if(a.getRow() > b.getRow()) pos |= SOUTH;
    if(a.getCol() < b.getCol()) pos |= WEST;
    if(a.getCol() > b.getCol()) pos |= EAST;

    if     ((pos ^ SOUTH) == 0)          return Direction.SOUTH;
    else if((pos ^ NORTH) == 0)          return Direction.NORTH;
    else if((pos ^ WEST) == 0)           return Direction.WEST;
    else if((pos ^ EAST) == 0)           return Direction.EAST;
    else if((pos ^ (NORTH | EAST)) == 0) return Direction.NORTH_EAST;
    else if((pos ^ (NORTH | WEST)) == 0) return Direction.NORTH_WEST;
    else if((pos ^ (SOUTH | EAST)) == 0) return Direction.SOUTH_EAST;
    else if((pos ^ (SOUTH | WEST)) == 0) return Direction.SOUTH_WEST;
    else return Direction.STAY_PUT;
  }

  public static Path getPath(Simulation sim, PathOptions opt)
  {
    return getPathFromTree(spanningTree(sim, opt), opt);
  }

  public static List<Location> getRange(Simulation sim, PathOptions opt)
  {
    // Placeholder.
    return null;
  }

  public static Path getNearestCharger(Simulation sim, PathOptions opt)
  {
    Path result = null;
    Node[][] G = spanningTree(sim, opt);
    for(Location loc : sim.getChargers())
    {
      opt.end = loc;
      Path tmp = getPathFromTree(G, opt);
      if(result == null || (tmp != null && Path.compare(result, tmp, opt) < 0))
        result = tmp;
    }
    return result;
  }

  public static Path getNearestSurvivor(Simulation sim, PathOptions opt)
  {
    // Placeholder.
    return null;
  }

  private static Path getPathFromTree(Node[][] G, PathOptions opt)
  {
    LinkedList<Location> list = new LinkedList<Location>();
    Node current = G[opt.end.getRow()][opt.end.getCol()];
    int cost = current.cost;
    while(current != null)
    {
      list.addFirst(current.location);
      current = current.predecessor;
    }

    return new Path(list, cost);
  }

  private static Node[][] spanningTree(Simulation sim, PathOptions opt)
  {
    PriorityQueue<Node> Q = new PriorityQueue<Node>();
    Node[][] G = new Node[sim.getRowCount()][sim.getColCount()];
    for(int i = 0; i < sim.getRowCount(); i++)
      for(int j = 0; j < sim.getColCount(); j++)
        G[i][j] = new Node(null, new Location(i,j), MAX, MAX, MAX);
    
    Node currentNode = G[opt.start.getRow()][opt.start.getCol()];
    currentNode.distance = currentNode.cost = currentNode.delta = 0;
    Q.add(currentNode);
    while(Q.size() > 0)
    {
      currentNode = Q.remove();
      int tmpDist, tmpCost, tmpNext, tmpDelta;

      for(Direction d : Direction.All())
      {
        Location next = currentNode.location.add(d.getRowInc(), d.getColInc());
        if(sim.getCell(next) != null && !sim.isKiller(next))
        {
          tmpDelta = G[next.getRow()][next.getCol()].delta;
          tmpDist = currentNode.distance + 1;
          tmpCost = currentNode.cost + sim.getMoveCost(next);
          if(opt.shortest)
          {
            if(tmpDist < tmpDelta && tmpDist < opt.maxLength && tmpCost < opt.maxCost)
            {
              Q.add(G[next.getRow()][next.getCol()]);
              G[next.getRow()][next.getCol()].predecessor = currentNode;
              G[next.getRow()][next.getCol()].distance = tmpDist;
              G[next.getRow()][next.getCol()].cost = tmpCost;
              G[next.getRow()][next.getCol()].delta = tmpDist;
            }
          }
          else
          {
            if(tmpCost < tmpDelta && tmpDist < opt.maxLength && tmpCost < opt.maxCost)
            {
              Q.add(G[next.getRow()][next.getCol()]);
              G[next.getRow()][next.getCol()].predecessor = currentNode;
              G[next.getRow()][next.getCol()].distance = tmpDist;
              G[next.getRow()][next.getCol()].cost = tmpCost;
              G[next.getRow()][next.getCol()].delta = tmpCost;
            }
          }
        }
      }
    }
    return G;
  }

  private static class Node implements Comparable
  {
    public int distance;
    public int cost;
    public int delta;
    public Location location;
    public Node predecessor;
    public Node(Node predecessor, Location location, int distance, int cost, int delta)
    {
      this.predecessor = predecessor;
      this.location = location;
      this.distance = distance;
      this.cost = cost;
      this.delta = delta;
    }
    public int compareTo(Object obj)
    {
      return delta = ((Node)obj).delta;
    }
    public boolean equals(Object obj)
    {
      Node node = (Node)obj;
      return location.equals(node.location);
    }
  }
}
