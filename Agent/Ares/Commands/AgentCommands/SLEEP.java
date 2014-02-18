package Ares.Commands.AgentCommands;

import Ares.Commands.*;

public class SLEEP extends AgentCommand {

    @Override
    public String toString() {
        return STR_SLEEP;
    }

    @Override
    public String procString() {
        return String.format("%s#Sleep", agent_id.procString());
    }
}
