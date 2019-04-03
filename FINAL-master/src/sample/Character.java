package sample;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import java.io.IOException;


public abstract class Character extends HBox {
    //Fields
    protected double speed, jumpConstant, jumpVariable, fallConstant;

    protected boolean isBlocking, isFalling,isJumping, facingRight, isRunningRight, isRunningLeft;
    protected int health;
    protected int damage = 10;
    protected int dmgAmount;
    protected int atkRange = 5;


    protected Image standingRight, standingLeft, runningRight, runningLeft,jumpingRight,jumpingLeft, attackingLeft, attackingRight;
    protected Image currentImage;//place holder such that images are not loaded continuously: Currently being tested to see if it's faster than loading continuously
    ImageView characterImage;


    public Character(double initialX, double initialY,double startingSpeed,double startingJumpConstant) throws IOException {//Constructor
        this.setLayoutX(initialX);
        this.setLayoutY(initialY);
        this.setSpeed(startingSpeed);
        this.setJumpConstant(startingJumpConstant);





        //Movement Images (these are arrows for the abstract character, but subclasses will overwrite the following Images)
        standingRight = new Image("shaggy_idle_right.png");
        standingLeft = new Image("shaggy_idle_left.png");
        runningRight = new Image("shaggy_move_right.png");
        runningLeft = new Image("shaggy_move_left.png");
        jumpingLeft = new Image("shaggy_jump_left.png");
        jumpingRight = new Image("shaggy_jump_right.png");
        attackingLeft = new Image("shaggy_attack_left.png");
        attackingRight = new Image("shaggy_attack_right.png");


        characterImage = new ImageView(jumpingLeft);
        this.getChildren().add(characterImage);
    }


    //Image checking----------------------------------------------------------------------------------------------------
    public void setImage(Image image){
        characterImage.setImage(image);
    }

    public Image getCurrentImage(){return this.currentImage;}//The image that makes the character visible

    protected boolean differentImage(Image img){ return !(img == this.getCurrentImage()); }//true when characterImage is not the same as the parameter passed here

    //Horizontal motion-------------------------------------------------------------------------------------------------
    public void standRight(){
        this.facingRight = true;
        this.isRunningRight = false;
        this.isRunningLeft = false;

        if(!this.isFalling){
            if(this.differentImage(this.standingRight) ) {
                this.setImage(standingRight);
            }
        }else{
            if(this.differentImage(this.jumpingRight)){
                this.setImage(jumpingRight);
            }
        }
    }

    public void standLeft(){
        this.facingRight = false;
        this.isRunningRight = false;
        this.isRunningLeft = false;

        if(!this.isFalling){
            if(this.differentImage(this.standingLeft)) {
                this.setImage(standingLeft);
            }
        }else{
            if(this.differentImage(this.jumpingLeft)) {
                this.setImage(jumpingLeft);
            }
        }

    }

    public void runRight(){
        if(!this.isFalling){
            if(this.differentImage(this.runningRight)) {
                this.setImage(runningRight);
            }
        }else{
            if(this.differentImage(this.jumpingRight)) {
                this.setImage(jumpingRight);
            }
        }
        this.isRunningRight = true;
        this.isRunningLeft = false;
        this.facingRight = true;
    }

    public void runLeft(){
        if(!this.isFalling){
            if(this.differentImage(this.runningLeft)) {
                this.setImage(runningLeft);
            }
        }else{
            if(this.differentImage(this.jumpingLeft)) {
                this.setImage(jumpingLeft);
            }
        }
        this.isRunningLeft = true;
        this.isRunningRight = false;
        this.facingRight = false;
    }

    //Vertical motion---------------------------------------------------------------------------------------------------
    public void jump(){
        this.jumpVariable = this.jumpConstant;
        this.gravity();
    }

    public void fall(){
        this.setJumping(false);
        this.gravity();
    }

