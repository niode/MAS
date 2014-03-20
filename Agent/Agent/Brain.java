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
      sim.update(sim.getSelfID(), msg.getChargeEnergy());
    }

    public void handleDisconnect() {
        base.log(LogLevels.Always, "DISCONNECT");
    }
    
    public void handleDead() {
        base.log(LogLevels.Always, "DEAD");
    }
    
    public void handleFwdMessage(FWD_MESSAGE fwd_message) {
        base.log(LogLevels.Always, "FWD_MESSAGE");
        com.receive(fwd_message);
    }
    
    public void handleMoveResult(MOVE_RESULT move_result) {
        base.log(LogLevels.Always, "MOVE_RESULT");
        sim.update(move_result.getSurroundInfo());
        sim.update(sim.getSelfID(), move_result.getSurroundInfo().getCurrentInfo().getLocation());
        sim.update(sim.getSelfID(), move_result.getEnergyLevel());
        com.send(move_result.getSurroundInfo());
    }

    public void handleObserveResult(OBSERVE_RESULT observe_result) {
        base.log(LogLevels.Always, "OBSERVE_RESULT");
        CellInfo info = observe_result.getTopLayerInfo();
        sim.update(info);
        sim.update(info.getLocation(), observe_result.getLifeSignals());
        sim.update(sim.getSelfID(), observe_result.getEnergyLevel());
        com.send(info);
    }

    
    public void handleSaveSurvResult(SAVE_SURV_RESULT save_surv_result) {
        base.log(LogLevels.Always, "SAVE_SURV_RESULT");
        sim.update(sim.getSelfID(), save_surv_result.getEnergyLevel());
        sim.update(save_surv_result.getSurroundInfo());
    }

    
    public void handleTeamDigResult(TEAM_DIG_RESULT team_dig_result) {
        base.log(LogLevels.Always, "TEAM_DIG_RESULT");
        sim.update(sim.getSelfID(), team_dig_result.getEnergyLevel());
        sim.update(team_dig_result.getSurroundInfo());
    }

    
    public void think() {
        base.log(LogLevels.Always, "Thinking");
        System.out.println("The world looks like:");
        for(int i = 0; i < sim.getRowCount(); i++)
        {
          for(int j = 0; j < sim.getColCount(); j++)
          {
            String cost = String.format("%3d", sim.getMoveCost(i, j)).substring(0, 3);
            String percent = String.format("%3d", sim.getPercentage(i, j)).substring(0, 3);
            System.out.printf("(%s, %s) ", cost, percent);
          }
          System.out.println();
        }
        ai.think();
        com.send(sim.getSelf());
        com.send(new END_TURN());
        sim.advance();
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
            base.log(LogLevels.Test, "Connected successfully");
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
            handleMoveResult(command);
        } else if (ares_command instanceof OBSERVE_RESULT) {
            OBSERVE_RESULT command = (OBSERVE_RESULT) ares_command;
            handleObserveResult(command);
        } else if (ares_command instanceof ROUND_END) {
            agent.setAgentState(AgentStates.IDLE);
        } else if (ares_command instanceof ROUND_START) {
            agent.setAgentState(AgentStates.THINK);
        } else if (ares_command instanceof SAVE_SURV_RESULT) {
            SAVE_SURV_RESULT command = (SAVE_SURV_RESULT) ares_command;
            handleSaveSurvResult(command);
        } else if (ares_command instanceof SLEEP_RESULT) {
            SLEEP_RESULT command = (SLEEP_RESULT) ares_command;
            if (command.wasSuccessful()) {
                handleSleepResult(command);
            }
        } else if (ares_command instanceof TEAM_DIG_RESULT) {
            TEAM_DIG_RESULT command = (TEAM_DIG_RESULT) ares_command;
            handleTeamDigResult((TEAM_DIG_RESULT) ares_command);
        } else if (ares_command instanceof ARES_UNKNOWN) {
            base.log(LogLevels.Always, "Brain: Got Unknown command reply from ARES");
        } else if (ares_command instanceof CMD_RESULT_START) {
        } else if (ares_command instanceof CMD_RESULT_END) {
        } else {
            base.log(LogLevels.Always, "Brain: Got unrecognized reply from ARES " + ares_command.getClass());
        }
    }
}
