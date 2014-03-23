package Agent.Role.TeamRules;

import java.util.*;
import Agent.*;
import Agent.Pathfinder.*;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.*;
import Agent.Role.TeamRules.*;
import Ares.AgentID;

public class TeamFinder
{
  Simulation sim;
  AgentID teammate;

  public TeamFinder(Simulation sim)
  {
    this.sim = sim;
    teammate = null;
  }

  public AgentID getTeammate()
  {
    if(teammate != null) return teammate;

    Path path;
    PathOptions opt = new PathOptions(PathOptions.CHEAPEST & PathOptions.WITHIN_RANGE);
    List<AgentID> team = sim.getTeammates();
    int selfIndex = 0;
    long[][] dist = new long[team.size()][team.size()];

    for(int i = 0; i < team.size(); i++)
    {
      if(team.get(i).equals(sim.getSelfID())) selfIndex = i;
      for(int j = 0; j < team.size(); j++)
      {
        if(i == j)
        {
          dist[i][j] = 0;
        } else
        {
          opt.start = sim.getAgentLocation(team.get(i));
          opt.end = sim.getAgentLocation(team.get(j));
          path = Pathfinder.getPath(sim, opt);
          if(path == null)
            dist[i][j] = Integer.MAX_VALUE;
          else
            dist[i][j] = path.getMoveCost();
        }
      }
    }

    long minCost = Long.MAX_VALUE;
    int teamIndex = 0;
    for(int i = 0; i < team.size(); i++)
    {
      if(i == selfIndex) continue;
      long tmp = getTeamCost(dist, team.size(), selfIndex, i);
      if(tmp < minCost)
      {
        minCost = tmp;
        teamIndex = i;
      }
    }

    teammate = team.get(teamIndex);
    return teammate;
  }

  private long getTeamCost(long[][] dist, int num, int a, int b)
  {
    int picked = (1 << a) | (1 << b);
    long[] cache = new long[1 << num];
    for(int i = 0; i < cache.length; i++)
      cache[i] = -1;
    return cost(cache, picked, dist, num - 2);
  }
 
  private long cost(long[] cache, int picked, long[][] dist, int num)
  {
    if(num == 1 || num == 0) return 0;
    if(cache[picked] >= 0) return cache[picked];

    long min = Integer.MAX_VALUE;
    for(int i = 0; i < dist.length; i++)
    {
      if(((1 << i) & picked) == (1 << i)) continue;

      for(int j = 0; j < dist[i].length; j++)
      {
        if(((1 << j) & picked) == (1 << j)) continue;

        if(i != j && dist[i][j] < min)
        {
          long tmp = cost(cache, picked | (1 << i) | (1 << j), dist, num - 2);
          if(dist[i][j] + tmp < 0) continue;
          else if(dist[i][j] + tmp < min) min = dist[i][j] + tmp;
        }
      }
    }
    return min;
  }
}
