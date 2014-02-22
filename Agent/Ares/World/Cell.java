package Ares.World;

import Ares.*;
import Ares.World.Info.*;
import Ares.World.Objects.*;
import Ares.World.Parts.*;
import java.util.ArrayList;
import java.util.List;

public class Cell{

    private enum Type {

        NO_CELL, NORMAL_CELL, CHARGING_CELL;
    }

    private enum State {

        STABLE_CELL, KILLER_CELL;
    }
    private Type type;
    private State state;
    protected Location location;
    private AgentIDList agent_id_list;
    private List<WorldObject> cell_layer_list;
    private boolean on_fire;
    private int move_cost;
    private int percent_chance;
    private LifeSignals stored_life_signals;

    public Cell() {
        type = Type.NO_CELL;
        state = State.STABLE_CELL;
        location = new Location(-1, -1);
        on_fire = false;
        move_cost = 1;
        agent_id_list = new AgentIDList();
        cell_layer_list = new ArrayList<>();
        percent_chance = -1;
        stored_life_signals = new LifeSignals();
    }

    public Cell(int row, int col) {
        this();
        location = new Location(row, col);
    }

    public void setupCell(String cell_state_type) {
        cell_state_type = cell_state_type.toUpperCase().trim();
        if (cell_state_type.compareTo("NORMAL_CELLS") == 0) {
            type = Type.NORMAL_CELL;
            on_fire = false;
            state = State.STABLE_CELL;
        } else if (cell_state_type.compareTo("CHARGING_CELLS") == 0) {
            type = Type.CHARGING_CELL;
            on_fire = false;
            state = State.STABLE_CELL;
        } else if (cell_state_type.compareTo("FIRE_CELLS") == 0) {
            type = Type.NORMAL_CELL;
            on_fire = true;
            state = State.KILLER_CELL;
        } else if (cell_state_type.compareTo("KILLER_CELLS") == 0) {
            type = Type.NORMAL_CELL;
            on_fire = false;
            state = State.KILLER_CELL;
        }
        
    }

    private Type getType() {
        return type;
    }

    public boolean isNoCell() {
        return getType() == Type.NO_CELL;
    }

    public boolean isNormalCell() {
        return getType() == Type.NORMAL_CELL;
    }

    public boolean isChargingCell() {
        return getType() == Type.CHARGING_CELL;
    }

    public void setNoCell() {
        setType(Type.NO_CELL);
    }

    public void setNormalCell() {
        setType(Type.NORMAL_CELL);
    }

    public void setChargingCell() {
        setType(Type.CHARGING_CELL);
    }

    private void setType(Type type) {
        this.type = type;
        
    }

    private State getState() {
        return state;
    }

    public boolean isStable() {
        return getState() == State.STABLE_CELL;
    }

    public boolean isKiller() {
        return getState() == State.KILLER_CELL;
    }

    public void setStable() {
        setState(State.STABLE_CELL);
    }

    public void setKiller() {
        setState(State.KILLER_CELL);
    }

    private void setState(State state) {
        this.state = state;
        
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        
    }

    public AgentIDList getAgentList() {
        return agent_id_list;
    }

    public void setAgentList(AgentIDList agent_id_list) {
        this.agent_id_list = agent_id_list;
        
    }

    public void addAgent(AgentID agent_id) {
        agent_id_list.add(agent_id);
        
    }

    public void removeAgent(AgentID agent_id) {
        agent_id_list.remove(agent_id);
        
    }

    public List<WorldObject> getCellLayers() {
        return cell_layer_list;
    }

    public void addLayer(WorldObject layer) {
        cell_layer_list.add(layer);
        
    }

    public WorldObject removeTopLayer() {
        if (cell_layer_list.isEmpty()) {
            return null;
        }
        WorldObject layer = cell_layer_list.remove(cell_layer_list.size() - 1);
        
        return layer;
    }