    public void land(){
        this.jumpVariable = 0;
        this.setFalling(false);
        this.setFallConstant(0);
        this.setJumping(false);
        if(this.facingRight){
            this.standRight();
        }else{
            this.standLeft();
        }
    }

    private void gravity(){
        this.setFalling(true);
        this.setFallConstant(this.getFallConstant()+2);
        this.setLayoutY(this.getLayoutY() + this.getFallConstant() - this.jumpVariable);
        if(facingRight) {
            if(differentImage(jumpingRight)) {
                this.setImage(jumpingRight);
            }
            // not sure else if is needed, check later
        }else{
            if(differentImage(jumpingLeft)) {
                this.setImage(jumpingLeft);
            }
        }
    }





    // [Attacks]--------------------------------------------------------------------------------------------------------
    //Hey Sandy, I simplified some of the attack methods if you are ok with that
    public void attack(Character opponent){
        if(this.isTouching(opponent)) {//when the characters are touching
            opponent.setHealth(opponent.getHealth() - this.damage); //deals damage to opponent by making their health smaller
        }

        //loading the attack animation
        if (facingRight) {
            this.setImage(attackingRight);
        } else{
            this.setImage(attackingLeft);
        }
    }

    public boolean isTouching( Character p2){//used to determine whether or not the characters are touching on the stage
        return (Math.abs(this.getLayoutX() -p2.getLayoutX())* 2 < (this.getWidth() + p2.getWidth())) && (Math.abs(this.getLayoutY() - p2.getLayoutY())*2<(this.getHeight()+p2.getHeight()));
    }

    // takeHit where if inreach is within range and is not blocking than decrease opponent hp
    /*public void takeHit(double pos1, double pos2){
        boolean reach = false;
        if (pos1 <= (pos2+atkRange) || pos1 >= (pos2+atkRange) || pos1 <=(pos2-atkRange) || pos1 >= (pos2-atkRange)){
            reach = true;
        }
        if(!this.isBlocking() && reach == true){ //when this character is not blocking it then takes damage
            this.setHealth(this.getHealth()-this.damage);
        }
    }*/

    /*// attack method
    public void attack(Character opponent, double pos1, double pos2){
        opponent.takeHit(pos1, pos2);
        if (isRunningLeft == true || facingRight == false) {
            this.setImage(attackingLeft);
        } else if (isRunningLeft == false || facingRight == true){
            this.setImage(attackingRight);
        }
    }*/
     /*// inReach method where attack will be valid if position 1 and near position 2
    public boolean inReach(int pos1, int pos2){
        boolean reach = false;
        if (pos1 <= (pos2+this.atkRange) || pos1 >= (pos2+this.atkRange)){
            reach = true;
        } return reach;
    }*/







    //Accessors and mutators
    public void setFallConstant(double newFallConstant){this.fallConstant = newFallConstant;}

    public double getFallConstant(){return this.fallConstant;}

    public boolean isBlocking(){ return this.isBlocking;}

    public boolean isRunningRight() {return this.isRunningRight;}

    public boolean isRunningLeft(){return  this.isRunningLeft;}

    public boolean isFalling(){return this.isFalling;}

    public boolean isJumping(){ return this.isJumping;}

    public void setJumping(boolean newJumping){this.isJumping = newJumping;}

    public int getHealth(){return this.health;}

    public void setHealth(int newHealth){ this.health = newHealth;}





    public void setFacingRight(boolean newFacingRight){ this.facingRight = newFacingRight; }

    public boolean getFacingRight(){ return this.facingRight;}

    public void setBlocking(boolean newBlocking){ this.isBlocking = newBlocking; }

    public void setFalling(boolean newFalling){this.isFalling = newFalling;}

    public double getSpeed(){return this.speed;}

    public void setSpeed(double newSpeed){this.speed = newSpeed;}

    public double getJumpVariable(){return this.jumpVariable;}

    public void setJumpConstant(double newJumpConstant) {this.jumpConstant = newJumpConstant;}

}
