package com.example.tacstratgame;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import static android.graphics.Color.WHITE;

public class Map {
    private Tile[][] map;
    private Resources resources;
    private int screenWidth = (int)(Resources.getSystem().getDisplayMetrics().widthPixels*.9);
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int interval;
    private int unusedPix;
    private int startheight;
    private int width;
    private int height;
    private float[] grid;
    private Paint gridPaint;
    private int drawMode;
    private boolean[][] visited;
    private ArrayList<Unit> unitList;
    private Unit movingUnit;
    private int turn;
    private int team1count;
    private int team2count;
    MainActivity ref;

    public Map(int mapNum, Resources res, MainActivity ref) {
        drawMode = 0;
        turn = 1;
        team1count = 5;
        team2count = 5;
        this.ref = ref;
        unitList = new ArrayList<Unit>();
        resources = res;
        loadMap(mapNum);
        grid = new float[(width+1)*4+(height+1)*4];
        interval = ((screenWidth-(width+1))/width)+1; //The distance from one gridline to the next
        unusedPix = screenWidth - (((interval-1) * width) + width+1); //The number of pixels that will be unused for the width (range from 0 to width-1)
        int gridPointer = 0; //Current position in the grid array

        //Sets the start and end points of the lines of the grid
        startheight = (screenHeight/2)-(interval * (height/2)); //The smallest y position for an even height map
        if(height % 2 == 1) {
            startheight -= (interval / 2 + 1); //The smallest y position for an odd height map
        }
        for (int i = 0; i <= height; i++) {
            int yPosition = startheight + (i * interval);
            grid[gridPointer] = 0 + (unusedPix/2); //(Smaller) Half of the unused pixels at the start
            gridPointer++;
            grid[gridPointer] = yPosition;
            gridPointer++;
            grid[gridPointer] = screenWidth - ((unusedPix/2) + (unusedPix%2) + 1); //(Larger) Half of the unused pixels at the end
            gridPointer++;
            grid[gridPointer] = yPosition;
            gridPointer++;
        }
        for (int i = unusedPix/2; i < screenWidth; i+= interval) {
            grid[gridPointer] = i;
            gridPointer++;
            grid[gridPointer] = startheight;
            gridPointer++;
            grid[gridPointer] = i;
            gridPointer++;
            grid[gridPointer] = startheight + (height * interval);
            gridPointer++;
        }
        gridPaint = new Paint();
        gridPaint.setColor(WHITE);
    }

    public int getTurn() { return turn; }
    public void endTurn() {
        if ( turn == 1 ) {
            turn = 2;
        } else {
            turn = 1;
        }
    }
    //not currently being used
    public void setTile(int x, int  y,Tile tile){
        map[x][y] = tile;
    }

    /**
     * Method to return the tile at the given float coordinates
     * @param x x float coordinate
     * @param y y float coordinate
     * @return tile at given coordinates
     */
    public Tile getTile(float x, float y){
        int xpos = getX(x);
        int ypos =getY(y);
        if (x < (unusedPix/2) || x > screenWidth-(unusedPix/2)-(unusedPix%2) || y < startheight || y > startheight + height*interval){
            return null;
        }
        return map[xpos][ypos];
    }

    /**
     * Returns the integer of the width position in map array
     * @param x x float coordinate
     * @return integer of tile in map array
     */
    public int getX(float x){
        return (int) (((x-unusedPix/2)-2)/interval);
    }
    /**
     * Returns the integer of the height position in map array
     * @param y y float coordinate
     * @return integer of tile in map array
     */
    public int getY(float y){
        return (int) ((y-startheight)/interval);
    }

    //not currently being used
    public float[] getGridArray(){return grid;}

    public ArrayList<Unit> getUnitArray(){return unitList;}

