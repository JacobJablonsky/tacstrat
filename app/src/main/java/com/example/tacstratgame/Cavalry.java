package com.example.tacstratgame;

public class Cavalry implements Unit {

    private int attack = 120;
    private int defense = 60;
    private int hp = 400;
    private int hpMax = 400;
    private int mvmt = 4;
    private int range = 1;
    private int x;
    private int y;
    private int team;
    private int hasMoved;
    private int hasAttacked;
    private int hasDefended;
    private int image;
    private int activeImage;
    private int waitImage = R.drawable.cat_wait;
    private String name = "Cavalry";
    private int tempDefense;

    public Cavalry(int x, int y, int team){
        this.x = x;
        this.y = y;
        this.team = team;
        if (team == 1){
            activeImage = R.drawable.cat;
        }else{
            activeImage = R.drawable.cat_2;
        }
        image = activeImage;
        hasMoved = 0;
        hasAttacked = 0;
        hasDefended = 0;
    }

    @Override
    public int getHasDefended() { return hasDefended; }
    public int getHasAttacked() { return hasAttacked; }
    public int getHasMoved() { return hasMoved; }

    @Override
    public int getHasHealed() {
        return 0;
    }

    public int getTeam() { return team; }
    public int getAttack() {
        return attack;
    }
    public int getDefense(){
        return defense;
    }
    public int getHp() {
        return hp;
    }
    public int getHpMax() { return hpMax; }
    public int getMvmt(){
        return mvmt;
    }
    public int getRange(){
        return range;
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getImage(){ return image; }
    public String getName() { return name; }
    public int getTempDefense() { return tempDefense; }

    @Override
    public void setHasHealed(int hasHealed) {

    }

    public void setHasAttacked(int hasAttacked) { this.hasAttacked = hasAttacked; }
    public void setHasDefended(int hasDefended) { this.hasDefended = hasDefended; }
    public void setHasMoved(int hasMoved) { this.hasMoved = hasMoved; }
    public void setAttack(int attack){
        this.attack = attack;
    }
    public void setDefense(int defense){
        this.defense = defense;
    }
    public void setHp(int hp){
        this.hp = hp;
    }
    public void setHpMax(int hpMax) { this.hpMax = hpMax; }
    public void setMvmt(int mvmt){
        this.mvmt = mvmt;
    }
    public void setRange(int range){
        this.range = range;
    }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setName(String name) { this.name = name; }
    public void setActive(){ this.image = activeImage; }
    public void setTempDefense(int tempDefense){ this.tempDefense = tempDefense; }

    public void setWait() {
        if(image == activeImage){
            image = waitImage;
        }else{
            image = activeImage;
        }
    }

    public void printStats( ) {
        System.out.printf( "Cavalry Stats:\n Attack: %d\n Defense: %d\n Health Points: %d\n Movement: %d\n Attack Range: %d\n", attack, defense, hp, mvmt, range );

    }

    public static void main( String []args ) {
        Cavalry cal = new Cavalry(0,0, 1);
        cal.printStats();
    }
}
