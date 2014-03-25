package Agent.Role.TeamRules;

import java.util.*;
import Agent.*;
import Agent.Pathfinder.*;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.*;
import Agent.Role.TeamRules.*;
import Agent.Role.*;
import Ares.AgentID;

public class TeamFinder
{
  Simulation sim;
  AgentID teammate;
  List<Team> teams;
  long round;

  public TeamFinder(Simulation sim)
  {
    this.sim = sim;
    teammate = null;
    teams = null;
    round = -1;
  }

  public List<Team> getTeams()
  {
    if(round == sim.getRound()) return teams;
    else getTeammate();
    return teams;
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
    if(round == sim.getRound()) return teammate;
    teammate = null;
    teams = null;

    Path path;
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);
    List<AgentID> team = sim.getTeammates(Role.ID.TEAM);
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
            dist[i][j] = Long.MAX_VALUE;
          else
            dist[i][j] = path.getLength();
        }
      }
    }

    TeamCost[] cache = new TeamCost[1 << team.size()];
    TeamCost test = cost(cache, 0, dist, team.size());
    teams = new LinkedList<Team>();
    for(PartialTeam t: test.team)
    {
      if(t.a < t.b)
        teams.add(new Team(team.get(t.a), team.get(t.b)));
      else
        teams.add(new Team(team.get(t.b), team.get(t.a)));

      if(t.a == selfIndex)
        teammate = team.get(t.b);
      else if(t.b == selfIndex)
        teammate = team.get(t.a);
    }
    round = sim.getRound();

    // Test
    for(Team t : teams)
      System.out.printf("%s ", t);
    System.out.println();

    return teammate;
  }
 
  // Recursively find the cost of selecting a pair to be a team.
  private TeamCost cost(TeamCost[] cache, int picked, long[][] dist, int num)
  {
    if(num == 1 || num == 0) return new TeamCost(0);
    if(cache[picked] != null) return cache[picked];

    TeamCost min = new TeamCost(Long.MAX_VALUE);
    for(int i = 0; i < dist.length; i++)
    {
      if(((1 << i) & picked) > 0) continue;

      for(int j = 0; j < dist[i].length; j++)
      {
        if(((1 << j) & picked) > 0) continue;

        if(i != j && dist[i][j] < min.cost)
        {
          if(dist[i][j] == Long.MAX_VALUE) continue;

          int next = picked | (1 << i) | (1 << j);
          TeamCost tmp = cost(cache, next, dist, num - 2);

          if(dist[i][j] == Long.MAX_VALUE || dist[i][j] + tmp.cost < 0) continue;
          else if(dist[i][j] + tmp.cost < min.cost)
          {
            min = tmp;
            tmp = new TeamCost(dist[i][j]);
            tmp.team.add(new PartialTeam(i,j));
            min.add(tmp);
          }
        }
      }
    }

    min = min.cost < Long.MAX_VALUE ? min : new TeamCost(0);
    cache[picked] = min;
    return min;
  }

  private static class TeamCost
  {
    public long cost;
    public LinkedList<PartialTeam> team;
    public TeamCost(long c)
    {
      team = new LinkedList<PartialTeam>();
      cost = c;
    }

    public void add(TeamCost other)
    {
      team.addAll(other.team);
      cost += other.cost;
    }
  }

  private static class PartialTeam
  {
    public int a;
    public int b;
    public PartialTeam(int a, int b)
    {
      this.a = a;
      this.b = b;
    }
  }

  public static class Team
  {
    public AgentID low;
    public AgentID high;
    public Team(AgentID low, AgentID high)
    {
      this.low = low;
      this.high = high;
    }

    public String toString()
    {
      return "(" + low.getID() + ", " + high.getID() + ")";
    }
  }
}
