package Agent.Pathfinder;

import Agent.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.*;

public class Pathfinder
{

//// DYNAMIC FUNCTIONS ///

	private Node[][] W = null;
	private boolean isWInit = false;
	private Node2[][] G = null;
	private boolean isGInit = false;
	private Simulation sim;
	private PathOptions opt;

	public Pathfinder(Simulation sim, PathOptions opt)
	{
		this.sim = sim;
		this.opt = opt;
	}

	public void changeOpt(PathOptions opt)
	{
		this.opt = opt;
		isGInit = false;
	}

	public void changeSim(Simulation sim)
	{
		this.sim = sim;
		isGInit = false;
	}

	public PathOptions getOpt()
	{
		return this.opt;
	}

	public Simulation getSim()
	{
		return this.sim;
	}

	public Path getNearestCharger()
	{
		Path result = null;
		if (!isGInit)
			G = genDijkstra(sim, opt);
		for(Location loc : sim.getChargers())
		{
		  opt.end = loc;
		  Path tmp = getPathFromTree(G, opt);
		  if(result == null || (tmp != null && Path.compare(result, tmp, opt) < 0))
		    result = tmp;
		}
		return result;
	}

	public Path getNearestSurvivor(int cutoff)
	{
		Path result = null;
		if (!isGInit)
			G = genDijkstra(sim, opt);
		for(int i = 0; i < sim.getRowCount(); i++)
		  for(int j = 0; j < sim.getColCount(); j++)
		  {
		    if(i == opt.start.getRow() && j == opt.start.getCol()) continue;
		    if(sim.getPercentage(i, j) >= cutoff)
		    {
		      opt.end = new Location(i, j);
		      Path tmp = getPathFromTree(G, opt);
		      if(result == null || (tmp != null && Path.compare(result, tmp, opt) < 0))
		        result = tmp;
		    }
		  }
		return result;
	}
	
	public Path getPath()
	{
		//System.out.printf("Getting path to (%s, %s)\n", opt.start, opt.end);
		if (!isGInit)
			G = genDijkstra(sim, opt);
		Path tmp = getPathFromTree(G, opt);
		//if(tmp != null) System.out.println(tmp.toString());
		//else System.out.println("NULL");
		return tmp;
	}

		public List<Path> getPaths(List<Location> list)
	{
		if (!isGInit)
			G = genDijkstra(sim, opt);
		List<Path> result = new LinkedList<Path>();
		for(Location l : list)
		{
		  opt.end = l;
		  result.add(getPathFromTree(G, opt));
		}
		return result;
	}

	public int getCheapestCost(Location from, Location to)
	{ 
		return getCheapestCost(from.getRow(), from.getCol(),to.getRow(), to.getCol());
	}
	
