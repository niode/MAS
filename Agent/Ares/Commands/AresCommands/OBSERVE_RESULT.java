package Ares.Commands.AresCommands;

import Ares.*;
import Ares.Commands.*;
import Ares.World.Info.*;

public class OBSERVE_RESULT extends AresCommand {

    private int energy_level;
    private CellInfo top_layer_info;
    private LifeSignals life_signals;

    public OBSERVE_RESULT(int energy_level, CellInfo top_layer_info, LifeSignals life_signals) {
        this.energy_level = energy_level;
        this.top_layer_info = top_layer_info;
        this.life_signals = life_signals;
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public CellInfo getTopLayerInfo() {
        return top_layer_info;
    }

    public LifeSignals getLifeSignals() {
        return life_signals;
    }

    @Override
    public String toString() {
        return String.format("%s ( ENG_LEV %s , CELL_INFO ( %s ) , NUM_SIG %s , LIFE_SIG %s )", STR_OBSERVE_RESULT, energy_level, top_layer_info, life_signals.size(), life_signals);
    }

    public void distort(double factor) {
        if (factor <= 0) {
            return;
        }
        life_signals.distort((int) factor);
        top_layer_info.distortInfo((int) factor);
    }
}
