/**
 * 
 */
package Agent.Role;

import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Ares.Direction;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;
import Ares.Commands.AgentCommands.SAVE_SURV;
import Ares.Commands.AgentCommands.TEAM_DIG;
import Ares.World.Cell;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.Survivor;
import Ares.World.Objects.WorldObject;

/**
 * Basic role for exploration. The agent will not cooperate, but encountering situations that
 * require cooperation may result in a role change.
 * 
 * @author Daniel
 */
public class ExplorerRole extends Role
	{
	boolean onFirstMove = true;

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
	 * @see Agent.Intelligence#think()
	 */
	@Override
	public void think()
		{
		//If on the first move, stay put to get neighbor info.
		if (onFirstMove)
			{
			onFirstMove = false;
			AgentCommand stay = new MOVE(Direction.STAY_PUT);
			getCommunicator().send(stay);
			return;
			}
		
		//First move done, get current location, cell and top layer.
		Location currentLoc = getSimulation().getSelf().getLocation();
		Cell currentCell = getSimulation().getCell(currentLoc);
		WorldObject topLayer = currentCell.getTopLayer();
		
		//If on a survivor, save them.
		if (topLayer instanceof Survivor)
			{
			getCommunicator().send(new SAVE_SURV());
			return;
			}
		
		//If already on rubble pile or with an agent, dig.
		if (topLayer instanceof Rubble)
			{
			Rubble rubble = (Rubble)topLayer;
			
			if (rubble.getRemoveAgents() == 1)
				{
				//Dig if only one agent needed.
				getCommunicator().send(new TEAM_DIG());
				return;
				}
			else
				{
				//Rubble must require 2 agents, dig if another there.
				if (currentCell.getCellInfo().getAgentIDList().size() > 1)
					{
					getCommunicator().send(new TEAM_DIG());
					return;
					}
				}
			}
		
		//Get path to nearest survivor.
		//We could save this, but we'll recalculate each turn.
		PathOptions opt = new PathOptions(currentLoc);
		Path nearestSurvPath = Pathfinder.getNearestSurvivor(getSimulation(), opt);
		
		//Get next location in path and move to it.
		Location moveTo = nearestSurvPath.getNext();
		AgentCommand move = new MOVE(Pathfinder.getDirection(currentLoc, moveTo));
		getCommunicator().send(move);
		}

	}
