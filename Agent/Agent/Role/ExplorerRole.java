/**
 * 
 */
package Agent.Role;

import java.util.ArrayList;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Core.LogLevels;
import Agent.Pathfinder.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Objects.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

/**
 * Basic role for exploration. The agent will not cooperate, but encountering situations that
 * require cooperation may result in a role change.
 * 
 * @author Daniel
 */
public class ExplorerRole extends Role
	{

	/**
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 */
	public ExplorerRole(Simulation sim, Communicator com, BaseAgent base)
		{
		// These are still accessible from the superclass getter methods!
		super(sim, com, base);
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Role#setupRules(java.util.ArrayList)
	 */
	@Override
	public void setupRules(ArrayList<Rule> rules)
		{
		rules.add(new RuleSaveSurvivor());
		rules.add(new RuleChargeRequired());
		rules.add(new RuleCanDig());
		}

	@Override
	public void noRuleMatch()
		{
		Location currentLoc = getSimulation().getSelf().getLocation();
		
		//Get path to nearest survivor.
		//We could save this, but we'll recalculate each turn.
		PathOptions opt = new PathOptions(currentLoc);
		Path nearestSurvPath = Pathfinder.getNearestSurvivor(getSimulation(), opt, 0);
		
    if(nearestSurvPath.getLength() > 0)
    {
      //Get next location in path and move to it.
      Location moveTo = nearestSurvPath.getNext();
      AgentCommand move = new MOVE(Pathfinder.getDirection(currentLoc, moveTo));
      getCommunicator().send(move);
    } else
    {
      if(getSimulation().getTopLayer(getSimulation().getSelf().getLocation()) instanceof Rubble)
      {
        getCommunicator().send(new TEAM_DIG());
      } else
      {
        getCommunicator().send(new SAVE_SURV());
      }
    }
		}

  public String toString()
  {
    return "ExplorerRole";
  }

}