	public int getCheapestCost(int i, int j, int k, int l)
	{
		int N = sim.getColCount();
		if (!isWInit)
			W = floydWarshall(sim, opt.cheapest, opt.unknownCellCost); 
		return W[i*N+j][k*N+l].cost;
	}
	
/////// STATIC FUNCTIONS //////

	
  public static Direction getDirection(Location a, Location b)
  {
    int SOUTH = 1;
    int NORTH = 1 << 1;
    int WEST = 1 << 2;
    int EAST = 1 << 3;
    int pos = 0;
    if(a.getRow() < b.getRow()) pos |= SOUTH;
    if(a.getRow() > b.getRow()) pos |= NORTH;
    if(a.getCol() < b.getCol()) pos |= EAST;
    if(a.getCol() > b.getCol()) pos |= WEST;

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
  
	/*opt must be set as follow :
	- shortest + maxLength if want to get a matrix that says if accessible within maxLength
	- cheapest + maxCost if want to get a matrix that says if accessible within maxCost
	+ need to have a default cost for unvisited cells
 */
  public static boolean[][][][] getWithinRange(Simulation sim, PathOptions opt)
  {
  	int M = sim.getRowCount();
  	int N = sim.getColCount();
  	boolean[][][][] B = new boolean[M][N][M][N];
  	Node[][] W = floydWarshall(sim, opt.cheapest, opt.unknownCellCost);
  	int max = (opt.cheapest)? opt.maxCost : opt.maxLength;
  	
  	for(int i = 0; i < M; i++)
  		for(int j = 0; j < N; j++)
  			for(int k = 0; k < M; k++)
  				for(int l = 0; l < N; l++)
						if (W[i*N+j][k*N+l].delta < max)
							B[i][j][k][l] = true;
  				
  	return B;
  }
  
  
  public static Set<Location> getValidNeighbors(Simulation sim, Location location, int cost)
  {
  	int locRow = location.getRow();
  	int locCol = location.getCol();
  	
  	HashSet<Location> result = new HashSet<Location>();
  	for (int i = locRow - 1; i <= locRow + 1; i++)
  	  for (int j = locCol - 1; j <= locCol + 1; j++)
  	  {
		//Reject current loc.
		if (i == location.getRow() && j == location.getCol())
		  continue;

  	    //Reject out of bounds.
  		if (i < 0 || j < 0 ||
  			i > sim.getRowCount() - 1 || j > sim.getColCount() - 1)
  		  continue;
  		
  		Location newLoc = new Location(i, j);
  		//Reject killer
  		if (sim.isKiller(newLoc))
  		  continue;

      // Reject expensive cells.
      if(sim.getMoveCost(newLoc) >= cost)
        continue;
  		
  		//Location is good, add to result.
  		result.add(newLoc);
  	  }
  	
  	if (result.isEmpty())
  	  return null;
  	return result;
  }

  public static List<Location> getRange(Simulation sim, PathOptions opt)
  {
    // Placeholder.
    return null;
  }

  public static Path getNearestCharger(Simulation sim, PathOptions opt)
  {
    Path result = null;
    Node2[][] G = genDijkstra(sim, opt);
    for(Location loc : sim.getChargers())
    {
      opt.end = loc;
      Path tmp = getPathFromTree(G, opt);
      if(result == null || (tmp != null && Path.compare(result, tmp, opt) < 0))
        result = tmp;
    }
    return result;
  }
  
  public static Path getNearestSurvivor(Simulation sim, PathOptions opt, int cutoff)
  {
    Path result = null;
    Node2[][] G = genDijkstra(sim, opt);
    for(int i = 0; i < sim.getRowCount(); i++)
      for(int j = 0; j < sim.getColCount(); j++)
      {
        if(i == opt.start.getRow() && j == opt.start.getCol()) continue;
        if(sim.getPercentage(i, j) >= cutoff)
        {
          opt.end = new Location(i, j);
          Path tmp = getPathFromTree(G, opt);
          if(result == null || (tmp != null && Path.compare(result, tmp, opt) < 0))
            result = tmp;
        }
      }
    return result;
  }
  
  
  public static Path getPath(Simulation sim, PathOptions opt)
  {
    //System.out.printf("Getting path to (%s, %s)\n", opt.start, opt.end);
    
    Path tmp = getPathFromTree(genDijkstra(sim, opt), opt);
    //if(tmp != null) System.out.println(tmp.toString());
    //else System.out.println("NULL");
    return tmp;
  }
  
   public static List<Path> getPaths(Simulation sim, PathOptions opt, List<Location> list)
  {
    Node2[][] G = genDijkstra(sim, opt);
    List<Path> result = new LinkedList<Path>();
    for(Location l : list)
    {
      opt.end = l;
      result.add(getPathFromTree(G, opt));
    }
    return result;
  }


	//rem: distance = path.size()
	private static class Path2 implements Comparable
	{
		public int distance;
		public int cost;
		public List<Location> path;
	
		public Path2(int distance, int cost, List<Location> path) 
		{
			this.distance = distance;
			this.cost = cost;
			this.path = path;
		}
	
		public void addTo(Path2 m)
		{
			distance = distance + m.distance;
			cost = cost + m.cost;
			path.addAll(m.path);
		}
	
		//lexicographic order
		public int compareTo(Object obj)
		{
      Path2 p = (Path2)obj;
			return (distance - p.distance == 0)? (cost - p.cost):(distance - p.distance);
		}
	
		//partial order
		public boolean leq(Path2 p)
		{
			return (distance <= p.distance && cost <= p.cost);
		}
	
		public boolean equals(Object obj)
		{
      Path2 p = (Path2)obj;
			return distance == p.distance && cost == p.cost;
		}
	
		public boolean gt(Path2 p)
		{
			return distance > p.distance && cost > p.cost;
		}
	}

	private static class Path2List implements Comparable
	{
		public List<Path2> list;
	
		/*rem =: the list is always suppose to be minimized  (ie any path of the 
		list can be compared with an other, the is no path with infinite componant,
		and the smallest distance is the first one) */
		public Path2List(List<Path2> list)
		{
			this.list = list;
		}
	
		public int compareTo(Object obj)
		{
      Path2List pl = (Path2List)obj;
			return list.get(0).compareTo(pl.list.get(0));
		}
	
		public void minimize()
		{
		if (!list.isEmpty()){
			Collections.sort(list);
			ListIterator<Path2> it = list.listIterator();
			int minc = (it.next()).cost;
			while(it.hasNext())
			{
			int c = (it.next()).cost;
			if (minc <= c)
				it.remove();
			else
				minc = c;
			}
		}
		}
	
		public void addTo(Path2List pl)
		{
      for(Path2 p : pl.list)
      {
        ArrayList<Location> path = new ArrayList<Location>();
        for(Location l : p.path)
        {
          path.add(l);
        }
        Path2 np = new Path2(p.distance, p.cost, path);
        list.add(np);
      }
			this.minimize();
		}
	
		public void addTo(Path2 p, int maxc)
		{
			ListIterator<Path2> it = list.listIterator();
			while (it.hasNext())
      {
        Path2 whatever = it.next();
        whatever.addTo(p);
        it.set(whatever);
      }
			it = list.listIterator();
			while (it.hasNext())
			{
				if(it.next().cost > maxc)
					it.remove();
			}
		}
	
		public void append(Path2 p)
		{
			list.add(p);
			this.minimize();
		}
		
		
		public void print(){
			for(Path2 p2 : this.list)
		  {
		    System.out.printf("  %d, %d, ",
		      p2.cost, p2.distance);
		    for(Location l : p2.path)
		      System.out.printf("%s ", l);
		    System.out.println();
		  }
		}
	}

	private static class Node2 implements Comparable
	{
		public Path2List list;
		public Path2List delta;
		public Location location;
		public boolean accessible;
		
		public Node2(Location location, Path2List list, Path2List delta)
		{
		  this.location = location;
		  this.list = list;
		  this.delta = delta;
		}
		
		public int compareTo(Object obj)
		{
      Node2 nd = (Node2)obj;
		  return list.compareTo(nd.list);
		}
	}


//I think it is not important but it is wrong to say that c(i,i) = move_cost
//(it should be 0... but in the same time I would have completely remove it
//if direction was not outputing it...
	private static int[][][] getAdj(Simulation sim, int max, int defval)
	{
		int M = sim.getRowCount();
		int N = sim.getColCount();
		int[][][] W = new int[M][N][8];
		  		 
		for(int i = 0; i < M; i++){
		for(int j = 0; j < N; j++)
		{
			for(Direction d : Direction.All())
			{
        if(d == Direction.STAY_PUT) continue;

		  	int k = i+d.getRowInc();
		  	int l = j+d.getColInc();
		  	W[i][j][d.getIndex()] = PathOptions.MAX;
		  	if (sim.getCell(k,l) != null){
		  		int c = defval;
		  		if (sim.getVisited(k,l)) 
		  			c = sim.getMoveCost(k,l);
		  		if (c < max && !sim.isKiller(k,l))
		  			W[i][j][d.getIndex()] = c; 
		  	}//else W = MAX
		  }
		}}
	
		return W;
	}

private static Node2[][] genDijkstra(Simulation sim, PathOptions opt)
{
	int M = sim.getRowCount();
	int N = sim.getColCount();
	int[][][] W = getAdj(sim, opt.maxCost, opt.unknownCellCost);

/*  for(int i = 0; i < M; i++)
  {
    for(int j = 0; j < N; j++)
      System.out.printf("%d ", W[i][j][0]);
    System.out.println();
  }
*/
	//int[][] W = clearGraph(sim, opt);
	PriorityQueue<Node2> Q = new PriorityQueue<Node2>();
	Node2[][] G = new Node2[M][N];
	for(int i = 0; i < M; i++)
    for(int j = 0; j < N; j++)
      G[i][j] = new Node2(new Location(i,j),
        new Path2List(new ArrayList<Path2>()), new Path2List(new ArrayList<Path2>()));
  BitSet isMarked = new BitSet(M*N);
  Node2 currentNode = G[opt.start.getRow()][opt.start.getCol()];
  currentNode.delta.list.add(new Path2(0,0,new ArrayList<Location>()));
  currentNode.list.list.add(new Path2(0,0,new ArrayList<Location>()));
  Q.add(currentNode);
  while(Q.size() > 0 && isMarked.cardinality() != M*N)
  {
  	currentNode = Q.remove();
  	int i = currentNode.location.getRow();
  	int j = currentNode.location.getCol();
  	isMarked.set(i*N+j);
    
//  System.out.printf("CurrentNode: %s\n", currentNode.location);
//  currentNode.delta.print();
     

  	if(isMarked.cardinality() != M*N)
  	{
			for (Direction d : Direction.All())
			{
        if(d == Direction.STAY_PUT) continue;

				int c = W[i][j][d.getIndex()];
				if (c < opt.maxCost) //both option check if there is an edge
				{
					Path2List tmpList = new Path2List(new ArrayList<Path2>());
          tmpList.addTo(currentNode.delta);

//       	System.out.printf("CurrentNode: %s\n", currentNode.location);
//       	currentNode.delta.print(); 


					int k = i+d.getRowInc();
					int l = j+d.getColInc();

//		    System.out.printf("tmpList1:\n");
//        tmpList.print();
          
					List<Location> edgeKL = new ArrayList<Location>();
          edgeKL.add(new Location(k,l));
					if (opt.shortest)
						tmpList.addTo(new Path2(1,c,edgeKL), opt.maxCost);
					else
						tmpList.addTo(new Path2(c,1,edgeKL), opt.maxLength);

//			  System.out.printf("tmpList2:\n");
//        tmpList.print();

					tmpList.addTo(G[k][l].list);

//		    System.out.printf("tmpList3:\n");
//        tmpList.print();

					Path2List tmpDelta = new Path2List(new ArrayList<Path2>());
          tmpDelta.list.addAll(tmpList.list);
					tmpDelta.list.removeAll(G[k][l].list.list);
					if (!(tmpDelta.list.isEmpty()))
					{
						G[k][l].list = tmpList;
						G[k][l].delta.addTo(tmpDelta);
						Q.add(G[k][l]);
					}
				}  			
			}
	  	currentNode.delta.list.clear();  
  	}
  }
  
	return G;
}


	private static Path getPathFromTree(Node2[][] G, PathOptions opt)
	{
		Node2 endNode = G[opt.end.getRow()][opt.end.getCol()];
		if (!(endNode.list.list.isEmpty()))
		{
			Path2 p = endNode.list.list.get(0);
			int cost = (opt.shortest)? p.cost : p.distance;
			return new Path(new LinkedList<Location>(p.path), cost);
		}
		else
			return null;
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
		  return delta - ((Node)obj).delta;
		}
		public boolean equals(Object obj)
		{
		  Node node = (Node)obj;
		  return location.equals(node.location);
		}
	}


//don't know if useful since graph is not very dense: E = 8*|V|/2 = 4|V|
//use node but not with the right semantic : location store a pair of node index
//(i,j) of the graph; node store all information for a path from i to j; in particulat
//predecessor gives how to break the path from i to j (gives the first part of the path:
//i to k --> second part is k to j); cost store the cost of the entire path and distance
//the one of the distance, even if not used. Delta is a bit redundant...
	private static Node[][] floydWarshall(Simulation sim, boolean costopt, int defval)
	{
		int M = sim.getRowCount();
		int N = sim.getColCount();
		Node[][] W = new Node[M*N][M*N];
				
		for(int i = 0; i < M; i++){
		for(int j = 0; j < N; j++)
		{
			for(int k = 0; k < M*N; k++)
			{
				W[i*N+j][k] = new Node(W[i*N+j][k], new Location(i*N+j,k), PathOptions.MAX, PathOptions.MAX, PathOptions.MAX);
			}
			for(int k1 = Math.max(-1, -i); k1 < Math.min(2, M-i); k1++){
			for(int k2 = Math.max(-1, -j); k2 < Math.min(2, N-j); k2++)
			{
				W[i*N+j][(i+k1)*N+j+k2].cost = defval;
				if (sim.getVisited(i + k1, j + k2))
					W[i * N + j][(i + k1) * N + j + k2].cost = sim.getMoveCost(i + k1, j + k2);

				W[i * N + j][(i + k1) * N + j + k2].distance = (sim.isKiller(i + k1, j + k2)) ? PathOptions.MAX : 1;

				if (costopt)
					W[i*N+j][(i+k1)*N+j+k2].delta = W[i*N+j][(i+k1)*N+j+k2].cost;
				else
					W[i*N+j][(i+k1)*N+j+k2].delta = W[i*N+j][(i+k1)*N+j+k2].distance;
			}}
			W[i*N+j][i*N+j].cost = 0;
			W[i*N+j][i*N+j].distance = 0;
			W[i*N+j][i*N+j].delta = 0;
		}}//to check: move cost killer = highest ; stay on place does not cost anything
	
		for(int k = 0; k < N*M; k++)
		{	
			for(int i = 0; i < N*M; i++){
			for(int j = 0; j < N*M; j++)
			{
				if(W[i][k].delta != PathOptions.MAX/2 && W[k][j].delta != PathOptions.MAX/2 && 
				W[i][j].delta > W[i][k].delta + W[k][j].delta)
				{
					W[i][j].predecessor = W[i][k];
					W[i][j].cost = W[i][k].cost + W[k][j].cost;
					W[i][j].distance = W[i][k].distance + W[k][j].distance;
					W[i][j].delta = W[i][k].delta + W[k][j].delta;
				}
			}}
		}
	
		return W;
	}

