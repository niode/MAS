package Ares.World;

import Ares.*;
import Ares.World.Parts.*;

public class Agent{

    private enum State {

        ALIVE, DEAD;
    }
    private AgentID agent_id;
    private Location location;
    private int energy_level;
    private State state;
    private Direction orientation;
    private String command_sent;

    public Agent(AgentID agent_id, Location location) {
        this.agent_id = agent_id;
        this.location = location;
        energy_level = Constants.DEFAULT_MAX_ENERGY_LEVEL;
        state = State.ALIVE;
        orientation = Direction.STAY_PUT;
        command_sent = "None";
    }

    public Agent(AgentID agent_id, Location location, int energy_level) {
        this(agent_id, location);
        this.energy_level = energy_level;
    }

    public AgentID getAgentID() {
        return agent_id;
    }

    public void setAgentID(AgentID agent_id) {
        this.agent_id = agent_id;
        
        
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        
        
    }

    public int getEnergyLevel() {
        return energy_level;
    }

    public void setEnergyLevel(int energy_level) {
        this.energy_level = energy_level;
        if (energy_level <= 0) {
            state = State.DEAD;
        } else {
            state = State.ALIVE;
        }
        
        
    }

    public void addEnergy(int add_energy) {
        if (add_energy >= 0) {
            energy_level += add_energy;
        }
        
        
    }

    public void removeEnergy(int remove_energy) {
        if (remove_energy < energy_level) {
            energy_level -= remove_energy;
        } else {
            energy_level = 0;
            state = State.DEAD;
        }
        
        
    }

    private State getState() {
        return state;
    }

    public boolean isAlive() {
        return getState() == State.ALIVE;
    }

    public boolean isDead() {
        return getState() == State.DEAD;
    }

    public void setAlive() {
        setState(State.ALIVE);
    }

    public void setDead() {
        setState(State.DEAD);
    }

    private void setState(State state) {
        this.state = state;
        
        
    }

    public Direction getOrientation() {
        return orientation;
    }

    public void setOrientation(Direction orientation) {
        this.orientation = orientation;
        
        
    }

    public String getCommandSent() {
        return command_sent;
    }

    public void setCommandSent(String command_sent) {
        this.command_sent = command_sent;
        
        
    }

    @Override
    public String toString() {
        return agent_id.toString();
    }

    public List<String> stringInformation() {
        List<String> string_information = new ArrayList<String>();
        string_information.add(String.format("AgentID       = %s", agent_id));
        string_information.add(String.format("Location      = %s", location));
        string_information.add(String.format("Energy Level  = %s", energy_level));
        string_information.add(String.format("State         = %s", state));
        string_information.add(String.format("Command Sent  = %s", command_sent));
        return string_information;
    }

    @Override
    public Agent clone() {
        Agent agent = new Agent(agent_id, location, energy_level);
        agent.state = state;
        agent.orientation = orientation;
        agent.command_sent = command_sent;
        return agent;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Agent) {
            return agent_id.equals(((Agent) object).agent_id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return agent_id.hashCode();
    }

    public int compareTo(AgentID agent_id) {
        return agent_id.compareTo(agent_id);
    }
}