    public WorldObject getTopLayer() {
        if (cell_layer_list.isEmpty()) {
            return null;
        }
        return cell_layer_list.get(cell_layer_list.size() - 1);
    }

    public void setTopLayer(WorldObject top_layer) {
        cell_layer_list.clear();
        cell_layer_list.add(top_layer);
        
    }

    public int numberOfLayers() {
        return cell_layer_list.size();
    }

    public boolean isOnFire() {
        return on_fire;
    }

    public void setOnFire() {
        on_fire = true;
        setState(State.KILLER_CELL);
        
    }

    public void setNotOnFire() {
        on_fire = false;
        setState(State.STABLE_CELL);
        
    }

    public void setOnFire(boolean on_fire) {
        if (on_fire) {
            setOnFire();
        } else {
            setNotOnFire();
        }
    }

    public int getMoveCost() {
        return move_cost;
    }

    public void setMoveCost(int move_cost) {
        this.move_cost = move_cost;
        
    }

    public int getPercentChance() {
        return percent_chance;
    }

    public void setPercentChance(int percent_chance) {
        this.percent_chance = percent_chance;
    }

    public CellInfo getCellInfo() {
        return new CellInfo(type == Type.NORMAL_CELL, location.clone(), on_fire, move_cost, agent_id_list.clone(), topLayerInfo());
    }

    public void setLifeSignals(LifeSignals stored_life_signals) {
        this.stored_life_signals = stored_life_signals;
    }

    public LifeSignals getLifeSignals() {
        return stored_life_signals;
    }

    private WorldObjectInfo topLayerInfo() {
        if (!cell_layer_list.isEmpty()) {
            return getTopLayer().getObjectInfo();
        } else {
            return new BottomLayerInfo();
        }
    }

    public int numberOfSurvivors() {
        int count = 0;
        for (WorldObject layer : cell_layer_list) {
            if (layer instanceof Survivor) {
                count++;
            }
            if (layer instanceof SurvivorGroup) {
                count += ((SurvivorGroup) layer).getNumberOfSurvivors();
            }
        }
        return count;
    }

    public LifeSignals getGeneratedLifeSignals() {
        int layer = cell_layer_list.size() - 1;
        int distortion;
        int lss;
        int i = 0;
        if (cell_layer_list.isEmpty()) {
            return new LifeSignals();
        }
        int[] life_signals = new int[cell_layer_list.size()];
        life_signals[i] = cell_layer_list.get(layer).getLifeSignal();
        i++;
        layer--;
        //2013 Distoration (original)
        int low_range = Constants.DEPTH_LOW_START;
        int high_range = Constants.DEPTH_HIGH_START;
        while (layer >= 0) {
            lss = cell_layer_list.get(layer).getLifeSignal();
            distortion = Utility.randomInRange(low_range, high_range);
            if (distortion > lss) {
                lss = 0;
            } else {
                lss -= distortion;
            }
            life_signals[i] = lss;
            i++;
            layer--;
            low_range += Constants.DEPTH_LOW_INC;
            high_range += Constants.DEPTH_HIGH_INC;
        }
        return new LifeSignals(life_signals);
    }

    public String fileOutputString() {
        String s = "Cell ( (" + location.getRow() + "," + location.getCol() + "), Move_Cost " + move_cost + ") \n\t\t{\n";
        for (WorldObject layer : cell_layer_list) {
            s += "\t\t    " + layer.fileOutputString() + ";\n";
        }
        s += "\t\t}\n\n";
        return s;
    }

    @Override
    public Cell clone() {
        Cell cell = new Cell();
        cell.type = type;
        cell.state = state;
        cell.location = location.clone();
        cell.agent_id_list = agent_id_list.clone();
        cell.cell_layer_list = new ArrayList<>();
        for (WorldObject layer : cell_layer_list) {
            cell.cell_layer_list.add(layer.clone());
        }
        cell.on_fire = on_fire;
        cell.move_cost = move_cost;
        cell.percent_chance = percent_chance;
        return cell;
    }
}
