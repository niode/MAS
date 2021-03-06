package Agent;

import Agent.Core.*;
import Agent.SAP.*;
import Agent.Role.*;

public class Main {

    public static void main(String[] args) {
        if (args.length >= 2) {
            BaseAgent.setLogLevel(LogLevels.All);

            Simulation sim = new Simulation();
            Communicator com = new Communicator(BaseAgent.getBaseAgent(), sim);
            //Intelligence ai = new TestIntelligence(sim, com);
            //Intelligence ai = new Agent.SAP.Intelligence(sim, com, BaseAgent.getBaseAgent());
            Intelligence ai = new RoleIntelligence(sim, com, BaseAgent.getBaseAgent());
            Brain brain = new Brain(BaseAgent.getBaseAgent(), ai, sim, com);

            BaseAgent.getBaseAgent().start(args[0], args[1], brain);
        } else {
            System.out.println("Agent: usage java TestAgent <hostname> <groupname>");
        }
    }
}