	private static int[][] clearGraph(Simulation sim, PathOptions opt){
		int M = sim.getRowCount();
		int N = sim.getColCount();
		int s = opt.start.getRow()*N + opt.start.getCol();
		int t = opt.end.getRow()*N + opt.end.getCol();
	
		//e, deceive, or intimidate.shortest => cost constraint
		int max = (opt.shortest)? opt.maxCost:opt.maxLength;  
		Node[][]C = floydWarshall(sim, opt.shortest, opt.unknownCellCost); 
		int[][]W = new int[M*N][8];
		for(int ij = 0; ij < M*N; ij++)
			for(int k = 0; k < 9; k++)
		  	W[ij][k] = PathOptions.MAX;
		  		 
		for(int i = 0; i < M; i++){
		for(int j = 0; j < N; j++)
		{
      int cost1 = C[s][i * N + j].delta;
      int cost2 = C[i * N + j][t].delta;
      if(cost1 < max && cost2 < max && cost1 + cost2 > 0 && cost1 + cost2 < max)
			{
				for(Direction d : Direction.All())
		  	{
		    	int k = i+d.getRowInc();
		    	int l = j+d.getColInc();
		    	if (sim.getCell(k,l) != null && !sim.isKiller(k,l)){ 
		    	//assumes that not null <=> 0<=k1<M and 0<=k2<N
		    		int c = opt.unknownCellCost;
		    		if (sim.getVisited(k,l))
		    			c = sim.getMoveCost(k,l);
            int c1 = opt.shortest ? c : 1;
            cost1 = C[k * N + l][t].delta;
            cost2 = C[s][i * N + j].delta;
            if (c1 < max && cost1 < max && cost2 + cost1 + c < max && cost1 + cost2 > 0)
		    			W[i*N+j][d.getIndex()] = c;
		    	}
					//else can remove the adge ie let W = MAX
				}
			}
			//else can remove node ie let W = MAX
		}}
	
		return W;
	}
	
}
