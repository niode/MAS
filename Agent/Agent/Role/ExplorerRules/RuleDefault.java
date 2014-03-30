package Agent.Role.ExplorerRules;

import java.util.*;
import Agent.*;
import Agent.Core.*;
import Agent.Pathfinder.*;
import Agent.Role.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;

public class RuleDefault implements Rule
{
  public boolean checkConditions(Simulation sim)
  {
    return true;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
		//Observe the cell with the highest % that is still <100.
		
		int highestPercent = 0;
		Location highestLocation = null; 
		for (int i = 0; i <= sim.getRowCount(); i++)
			for (int j = 0; j <= sim.getColCount(); j++)
      {
				int percent = sim.getPercentage(i, j);
				if (percent < 100 && percent > highestPercent)
        {
					highestPercent = percent;
					highestLocation = new Location(i, j);
        }
      }
		
		if (highestLocation != null)
    {
			com.send(new OBSERVE(highestLocation));
    } else
    {
			//Not sure what to do if no places worth observing. Just move to random nearby cell.
			Location current = sim.getAgentLocation(sim.getSelfID());
			Object[] valid = Pathfinder.getValidNeighbors(sim, current).toArray();
			Random rand = new Random();
			Location target = (Location)valid[rand.nextInt(valid.length)];
			if (target.equals(current))
        return new SLEEP();
			else
        return new MOVE(Pathfinder.getDirection(current, target));
    }
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
