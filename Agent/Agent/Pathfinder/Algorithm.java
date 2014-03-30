
import Agent.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import java.util.*;

public static class Algorithm
{
	//TO CHECK: comparator good order
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
			path.add(p.path);
		}
	
		//lexicographic order
		public int compareTo(Path2 p)
		{
			return (distance - p.distance == 0)? (cost - p.cost):(distance - p.distance);
		}
	
		//partial order
		public boolean leq(Path2 p)
		{
			return (distance <= p.distance && cost <= p.cost);
		}
	
		public boolean equals(Path2 p)
		{
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
	
		//rem =: the list is always suppose to be minimized (ie any path of the list can be compared with an other, the is no path with infinite componant, and the smallest distance is the first one)
		public Path2List(List<Path2> list)
		{
			this.list = list;
		}
	
		public int compareTo(Path2List pl)
		{
			return list.get(0).compareTo(pl.list.get(0));
		}
	
		public void minimize()
		{
		if (!list.isEmpty()){
			sort(list);
			listIterator it = list.listIterator();
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
			list.add(pl.list);
			this.minimize();
		}
	
		public void addTo(Path2 p, int maxc)
		{
			listIterator it = list.listIterator();
			while (it.hasNext())
				it.set(it.next().addTo(p));
			it = list.listIterator;
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
		
		public int compareTo(Node2 nd)
		{
		  return list.compareTo(nd.list);
		}
	}


//I think it is not important but it is wrong to say that c(i,i) = move_cost (it should be 0... but in the same time I would have completely remove it if direction was not outputing it...
	private static int[][] getAdj(Simulation sim, int max, int defval)
	{
		int M = sim.getRowCount();
		int N = sim.getColCount();
		int[][]W = new int[M*N][7];
		  		 
		for(int i = 0; i < M; i++){
		for(int j = 0; j < N; j++)
		{
			for(Direction d : Direction.All())
			{
		  	int k = i+d.getRowInc();
		  	int l = j+d.getColInc();
		  	W[i*N+j][d.index()] = MAX;
		  	if (sim.getCell(k,l) != null){
		  		int c = defval;
		  		if (sim.getVisited(k,l)) 
		  			c = sim.getMoveCost(k,l);
		  		if (c < max && !sim.isKiller(k,l))
		  			W[i*N+j][d.index()] = c; 
		  	}//else W = MAX
		  }
		}}
	
		return W;
	}

private static Node2[][] genDijkstra(Simulation sim, PathOptions opt)
{
	int M = sim.getRowCount();
	int N = sim.getColCount();
	int[][] W = getAdj(sim, opt.maxCost, opt.unknownCellCost);
	//int[][] W = clearGraph(sim, opt);
	PriorityQueue<Node2> Q = new PriorityQueue<Node2>();
	Node2[][] G = new Node2[M][N];
	for(int i = 0; i < M; i++)
    for(int j = 0; j < N; j++)
      G[i][j] = new Node2(new Location(i,j), null, null);
  BitSet[] isMarked = new BitSet(M*N);
  Node2 currentNode = G[opt.start.getRow()][opt.start.getCol()];
  currentNode.delta.list.add(new Path2(0,0,new ArrayList<Location>
  (new Location(opt.start.getRow(),opt.start.getCol()))));
  currentNode.list.list.add(new Path2(0,0,new ArrayList<Location>
  (new Location(opt.start.getRow(),opt.start.getCol()))));
  Q.add(currentNode);
  while(Q.size() > 0 && isMarked.cardinality() != M*N)
  {
  	currentNode = Q.remove();
  	int i = currentNode.location.getRow();
  	int j = currentNode.location.getCol();
  	isMarked.set(i*N+j);
  	if(isMarked.cardinality() != M*N)
  	{
			for (Direction d : Direction.All())
			{
				int c = W[i*N+j][d.getIndex()];
				if (c < opt.maxCost) //both option check if there is an edge
				{
					Path2List tmpList = new ArrayList(currentNode.delta);
					int k = i+d.getRowInc();
					int l = j+d.getColInc();
					List<Location> edgeKL = new ArrayList<Location>(new Location(k,l));
					if (shortest)
						tmpList.addTo(new Path2(1,c,edgeKL), opt.maxCost);
					else
						tmpList.addTo(new Path2(c,1,edgeKL), opt.maxLength);
					tmpList.addTo(G[k][l].list);
					Path2List tmpDelta = new ArrayList(tmpList);
					tmpDelta.removeAll(G[k][l].list);
					if (!(tmpDelta.isempty()))
					{
						G[k][l].list = templist;
						G[k][k].delta.addTo(tmpDelta);
						Q.add(G[k][l]);
					}
				}  			
			}
	  	currentNode.delta.clear();  
  	}
  }
  
	return G;
}

//I don't know what are the default values
	private static Path getPathFromTree(Node2[][] G, PathOptions opt)
	{
		Node2 endNode = G[opt.end.getRow()][opt.end.getCol()];
		if (!(endNode.list.isEmpty()))
		{
			Path2 p = endNode.list.get(0);
			return new Path(new LinkedList<Location>(p.path), p.cost);
		}
		else
			return new Path(new LinkedList<Location>(), MAX);
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
//use node but not with the right semantic : location store a pair of node index (i,j) of the graph; node store all information for a path from i to j; in particulat predecessor gives how to break the path from i to j (gives the first part of the path : i to k --> second part is k to j); cost store the cost of the entire path and distance the one of the distance, even if not used. Delta is a bit redundant...
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
				W[i*N+j][k] = new Node(W[i*N+j][k], new Location(i*N+j,k), MAX, MAX, MAX);
			}
			for(int k1 = Math.max(-1, -i); k1 < Math.min(2, M-i); k1++){
			for(int k2 = Math.max(-1, -j); k2 < Math.min(2, N-j); k2++)
			{
				W[i*N+j][(i+k1)*N+j+k2].cost = defval;
				if (sim.getVisited(k,l))
					W[i*N+j][(i+k1)*N+j+k2].cost = sim.getMoveCost(i+k1, j+k2);
				W[i*N+j][(i+k1)*N+j+k2].distance = (sim.isKiller(i+k1, j+k2))? MAX:1;
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
				if(W[i][k].delta != MAX && W[k][j].delta != MAX && 
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

//don't know if it's a good idea because makes the graph very specific (from s to t)
//return "weight" between two edges (= adjacents cell) no matter what the option is, since in any case we need both weight and distance, and since distances are implicit (= 1 if weight != MAX ; = MAX = infinity = no edge otherwise)
//rem: the trouble with MAX is that we can't add two values without checking first that there are not infifinity because can cause overflow? --> or maybe just MAX-1 which would not be too bad... TO CHECK (and if ir's ok remove extra conditions)
	private static int[][] clearGraph(Simulation sim, PathOptions opt){
		int M = sim.getRowCount();
		int N = sim.getColCount();
		int s = opt.start.getRow()*N + opt.start.getCol();
		int t = opt.end.getRow()*N + opt.end.getCol();
	
		//shortest => cost constraint
		int max = (opt.shortest)? opt.maxCost:opt.maxLength;  
		Node[][]C = floydWarshall(sim, opt.shortest, opt.unknownCellCost); 
		int[][]W = new int[M*N][8];
		for(int ij = 0; ij < M*N; ij++)
			for(int k = 0; k < 9; k++)
		  	G[ij][k] = MAX;
		  		 
		for(int i = 0; i < M; i++){
		for(int j = 0; j < N; j++)
		{
			if (C[s][i*N+j] < max && C[i*N+j][t] < max && C[s][i*N+j] + C[i*N+j][t] < max)
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
		    		if (c < max && C[k*N+l][t] < max && C[s][i*N+j]+c+C[k*N+l][t] < max)
		    			W[i*N+j][d.index()] = c;
		    	}
					//else can remove the adge ie let W = MAX
				}
			}
			//else can remove node ie let W = MAX
		}}
	
		return W;
	}
	
}

