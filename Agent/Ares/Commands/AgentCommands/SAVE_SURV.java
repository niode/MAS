package Ares.Commands.AgentCommands;

import Ares.Commands.*;

public class SAVE_SURV extends AgentCommand {

    @Override
    public String toString() {
        return STR_SAVE_SURV;
    }

    @Override
    public String procString() {
        return String.format("%s#Save SV", agent_id.procString());
    }
}
