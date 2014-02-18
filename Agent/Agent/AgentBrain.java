package Agent;

import Agent.Core.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import Ares.Commands.AresCommands.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgentBrain extends Brain {

    private Random random = new Random(12345);
    private int round = 1;
    private static BaseAgent agent = BaseAgent.getBaseAgent();
    private List<AgentCommand> list = new ArrayList<>();

    public AgentBrain() {
        list.add(new SLEEP());
        list.add(new OBSERVE(new Location(2, 2)));
        list.add(new SAVE_SURV());
        list.add(new TEAM_DIG());
        list.add(new MOVE(Direction.SOUTH_WEST));
        AgentIDList agent_id_list = new AgentIDList();
        list.add(new SEND_MESSAGE(agent_id_list, "This is a test"));
    }

    @Override
    public void handleDisconnect() {
        BaseAgent.log(LogLevels.Always, "DISCONNECT");
    }

    @Override
    public void handleDead() {
        BaseAgent.log(LogLevels.Always, "DEAD");
    }

    @Override
    public void handleFwdMessage(FWD_MESSAGE fwd_message) {
        BaseAgent.log(LogLevels.Always, "FWD MESSAGE:" + fwd_message);
        BaseAgent.log(LogLevels.Test, "" + fwd_message);
    }

    @Override
    public void handleMoveResult(MOVE_RESULT move_result) {
        BaseAgent.log(LogLevels.Always, "MOVE_RESULT:" + move_result);
        BaseAgent.log(LogLevels.Test, "" + move_result);
    }

    @Override
    public void handleObserveResult(OBSERVE_RESULT observe_result) {
        BaseAgent.log(LogLevels.Always, "OBSERVE_RESULT:" + observe_result);
        BaseAgent.log(LogLevels.Always, observe_result.getEnergyLevel() + "");
        BaseAgent.log(LogLevels.Always, observe_result.getLifeSignals() + "");
        BaseAgent.log(LogLevels.Always, observe_result.getTopLayerInfo() + "");
        BaseAgent.log(LogLevels.Test, "" + observe_result);
    }

    @Override
    public void handleSaveSurvResult(SAVE_SURV_RESULT save_surv_result) {
        BaseAgent.log(LogLevels.Always, "SSAVE_SURV_RESULT:" + save_surv_result);
        BaseAgent.log(LogLevels.Test, "" + save_surv_result);
    }

    @Override
    public void handleTeamDigResult(TEAM_DIG_RESULT team_dig_result) {
        BaseAgent.log(LogLevels.Always, "TEAM_DIG_RESULT:" + team_dig_result);
        BaseAgent.log(LogLevels.Test, "" + team_dig_result);
    }

    @Override
    public void think() {
        BaseAgent.log(LogLevels.Always, "Thinking");
        AgentCommand command = list.get(round % list.size());
        if (command instanceof SEND_MESSAGE) {
            SEND_MESSAGE send_message = (SEND_MESSAGE) command;
            if (send_message.getAgentIDList().isEmpty()) {
                send_message.getAgentIDList().add(new AgentID(0, agent.getAgentID().getGID()));
            }
        }
        if (command instanceof OBSERVE) {
            int row_size = getWorld().getGrid().length;
            int row = random.nextInt(row_size);
            int col_size = getWorld().getGrid()[row].length;
            int col = random.nextInt(col_size);
            command = new OBSERVE(new Location(row, col));
        } else if (command instanceof MOVE) {
            command = new MOVE(getDirection(random.nextInt(9)));
        }
        BaseAgent.log(LogLevels.Always, "Sending " + command);
        agent.send(command);
        BaseAgent.getBaseAgent().send(new END_TURN());
        round++;
    }

    public static Direction getDirection(int index) {
        if (index == 0) {
            return Direction.STAY_PUT;
        } else if (index == 1) {
            return Direction.NORTH;
        } else if (index == 2) {
            return Direction.NORTH_EAST;
        } else if (index == 3) {
            return Direction.EAST;
        } else if (index == 4) {
            return Direction.SOUTH_EAST;
        } else if (index == 5) {
            return Direction.SOUTH;
        } else if (index == 6) {
            return Direction.SOUTH_WEST;
        } else if (index == 7) {
            return Direction.WEST;
        } else if (index == 8) {
            return Direction.NORTH_WEST;
        } else {
            return Direction.UNKNOWN;
        }
    }
}
