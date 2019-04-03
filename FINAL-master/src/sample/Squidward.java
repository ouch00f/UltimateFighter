package sample;

import java.io.IOException;

public class Squidward extends Character {

    public Squidward(double initialX, double initialY) throws IOException {
        super(initialX,initialY,10,30);//initially facing left with the attributes called into the super
        this.setWidth(10);
        this.setHeight(10);
        this.standLeft();

    }
}
