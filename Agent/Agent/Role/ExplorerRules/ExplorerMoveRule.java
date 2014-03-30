package Agent.Role.ExplorerRules;

import Agent.*;
import Agent.Core.*;
import Agent.Pathfinder.*;
import Agent.Role.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import Ares.World.*;
import Ares.World.Objects.*;
import java.util.*;

public class ExplorerMoveRule implements Rule
{
  Path path = null;

  private ExplorerState state;
  public ExplorerMoveRule(ExplorerState state)
  {
    super();
    this.state = state;
  }

  public boolean checkConditions(Simulation sim)
  {
    if(state.target == null) return false;

    // Get the path.
    PathOptions opt = new PathOptions(PathOptions.SHORTEST & PathOptions.WITHIN_RANGE);
    opt.start = sim.getAgentLocation(sim.getSelfID());
    opt.end = state.target;
    path = Pathfinder.getPath(sim, opt);

    // If the path is null, some new knowledge of the world has
    // invalidated the target.
    if(path == null) return false;

    // Move if the target is not null and the target is not the
    // current location.
    return !state.target.equals(sim.getAgentLocation(sim.getSelfID()));
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    Location start = sim.getAgentLocation(sim.getSelfID());
    Location end = path.getNext();

    return new MOVE(Pathfinder.getDirection(start, end));
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
