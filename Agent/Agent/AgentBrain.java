package Agent;

import Agent.Core.*;
import Ares.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import Ares.Commands.AresCommands.*;
import java.util.ArrayList;
import java.util.List;

public class AgentBrain extends Brain {

    private static BaseAgent base;
    private Simulation sim = new Simulation();
    private Intelligence ai = new Intelligence(sim);
    private Communicator com = new Communicator(base, sim);
    private AgentID id;

    public AgentBrain() {
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
        com.receive(fwd_message);
    }

    @Override
    public void handleMoveResult(MOVE_RESULT move_result) {
        BaseAgent.log(LogLevels.Always, "MOVE_RESULT:" + move_result);
        BaseAgent.log(LogLevels.Test, "" + move_result);
        sim.update(move_result.getSurroundInfo());
        sim.update(id, move_result.getEnergyLevel());
    }

    @Override
    public void handleObserveResult(OBSERVE_RESULT observe_result) {
        BaseAgent.log(LogLevels.Always, "OBSERVE_RESULT:" + observe_result);
        BaseAgent.log(LogLevels.Always, observe_result.getEnergyLevel() + "");
        BaseAgent.log(LogLevels.Always, observe_result.getLifeSignals() + "");
        BaseAgent.log(LogLevels.Always, observe_result.getTopLayerInfo() + "");
        BaseAgent.log(LogLevels.Test, "" + observe_result);
        CellInfo info = observe_result.getTopLayerInfo();
        sim.update(info);
        sim.update(info.getLocation(), observe_result.getLifeSignals());
        sim.update(id, observe_result.getEnergyLevel());
    }

    @Override
    public void handleSaveSurvResult(SAVE_SURV_RESULT save_surv_result) {
        BaseAgent.log(LogLevels.Always, "SSAVE_SURV_RESULT:" + save_surv_result);
        BaseAgent.log(LogLevels.Test, "" + save_surv_result);
        sim.update(id, save_surv_result.getEnergyLevel());
        sim.update(save_surv_result.getSurroundInfo());
    }

    @Override
    public void handleTeamDigResult(TEAM_DIG_RESULT team_dig_result) {
        BaseAgent.log(LogLevels.Always, "TEAM_DIG_RESULT:" + team_dig_result);
        BaseAgent.log(LogLevels.Test, "" + team_dig_result);
        sim.update(id, team_dig_result.getEnergyLevel());
        sim.update(team_dig_result.getSurroundInfo());
    }

    @Override
    public void think() {
        BaseAgent.log(LogLevels.Always, "Thinking");
    }
}
