package Ares.Commands.AresCommands;

import Ares.Commands.*;
import Ares.World.Info.*;

public class TEAM_DIG_RESULT extends AresCommand {

    private int energy_level;
    private SurroundInfo surround_info;

    public TEAM_DIG_RESULT(int energy_level, SurroundInfo surround_info) {
        this.energy_level = energy_level;
        this.surround_info = surround_info;
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public SurroundInfo getSurroundInfo() {
        return surround_info;
    }

    @Override
    public String toString() {
        return String.format("%s ( ENG_LEV %s , %s )", STR_TEAM_DIG_RESULT, energy_level, surround_info);
    }
}
