package org.kevin.game;

public class Cell {
    public boolean topWall = true;
    public boolean leftWall = true;
    public boolean bottomWall = true;
    public boolean rightWall = true;
    public boolean visited = false;

    public int col, row;

    public Cell(int col, int row){
        this.col = col;
        this.row = row;
    }
}
