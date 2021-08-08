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
        Log.e(TAG, "onDraw: start");
        canvas.drawColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();
//        Log.e(TAG, "onDraw: width : " + width + ",height : " + height);

        if(width / height < COLS / ROWS){
            cellSize = Math.round(width / (COLS + 1.0));
        }else{
            cellSize = Math.round(height/(ROWS + 1.0));
        }

//        Log.e(TAG, "onDraw: cellSize : " + cellSize);
        hMargin = (width - COLS * cellSize) / 2;
        vMargin = (height - ROWS * cellSize) / 2;
        if(hMargin < 0 || vMargin < 0){
            Log.e(TAG, "onDraw: onDraw position error !!");
            return;
        }

//        Log.e(TAG, "onDraw: " + hMargin + "," + vMargin);

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
        Log.e(TAG, "onDraw: currentDir" + currentDir.toString());
        if(currentDir == Direction.NONE){
            rectPlayer = new Rect(Math.round(player.col*cellSize),
                    Math.round(player.row*cellSize),
                    Math.round((player.col+1)*cellSize),
                    Math.round((player.row+1)*cellSize));
        }
        canvas.drawRect(rectPlayer, playerPaint);


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

        remove_random_walls(25);

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
//                try {
//                    Thread.sleep(500);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
                if(currentDir == Direction.NONE){
                    try {
                        Thread.sleep(2000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "run start: " + player.col + "," + player.row);

                checkNextPosition();

                Log.e(TAG, "run end: " + player.col + "," + player.row);

                Log.e(TAG, "_________________one move_____________________");
                move();
            }
        });
    }

    private void checkNextPosition(){
//        Log.e(TAG, "checkNextPosition: ");
        ArrayList<Direction> directions = new ArrayList<>();

        if(!cells[player.col][player.row].topWall){
            Log.e(TAG, "run: top" );
//            player = cells[player.col][player.row-1];
            directions.add(Direction.UP);
//            currentDir = Direction.UP;

        }
        if(!cells[player.col][player.row].leftWall){
            Log.e(TAG, "run: left" );
//            player = cells[player.col-1][player.row];
            directions.add(Direction.LEFT);
//            currentDir = Direction.LEFT;

        }
        if(!cells[player.col][player.row].bottomWall){
            Log.e(TAG, "run: bottom" );
//            player = cells[player.col][player.row+1];
            directions.add(Direction.DOWN);
//            currentDir = Direction.DOWN;

        }
        if(!cells[player.col][player.row].rightWall){
            Log.e(TAG, "run: right" );
//            player = cells[player.col+1][player.row];
            directions.add(Direction.RIGHT);
//            currentDir = Direction.RIGHT;
        }

        int index = random.nextInt(directions.size());
        currentDir = directions.get(index);
        goDir(currentDir);

    }


    private void goDir(Direction direction){
        switch (direction){
            case UP:
                for(int i = 1; i<=2; i++){
                    rectPlayer = new Rect(Math.round(player.col*cellSize),
                            Math.round((player.row-(float)i/2)*cellSize),
                            Math.round((player.col+1)*cellSize),
                            Math.round(((player.row+1)-(float)i/2)*cellSize));

                    post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                player = cells[player.col][player.row-1];
//                Log.e(TAG, "run test: " + player.col + "," + player.row);
                break;

            case LEFT:
                for(int i = 1; i<= 2; i++){
                    rectPlayer = new Rect(Math.round((player.col - (float)i/2)*cellSize),
                            Math.round((player.row)*cellSize),
                            Math.round(((player.col+1)-(float)i/2)*cellSize),
                            Math.round((player.row+1)*cellSize));

                    post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                player = cells[player.col-1][player.row];
                break;

            case DOWN:
                for(int i = 1; i<= 2; i++){
                    rectPlayer = new Rect(Math.round(player.col*cellSize),
                            Math.round((player.row + (float)i/2)*cellSize),
                            Math.round((player.col+1)*cellSize),
                            Math.round(((player.row+1) + (float)i/2)*cellSize));

                    post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                player = cells[player.col][player.row+1];
                break;
            case RIGHT:
                for(int i = 1; i<= 2; i++){
                    rectPlayer = new Rect(Math.round((player.col + (float)i/2)*cellSize),
                            Math.round((player.row)*cellSize),
                            Math.round(((player.col+1) + (float)i/2)*cellSize),
                            Math.round((player.row+1)*cellSize));

                    post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                player = cells[player.col+1][player.row];
                break;
        }

    }

    private void remove_diagonal_walls(){
        for(int i = 2; i <= 10; i++){
            Direction direction;
            ArrayList<Direction> directions = new ArrayList<>();
            if(cells[i][i].topWall){
                directions.add(Direction.UP);
            }
            if(cells[i][i].leftWall){
                directions.add(Direction.LEFT);
            }
            if(cells[i][i].bottomWall){
                directions.add(Direction.DOWN);
            }
            if(cells[i][i].rightWall){
                directions.add(Direction.RIGHT);
            }
            int index = random.nextInt(directions.size());
            direction = directions.get(index);
            switch (direction){
                case UP:
                    cells[i][i].topWall = false;
                    cells[i][i-1].bottomWall = false;
                    break;
                case LEFT:
                    cells[i][i].leftWall = false;
                    cells[i-1][i].rightWall = false;
                    break;
                case DOWN:
                    cells[i][i].bottomWall = false;
                    cells[i][i+1].topWall = false;
                    break;
                case RIGHT:
                    cells[i][i].rightWall = false;
                    cells[i+1][i].leftWall = false;
                    break;
            }

        }
    }

    private void remove_random_walls(int many){
        for(int i = 0; i < many; i++){
            Random rCol = new Random();
            int col = rCol.nextInt(9) + 1;
            Random rRow = new Random();
            int row = rRow.nextInt(9) + 1;
            Log.e(TAG, "remove_random_walls: col:" + col + "row" + row);
            Direction direction;
            ArrayList<Direction> directions = new ArrayList<>();
            if(cells[col][row].topWall){
                directions.add(Direction.UP);
            }
            if(cells[col][row].leftWall){
                directions.add(Direction.LEFT);
            }
            if(cells[col][row].bottomWall){
                directions.add(Direction.DOWN);
            }
            if(cells[col][row].rightWall){
                directions.add(Direction.RIGHT);
            }
            if(directions.size() != 0){
                int index = random.nextInt(directions.size());
                Log.e(TAG, "remove_random_walls: index:" + index);
                direction = directions.get(index);
                switch (direction){
                    case UP:
                        cells[col][row].topWall = false;
                        cells[col][row-1].bottomWall = false;
                        break;
                    case LEFT:
                        cells[col][row].leftWall = false;
                        cells[col-1][row].rightWall = false;
                        break;
                    case DOWN:
                        cells[col][row].bottomWall = false;
                        cells[col][row+1].topWall = false;
                        break;
                    case RIGHT:
                        cells[col][row].rightWall = false;
                        cells[col+1][row].leftWall = false;
                        break;
                }

            }
        }
    }






}
