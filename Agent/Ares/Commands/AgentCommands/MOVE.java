package Ares.Commands.AgentCommands;

import Ares.*;
import Ares.Commands.*;

public class MOVE extends AgentCommand {

    private Direction direction;

    public MOVE(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", STR_MOVE, direction);
    }

    @Override
    public String procString() {
        return String.format("%s#Move %s", agent_id.procString(), direction.getProcString());
    }
}
