package Agent;

import Agent.Core.*;
import Ares.*;
import Ares.Parsers.*;
import Ares.World.*;
import Ares.World.Info.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import Ares.Commands.AresCommands.*;
import java.util.ArrayList;
import java.util.List;

public class Brain{
    private static BaseAgent base;
    private Simulation sim;
    private Communicator com;
    private Intelligence ai;

    public Brain(BaseAgent base, Intelligence ai, Simulation sim, Communicator com) {
      this.ai = ai;
      this.sim = sim;
      this.com = com;
      this.base = base;
    }

    
    public void setStartState(World world, AgentID id, Location location, int energy)
    {
      sim.setSelf(id);
      sim.update(world);
      sim.update(id, energy);
      sim.update(id, location);
    }

    
    public void handleSleepResult(SLEEP_RESULT msg)
    {
      sim.update(sim.getSelf(), msg.getChargeEnergy());
    }

    public void handleDisconnect() {
        BaseAgent.log(LogLevels.Always, "DISCONNECT");
    }

    
    public void handleDead() {
        BaseAgent.log(LogLevels.Always, "DEAD");
    }

    
    public void handleFwdMessage(FWD_MESSAGE fwd_message) {
        BaseAgent.log(LogLevels.Always, "FWD MESSAGE:" + fwd_message);
        BaseAgent.log(LogLevels.Test, "" + fwd_message);
        com.receive(fwd_message);
    }

    
    public void handleMoveResult(MOVE_RESULT move_result) {
        BaseAgent.log(LogLevels.Always, "MOVE_RESULT:" + move_result);
        BaseAgent.log(LogLevels.Test, "" + move_result);
        sim.update(move_result.getSurroundInfo());
        sim.update(sim.getSelf(), move_result.getEnergyLevel());
    }

    
    public void handleObserveResult(OBSERVE_RESULT observe_result) {
        BaseAgent.log(LogLevels.Always, "OBSERVE_RESULT:" + observe_result);
        BaseAgent.log(LogLevels.Always, observe_result.getEnergyLevel() + "");
        BaseAgent.log(LogLevels.Always, observe_result.getLifeSignals() + "");
        BaseAgent.log(LogLevels.Always, observe_result.getTopLayerInfo() + "");
        BaseAgent.log(LogLevels.Test, "" + observe_result);
        CellInfo info = observe_result.getTopLayerInfo();
        sim.update(info);
        sim.update(info.getLocation(), observe_result.getLifeSignals());
        sim.update(sim.getSelf(), observe_result.getEnergyLevel());
    }

    
    public void handleSaveSurvResult(SAVE_SURV_RESULT save_surv_result) {
        BaseAgent.log(LogLevels.Always, "SSAVE_SURV_RESULT:" + save_surv_result);
        BaseAgent.log(LogLevels.Test, "" + save_surv_result);
        sim.update(sim.getSelf(), save_surv_result.getEnergyLevel());
        sim.update(save_surv_result.getSurroundInfo());
    }

    
    public void handleTeamDigResult(TEAM_DIG_RESULT team_dig_result) {
        BaseAgent.log(LogLevels.Always, "TEAM_DIG_RESULT:" + team_dig_result);
        BaseAgent.log(LogLevels.Test, "" + team_dig_result);
        sim.update(sim.getSelf(), team_dig_result.getEnergyLevel());
        sim.update(team_dig_result.getSurroundInfo());
    }

    
    public void think() {
        BaseAgent.log(LogLevels.Always, "Thinking");
        ai.think();
    }

    public void handleAresCommand(AresCommand ares_command) throws AresParserException {
        BaseAgent agent = BaseAgent.getBaseAgent();
        if (ares_command instanceof CONNECT_OK) {
            CONNECT_OK connect_ok = (CONNECT_OK) ares_command;
            agent.setId(connect_ok.getNewAgentID());
            agent.setEnergyLevel(connect_ok.getEnergyLevel());
            agent.setLocation(connect_ok.getLocation());
            setStartState(new World(AresParser.buildWorld(connect_ok.getWorldFilename())),
                          connect_ok.getNewAgentID(),
                          connect_ok.getLocation(),
                          connect_ok.getEnergyLevel());
            agent.setAgentState(AgentStates.CONNECTED);
            BaseAgent.log(LogLevels.Test, "Connected successfully");
        } else if (ares_command instanceof DEATH_CARD) {
            agent.setAgentState(AgentStates.SHUTTING_DOWN);
            handleDead();
            System.exit(0);
        } else if (ares_command instanceof DISCONNECT) {
            agent.setAgentState(AgentStates.SHUTTING_DOWN);
            handleDisconnect();
            System.exit(0);
        } else if (ares_command instanceof FWD_MESSAGE) {
            FWD_MESSAGE command = (FWD_MESSAGE) ares_command;
            handleFwdMessage(command);
        } else if (ares_command instanceof MESSAGES_END) {
            agent.setAgentState(AgentStates.IDLE);
        } else if (ares_command instanceof MESSAGES_START) {
            agent.setAgentState(AgentStates.READ_MAIL);
        } else if (ares_command instanceof MOVE_RESULT) {
            MOVE_RESULT command = (MOVE_RESULT) ares_command;
            //CellInfo curr = command.getSurroundInfo().getCurrentInfo();
            //agent.setEnergyLevel(command.getEnergyLevel());
            //agent.setLocation(curr.getLocation());
            handleMoveResult(command);
        } else if (ares_command instanceof OBSERVE_RESULT) {
            OBSERVE_RESULT command = (OBSERVE_RESULT) ares_command;
            //agent.setEnergyLevel(command.getEnergyLevel());
            handleObserveResult(command);
        } else if (ares_command instanceof ROUND_END) {
            agent.setAgentState(AgentStates.IDLE);
        } else if (ares_command instanceof ROUND_START) {
            agent.setAgentState(AgentStates.THINK);
        } else if (ares_command instanceof SAVE_SURV_RESULT) {
            SAVE_SURV_RESULT command = (SAVE_SURV_RESULT) ares_command;
            //CellInfo curr = command.getSurroundInfo().getCurrentInfo();
            //agent.setEnergyLevel(command.getEnergyLevel());
            //agent.setLocation(curr.getLocation());
            handleSaveSurvResult(command);
        } else if (ares_command instanceof SLEEP_RESULT) {
            SLEEP_RESULT command = (SLEEP_RESULT) ares_command;
            if (command.wasSuccessful()) {
                //agent.setEnergyLevel(command.getChargeEnergy());
                handleSleepResult(command);
            }
        } else if (ares_command instanceof TEAM_DIG_RESULT) {
            TEAM_DIG_RESULT command = (TEAM_DIG_RESULT) ares_command;
            //CellInfo curr = command.getSurroundInfo().getCurrentInfo();
            //agent.setEnergyLevel(command.getEnergyLevel());
            //agent.setLocation(curr.getLocation());
            handleTeamDigResult((TEAM_DIG_RESULT) ares_command);
        } else if (ares_command instanceof ARES_UNKNOWN) {
            BaseAgent.log(LogLevels.Always, "Brain: Got Unknown command reply from ARES");
        } else if (ares_command instanceof CMD_RESULT_START) {
        } else if (ares_command instanceof CMD_RESULT_END) {
        } else {
            BaseAgent.log(LogLevels.Always, "Brain: Got unrecognized reply from ARES " + ares_command.getClass());
        }
    }
}
