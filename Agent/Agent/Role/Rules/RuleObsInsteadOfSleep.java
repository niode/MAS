package Agent.Role.Rules;
import Agent.Communicator;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
import Agent.Role.Rules.*;
import Agent.Simulation;
import Ares.Commands.AgentCommand;
import Ares.Location;
import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import Ares.Commands.AgentCommands.OBSERVE;

/**
 * Created by alexo on 2014-04-03.
 */
public class RuleObsInsteadOfSleep implements Rule {


    @Override
    public boolean checkConditions(Simulation sim) {
        // we're assuming no actions used here
        return true;
    }

    @Override
    public AgentCommand doAction(Simulation sim, Communicator com) {

        List<Location> potentialScans = Arrays.asList((Location[])sim.getUnvisited().toArray()) ;
        ArrayList<Location> PS = new ArrayList<Location>(potentialScans);

        while(PS.size() != 0){

            int selection = (int)(Math.random()*(PS.size()-1));
            if (!sim.getCell(PS.get(selection)).isKiller() &&
                    !sim.getCell(PS.get(selection)).isOnFire() ) {
                return new OBSERVE(PS.get(selection));
            }else {
                PS.remove(selection);
            }

        }

        return null;
    }

    @Override
    public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base) {
        return null;
    }
}
