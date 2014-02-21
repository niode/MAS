package Ares.World;

import Ares.*;

public class World {

    private int rows;
    private int cols;
    private Cell[][] world;

    public World(Cell[][] world) {
        this.rows = world.length;
        this.cols = world[0].length;
        this.world = world;
    }

    public World(int rows, int cols) {
        if (rows > 0 && cols > 0) {
            this.rows = rows;
            this.cols = cols;
            world = new Cell[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    world[row][col] = new Cell(row, col);
                }
            }
        }
    }

    public Cell[][] getCell() {
        return world;
    }

    public void setCell(Cell[][] world) {
        this.rows = world.length;
        this.cols = world[0].length;
        this.world = world;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCell(Location location, Cell cell) {
        if (world == null || location == null) {
            return;
        }
        if (location.valid(rows, cols)) {
            world[location.getRow()][location.getCol()] = cell;
        }
    }

    public Cell getCell(Location location) {
        if (world == null || location == null) {
            return null;
        }
        if (location.valid(rows, cols)) {
            return world[location.getRow()][location.getCol()];
        }
        return null;
    }

    public Cell getCellNeighbour(Direction direction, Location location) {
        return getCell(location.add(direction.getRowInc(), direction.getColInc()));
    }
}