    public void draw(Canvas canvas) {
        canvas.drawLines(grid, gridPaint);
        if (drawMode == 0) { // If false, simply draws the map
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Bitmap bit = BitmapFactory.decodeResource(resources, map[i][j].getPicture());
                    bit = Bitmap.createScaledBitmap(bit, interval - 1, interval - 1, false); // Resize the image to all be the same size
                    canvas.drawBitmap(bit, 1 + (unusedPix / 2) + (interval * i), 1 + startheight + (interval * j), null);
                }
            }
        }else { // Draws the map and highlights the most recent movement range of a unit
            Bitmap highlight = BitmapFactory.decodeResource(resources, R.drawable.standard_tile);
            highlight = Bitmap.createScaledBitmap(highlight, interval - 1, interval - 1, false);
            highlight.setHasAlpha(true);
            if (drawMode == 1) {
                highlight.eraseColor(0x4D0000FF); // 70% transparent
            } else if (drawMode == 2) {
                highlight.eraseColor(0x4DFF0000);
            } else{
                highlight.eraseColor(0x4D00FF00);
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Bitmap bit = BitmapFactory.decodeResource(resources, map[i][j].getPicture());
                    bit = Bitmap.createScaledBitmap(bit, interval - 1, interval - 1, false); // Resize the image to all be the same size
                    canvas.drawBitmap(bit, 1 + (unusedPix / 2) + (interval * i), 1 + startheight + (interval * j), null);
                    if (visited[i][j]){
                        canvas.drawBitmap(highlight, 1 + (unusedPix / 2) + (interval * i), 1 + startheight + (interval * j), null);
                    }
                }
            }
        }
        for (Unit u: unitList){
            Bitmap image = BitmapFactory.decodeResource(resources, u.getImage());
            image = Bitmap.createScaledBitmap(image, interval-1, interval-1, false);
            canvas.drawBitmap(image, 1 + (unusedPix / 2) + (interval * u.getX()), 1 + startheight + (interval * u.getY()), null);
        }
    }

    public void update() {
        //useless?
    }

    /**
     * Loads all the tiles from the given map number into the map global variable
     * @param mapNum Number of the map to load from arrays.xml
     * @return 0 if successful, -1 otherwise
     */
    public int loadMap(int mapNum){
        TypedArray level;

        //Case statement can be expanded to include more levels as made
        switch (mapNum){
            case 0:
                level = resources.obtainTypedArray(R.array.level0);
                team2count = 1;
                break;
            case 1:
                level = resources.obtainTypedArray(R.array.level1);
                break;
            case 2:
                level = resources.obtainTypedArray(R.array.level2);
                break;
            case 3:
                level = resources.obtainTypedArray(R.array.level3);
                break;
            case 4:
                level = resources.obtainTypedArray(R.array.level4);
                break;
            case 5:
                level = resources.obtainTypedArray(R.array.level5);
                break;
            default:
                return -1;
        }

        int index = 0;
        width =level.getInt(index,-1);
        index++;
        height = level.getInt(index, -1);
        index++;
        if(width < 1 || height < 1){ //If the numbers given were invalid or failed to load
            return -1;
        }
        map = new Tile[width][height];
        int column = 0;
        int row = 0;
        String item;
        while (index < level.length()){
            item = level.getString(index);

            // Each letter represents a different type of tile
            switch(item){
                case "s":
                    map[row][column] = new Tile(R.drawable.sand_tile_3, 2, 2, -25);
                    break;
                case "b":
                    map[row][column] = new Tile(R.drawable.building_tile_2, -1, 1, 0);
                    break;
                case "f":
                    map[row][column] = new Tile(R.drawable.standard_tile, 1, 0, 0);
                    break;
                case "w":
                    map[row][column] = new Tile(R.drawable.water_tile_3, 3, 2, -50);
                    break;
                default:
                    if (column == 0) {
                        column = height - 1;
                        row--;
                    }else{
                        column--;
                    }
                    Unit unit;
                    if (item.compareTo("art1") == 0) {
                        unit = new Artillery(row, column, 1);
                    } else if (item.compareTo("art2") == 0) {
                        unit = new Artillery(row, column, 2);
                    } else if (item.compareTo("inf1") == 0) {
                        unit = new Infantry(row, column, 1);
                    } else if (item.compareTo("inf2") == 0) {
                        unit = new Infantry(row, column, 2);
                    } else if (item.compareTo("cav1") == 0) {
                        unit = new Cavalry(row, column, 1);
                    } else if (item.compareTo("cav2") == 0) {
                        unit = new Cavalry(row, column, 2);
                    } else if (item.compareTo("mar1") == 0) {
                        unit = new Marksman(row, column, 1);
                    } else if (item.compareTo("mar2") == 0) {
                        unit = new Marksman(row, column, 2);
                    } else if (item.compareTo("med1") == 0) {
                        unit = new Medic(row, column, 1);
                    } else if (item.compareTo("med2") == 0) {
                        unit = new Medic(row, column, 2);
                    } else {
                        unit = new Medic(row, column, 1); //If matches nothing else unit interpreted as medic
                    }

                    map[row][column].setUnit(unit);
                    unitList.add(unit);
                    break;
            }
            index++;
            column++;
            if(column >= height){
                column = 0;
                row++;
            }
        }
        return 0;
    }

    /**
     * A function that turns all the tiles a unit can move to to 'true' in the visited global array
     * @param unit The unit to move
     */
    public void move(Unit unit){
        movingUnit = unit;

        // Ensure unit is on the right team and that they haven't moved already this turn
        if ( unit.getTeam() == turn && unit.getHasMoved() == 0 && unit.getHasDefended() == 0 ) {
            drawMode = 0; // Ensure this doesn't get drawn until its done
            visited = new boolean[width][height];
            int movement = unit.getMvmt();
            int x = unit.getX(); //The width location on the grid where the unit is
            int y = unit.getY(); //The height location on the grid where the unit is
            moveSpread(x - 1, y, movement);
            moveSpread(x + 1, y, movement);
            moveSpread(x, y - 1, movement);
            moveSpread(x, y + 1, movement);
            visited[x][y] = false; //The square the unit is on should not be able to be moved to
            drawMode = 1; // The visited range is ready to draw
        }
    }

    /**
     * A helper function that finds a units movement range by recursively stepping through all
     * possible permutations. Starts to have significant performance drops at around ten movement.
     * @param x the current width value being tested
     * @param y the current height value being tested
     * @param movement the remaining movement range
     */
    private void moveSpread(int x, int y, int movement) {
        if (x < 0 || y < 0 || x >= width || y >= height ){
            return;
        }
        Tile tile = map[x][y];
        if (movement < tile.getMovementReduction() || tile.getMovementReduction() == -1){
            return;
        }else{
            movement = movement - tile.getMovementReduction();
            if(!map[x][y].hasUnit()) {
                visited[x][y] = true;
            }
            moveSpread(x-1, y, movement);
            moveSpread(x+1, y, movement);
            moveSpread(x, y-1, movement);
            moveSpread(x, y+1, movement);
        }
    }

    /**
     * @return Boolean of if map is drawing movement range
     */
    public boolean drawingMove() { return drawMode == 1;}

    /**
     * @return Boolean of if map is drawing attack range
     */
    public boolean drawingAttack() { return drawMode == 2;}

    /**
     * @return Boolean of if map is drawing attack range
     */
    public boolean drawingSpecial() { return drawMode == 3;}
    /**
     * Returns the boolean value that is if x and y are in the current moving units range
     * @param x x position in map array
     * @param y y position in map array
     * @return Boolean of if x and y are in movement range
     */
    public boolean canMove(int x, int y){ return visited[x][y];}

    /**
     * Moves the unit who is currently displaying movement range and stops drawing movement
     * @param x x position to move unit to
     * @param y y position to move unit to
     */
    public void moveUnit(int x, int y){
        drawMode = 0;
        map[movingUnit.getX()][movingUnit.getY()].setUnit(null);
        map[x][y].setUnit(movingUnit);
        movingUnit.setX(x);
        movingUnit.setY(y);
        movingUnit.setHasMoved(1);
        if(movingUnit.getHasAttacked() == 1){
            movingUnit.setWait();
        }
    }

    /**
     * Stops the map from drawing the movement range
     */
    public void stopDrawingMove() { drawMode = 0;}

    /**
     * Draws the attack range of a given unit by recursively finding all permutations
     * @param unit The unit to find the attack range of
     */
    public void attackRange(Unit unit, int mode) {
        int beforeX;
        int beforeY;

        movingUnit = unit;
        if ( unit.getTeam() == turn && unit.getHasAttacked() == 0 && unit.getHasDefended() == 0 ) {
            drawMode = 0; // Ensure this doesn't get drawn until its done
            visited = new boolean[width][height];
            int attack = unit.getRange();
            int x = unit.getX(); //The width location on the grid where the unit is
            int y = unit.getY(); //The height location on the grid where the unit is
            attackSpread(x - 1, y, attack);
            attackSpread(x + 1, y, attack);
            attackSpread(x, y - 1, attack);
            attackSpread(x, y + 1, attack);
            visited[x][y] = false; //The square the unit is on should not be able to be attacked
            drawMode = 2; // The visited range is ready to draw
        }
    }

    public void attackRange( Unit unit ) {
        attackRange( unit, 2 );
    }

    /**
     * A helper function that finds a units attack range by recursively stepping through all
     * possible permutations.
     *
     * @param x      the current width value being tested
     * @param y      the current height value being tested
     * @param attack the remaining attack range
     */
    private void attackSpread(int x, int y, int attack) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return;
        }
        if (map[x][y].getMovementReduction() == -1 || attack == 0) {
            return;
        } else {
            attack = attack - 1;
            visited[x][y] = true;
            attackSpread(x - 1, y, attack);
            attackSpread(x + 1, y, attack);
            attackSpread(x, y - 1, attack);
            attackSpread(x, y + 1, attack);

        }
    }

    /**
     * Attacks a unit in a valid range or cancels the attack command
     * @param x X coordinate of unit getting attacked
     * @param y Y coordinate of unit getting attacked
     */
    public void attack(int x, int y){
        drawMode = 0;
        if(visited[x][y]){
            if(map[x][y].hasUnit()){
                Unit unit = map[x][y].getUnit();
                if ( unit.getTeam() != turn ) {
                    int attackPower = (movingUnit.getAttack() - unit.getDefense() - unit.getTempDefense());
                    if (attackPower > 0){
                        unit.setHp(unit.getHp() - attackPower);
                    }
                    movingUnit.setHasAttacked(1);
                    if (movingUnit.getHasMoved() == 1){
                        movingUnit.setWait();
                    }
                    if (unit.getHp() <= 0) {
                        unitList.remove(unit);
                        map[x][y].setUnit(null);
                        if ( turn == 1 ) {
                            team2count--;
                            if ( team2count == 0 ) {
                                ref.onGameEnd(1);
                            }
                        } else {
                            team1count--;
                            if ( team1count == 0 ) {
                                ref.onGameEnd(2);
                            }
                        }
                    }
                } else {
                    System.out.println( "NO FRIENDLY FIRE!!!\n");
                }
            }
        }
    }

    /**
     * Makes the unit defend, increasing defence and ending its turn
     * @param unit Unit who is defending
     */
    public void defend(Unit unit){
        unit.setHasDefended(1);
        unit.setTempDefense(100); //Twice what defend actually boosts by
        unit.setWait();
    }

    /**
     * Heals a unit in a valid range or cancels the special command
     * @param x X coordinate of unit getting healed
     * @param y Y coordinate of unit getting healed
     */
    public void special(int x, int y){
        drawMode = 0;
        if (visited[x][y]) {
            if (map[x][y].hasUnit()) {
                Unit unit = map[x][y].getUnit();
                if ( unit.getTeam() == turn && unit.getHasHealed() == 0 ) {
                    unit.setHp(unit.getHp() + ((Medic) movingUnit).getHeal()); //Will only be here if movingUnit is a medic
                    if (unit.getHp() > unit.getHpMax()) {
                        unit.setHp(unit.getHpMax());
                    }
                    unit.setHasHealed(1);
                    unit.setWait();
                }
            }
        }
    }
}