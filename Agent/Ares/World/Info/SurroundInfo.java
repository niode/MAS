package Ares.World.Info;

import Ares.*;

public class SurroundInfo {

    private LifeSignals life_signals = new LifeSignals();
    private CellInfo[][] surround_info = new CellInfo[3][3];

    public LifeSignals getLifeSignals() {
        return life_signals;
    }

    public void setLifeSignals(LifeSignals life_signals) {
        this.life_signals = life_signals;
    }

    public CellInfo getCurrentInfo() {
        return surround_info[Direction.STAY_PUT.getSurroundX()][Direction.STAY_PUT.getSurroundY()];
    }

    public void setCurrentInfo(CellInfo current_info) {
        surround_info[Direction.STAY_PUT.getSurroundX()][Direction.STAY_PUT.getSurroundY()] = current_info;
    }

    public CellInfo getSurroundInfo(Direction direction) {
        if (direction == Direction.UNKNOWN) {
            return null;
        }
        return surround_info[direction.getSurroundX()][direction.getSurroundY()];
    }

    public void setSurroundInfo(Direction direction, CellInfo cell_info) {
        if (direction != Direction.UNKNOWN) {
            surround_info[direction.getSurroundX()][direction.getSurroundY()] = cell_info;
        }
    }

    @Override
    public String toString() {
        return String.format("CURR_CELL ( %s ) , NUM_SIG %s , LIFE_SIG %s , NORTH_WEST ( %s ) , NORTH ( %s ) , NORTH_EAST ( %s ) , EAST ( %s ) , SOUTH_EAST ( %s ) , SOUTH ( %s ) , SOUTH_WEST ( %s ) , WEST ( %s )", getCurrentInfo(), life_signals.size(), life_signals, getSurroundInfo(Direction.NORTH_WEST), getSurroundInfo(Direction.NORTH), getSurroundInfo(Direction.NORTH_EAST), getSurroundInfo(Direction.EAST), getSurroundInfo(Direction.SOUTH_EAST), getSurroundInfo(Direction.SOUTH), getSurroundInfo(Direction.SOUTH_WEST), getSurroundInfo(Direction.WEST));
    }
}
