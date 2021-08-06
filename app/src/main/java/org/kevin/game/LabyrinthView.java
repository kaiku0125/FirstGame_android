package org.kevin.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LabyrinthView extends View {
    private enum Direction{
        NONE, UP, DOWN, LEFT, RIGHT
    }
    private Direction currentDir = Direction.NONE;
    private static final String TAG = "LabyrinthView";
    private Cell[][] cells;
    private Cell player;
    private static final int COLS = 12;
    private static final int ROWS = 12;
    private static final float WALL_THICKNESS = 4;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint;
    private Random random;
    int count = 0;
    private ExecutorService executorService;
    private float  f = 0;
    private Rect rectPlayer;

    public LabyrinthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);
        random = new Random();

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        executorService = Executors.newSingleThreadExecutor();

        createLabyrinth();

        move();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw: ");
        canvas.drawColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();
        Log.e(TAG, "onDraw: width : " + width + ",height : " + height);

        if(width / height < COLS / ROWS){
            cellSize = Math.round(width / (COLS + 1.0));
        }else{
            cellSize = Math.round(height/(ROWS + 1.0));
        }

        Log.e(TAG, "onDraw: cellSize : " + cellSize);
        hMargin = (width - COLS * cellSize) / 2;
        vMargin = (height - ROWS * cellSize) / 2;
        if(hMargin < 0 || vMargin < 0){
            Log.e(TAG, "onDraw: onDraw position error !!");
            return;
        }

        Log.e(TAG, "onDraw: " + hMargin + "," + vMargin);

        canvas.translate(hMargin, vMargin);

        for(int x = 0; x < COLS; x++){
            for(int y = 0; y < ROWS; y++){
                if(cells[x][y].topWall){
                    canvas.drawLine(x*cellSize, y*cellSize, (x+1)*cellSize, y*cellSize, wallPaint);
                }
                if(cells[x][y].leftWall){
                    canvas.drawLine(x*cellSize, y*cellSize, x*cellSize, (y+1)*cellSize, wallPaint);
                }
                if(cells[x][y].bottomWall){
                    canvas.drawLine(x*cellSize, (y+1)*cellSize, (x+1)*cellSize, (y+1)*cellSize, wallPaint);
                }
                if(cells[x][y].rightWall){
                    canvas.drawLine((x+1)*cellSize, y*cellSize, (x+1)*cellSize, (y+1)*cellSize, wallPaint);
                }
            }
        }

        if(currentDir == Direction.NONE){
            rectPlayer = new Rect(Math.round(player.col*cellSize),
                    Math.round(player.row*cellSize),
                    Math.round((player.col+1)*cellSize),
                    Math.round((player.row+1)*cellSize));
        }
        canvas.drawRect(rectPlayer, playerPaint);

//        canvas.drawRect(player.col*cellSize,
//                          player.row*cellSize,
//                        (player.col+1)*cellSize,
//                        (player.row+1)*cellSize,
//                                playerPaint);
    }

    private void createLabyrinth(){
        Log.e(TAG, "createLabyrinth: ");
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        cells = new Cell[COLS][ROWS];

        for(int x = 0; x < COLS; x++){
            for(int y = 0; y < ROWS; y++){
                cells[x][y] = new Cell(x, y);
            }
        }
        player = cells[6][6];


        current = cells[6][6];
        current.visited = true;

        do{
            next = getNeighbor(current);
            if(next != null){
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            }else{
                current = stack.pop();
            }
        } while(!stack.empty());

    }

    private Cell getNeighbor(Cell cell){
        ArrayList<Cell> neighbors = new ArrayList<>();

        //leftNeighbor
        if(cell.col > 0){
            if(!cells[cell.col-1][cell.row].visited){
                neighbors.add(cells[cell.col-1][cell.row]);
            }
        }
        //rightNeighbor
        if(cell.col < COLS - 1){
            if(!cells[cell.col+1][cell.row].visited){
                neighbors.add(cells[cell.col+1][cell.row]);
            }
        }
        //topNeighbor
        if(cell.row > 0){
            if(!cells[cell.col][cell.row-1].visited){
                neighbors.add(cells[cell.col][cell.row-1]);
            }
        }
        //bottomNeighbor
        if(cell.row < ROWS - 1){
            if(!cells[cell.col][cell.row+1].visited){
                neighbors.add(cells[cell.col][cell.row+1]);
            }
        }

        if(neighbors.size() > 0){
            int index = random.nextInt(neighbors.size());
            return neighbors.get(index);
        }
        return null;
    }

    private void removeWall(Cell current, Cell next){
        //current below next
        if(current.col == next.col && current.row == next.row+1){
            current.topWall = false;
            next.bottomWall = false;
        }
        //current above next
        if(current.col == next.col && current.row == next.row-1){
            current.bottomWall = false;
            next.topWall = false;
        }
        //current toRight next
        if(current.col == next.col+1 && current.row == next.row){
            current.leftWall = false;
            next.rightWall = false;
        }
        //current toLeft next
        if(current.col == next.col-1 && current.row == next.row){
            current.rightWall = false;
            next.leftWall = false;
        }

    }

    private void move(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                Log.e(TAG, "run: " + player.col + "," + player.row);



                checkNextPosition();


                Log.e(TAG, "run: " + player.col + "," + player.row);


            }
        });
    }

    private void checkNextPosition(){
        if(!cells[player.col][player.row].topWall){
            Log.e(TAG, "run: top" );
//            player = cells[player.col][player.row-1];
            currentDir = Direction.UP;

        }
        if(!cells[player.col][player.row].leftWall){
            Log.e(TAG, "run: left" );
//            player = cells[player.col-1][player.row];
            currentDir = Direction.LEFT;

        }
        if(!cells[player.col][player.row].bottomWall){
            Log.e(TAG, "run: bottom" );
//            player = cells[player.col][player.row+1];
            currentDir = Direction.DOWN;

        }
        if(!cells[player.col][player.row].rightWall){
            Log.e(TAG, "run: right" );
//            player = cells[player.col+1][player.row];
            currentDir = Direction.RIGHT;
        }
        goDir(currentDir);
    }


    private void goDir(Direction direction){
        switch (direction){
            case UP:
                player = cells[player.col][player.row-1];
                rectPlayer = new Rect(Math.round(player.col*cellSize),
                        Math.round((player.row-0.5f)*cellSize),
                        Math.round((player.col+1)*cellSize),
                        Math.round(((player.row+1)-0.5f)*cellSize));
                invalidate();
            case LEFT:
                player = cells[player.col-1][player.row];
            case DOWN:
                player = cells[player.col][player.row+1];
            case RIGHT:
                player = cells[player.col+1][player.row];
        }

    }


}
