package Ares.World.Info;

import Ares.*;

public class CellInfo {

    private enum Type {

        NO_CELL, NORMAL_CELL, CHARGING_CELL;
    }
    private Type type;
    private Location location;
    private Boolean on_fire;
    private int move_cost;
    private AgentIDList agent_id_list;
    private WorldObjectInfo top_layer_info;

    public CellInfo() {
        type = Type.NO_CELL;
        location = new Location(-1, -1);
        on_fire = false;
        move_cost = 0;
        agent_id_list = new AgentIDList();
        top_layer_info = new BottomLayerInfo();
    }

    public CellInfo(boolean normal_cell, Location location, boolean on_fire, int move_cost, AgentIDList agent_id_list, WorldObjectInfo top_layer_info) {
        if (normal_cell) {
            this.type = Type.NORMAL_CELL;
        } else {
            this.type = Type.CHARGING_CELL;
        }
        this.location = location;
        this.on_fire = on_fire;
        this.move_cost = move_cost;
        this.agent_id_list = agent_id_list;
        this.top_layer_info = top_layer_info;
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

    private void setType(Type cell_type) {
        this.type = cell_type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isOnFire() {
        return on_fire;
    }

    public void setOnFire() {
        this.on_fire = true;
    }

    public void setNotOnFire() {
        this.on_fire = false;
    }

    public int getMoveCost() {
        return move_cost;
    }

    public void setMoveCost(int move_cost) {
        this.move_cost = move_cost;
    }

    public AgentIDList getAgentIDList() {
        return agent_id_list;
    }

    public void setAgentIDList(AgentIDList agent_id_list) {
        this.agent_id_list = agent_id_list;
    }

    public WorldObjectInfo getTopLayerInfo() {
        return top_layer_info;
    }

    public void setTopLayerInfo(WorldObjectInfo top_layer_info) {
        this.top_layer_info = top_layer_info;
    }

    @Override
    public String toString() {
        if (type == Type.NO_CELL) {
            return type.toString();
        }
        return String.format("%s ( ROW_ID %s , COL_ID %s , ON_FIRE %s , MV_COST %s , NUM_AGT %s , ID_List %s , TOP_LAYER ( %s ) )", type.toString(), location.getRow(), location.getCol(), on_fire.toString().toUpperCase(), move_cost, agent_id_list.size(), agent_id_list, top_layer_info);

    }

    public void distortInfo(int factor) {
        top_layer_info.distortInfo(factor);
    }
}
