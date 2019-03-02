package com.example.tacstratgame;

/**
 * Class that holds the information for a specific tile on a map. Parameters can only be changed
 * upon instantiation of the Tile.
 */
public class Tile {
    private int picture; //The integer pointing to the image in drawable
    private int movementReduction; // The amount that traveling over this tile will reduce available movement
    private int effect; // Possible special effect of this tile


    public Tile(int picture, int movementReduction, int effect){
        this.picture = picture;
        this.movementReduction = movementReduction;
        this.effect = effect;
    }

    public int getPicture() {
        return picture;
    }

    public int getMovementReduction() {
        return movementReduction;
    }

    public int getEffect() {
        return effect;
    }
}