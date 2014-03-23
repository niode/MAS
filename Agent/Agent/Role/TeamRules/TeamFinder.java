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

  /*
   * This function will calculate the best allocation of teams
   * based on which agents are currently in a TeamRole.
   * The function relies on the fact that all agents will
   * come up with the exact same result when they run the
   * algorithm, so distance is used instead of move cost.
   */
  public AgentID getTeammate()
  {
    // Check if the teammate has been calculated already.
    if(teammate != null) return teammate;

    Path path;
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);
    List<AgentID> team = sim.getTeammates();
    int selfIndex = 0;
    long[][] dist = new long[team.size()][team.size()];

    // Find the distances between each two agents.
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
            dist[i][j] = path.getLength();
        }
      }
    }

    // Brute-force the best possible teammate.
    long minCost = Long.MAX_VALUE;

    // Initialize cache.
    long[] cache = new long[1 << team.size()];
    for(int i = 0; i < cache.length; i++)
      cache[i] = -1;

    // Precalculate the best solution.
    long min = cost(cache, 0, dist, team.size());

    // Check which partner produces the best solution.
    int teamIndex = -1;
    for(int i = 0; i < team.size(); i++)
    {
      if(cache[(1 << selfIndex) | (1 << i)] < 0) continue;
      if(i != selfIndex && dist[selfIndex][i] + cache[(1 << selfIndex) | (1 << i)] == min)
      {
        teamIndex = i;
        break;
      }
    }

    if(teamIndex == -1) return null;

    teammate = team.get(teamIndex);
    System.out.println("Teammate: " + teammate.getID());
    return teammate;
  }
 
  // Recursively find the cost of selecting a pair to be a team.
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
    cache[picked] = min;
    return min;
  }
}
