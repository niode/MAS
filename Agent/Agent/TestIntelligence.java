package Agent;

import Agent.*;
import Agent.Core.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import java.util.List;
import java.util.ArrayList;

public class TestIntelligence extends Intelligence
{
  private List<AgentCommand> list = new ArrayList<AgentCommand>();

  public TestIntelligence(Simulation sim, Communicator com, BaseAgent base)
  {
    super(sim, com,base);
    list.add(new SLEEP());
    list.add(new OBSERVE(new Location(0, 0)));
    list.add(new SAVE_SURV());
    list.add(new TEAM_DIG());
    list.add(new MOVE(Direction.SOUTH_WEST));
    AgentIDList idList = new AgentIDList();
    idList.add(new AgentID(0, BaseAgent.getBaseAgent().getAgentID().getGID()));
    list.add(new SEND_MESSAGE(idList, "This is a test"));
  }

  public void think()
  {
    BaseAgent agent = BaseAgent.getBaseAgent();
    base.log(LogLevels.Always, "Thinking");
    AgentCommand command = list.get(sim.getRound() % list.size());
  }
}
