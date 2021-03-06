package gameSystem;

import javafx.scene.image.Image;
import sample.Main;

import java.io.File;


public class Crocodile extends Entity {

    private final double speed;
    private final Image crocodile1Right;
    private final Image crocodile2Right;
    private final Image crocodile1Left;
    private final Image crocodile2Left;
    private boolean hungry=false;


    public Crocodile(int xPos,int yPos,int size,double speed){

        this.speed=speed;
        crocodile1Right = new Image(new File(Main.IMAGE_PATH + "crocodile1Right.png").toURI().toString(), size, 30, true, true);
        crocodile2Right = new Image(new File(Main.IMAGE_PATH+ "crocodile2Right.png").toURI().toString(), size, 30,true, true);
        crocodile1Left = new Image(new File(Main.IMAGE_PATH + "crocodile1Left.png").toURI().toString(), size, 30,true, true);
        crocodile2Left = new Image(new File(Main.IMAGE_PATH + "crocodile2Left.png").toURI().toString(), size, 30,true, true);
        setX(xPos);
        setY(yPos);
        setImage(crocodile1Right);

    }


    public double getSpeed(){
        return this.speed;
    }


    public boolean isHungry(){return hungry;}


    @Override
    public void movement(Long now) {

        move(speed,0);
        if(speed>0){
            if ((now/3/ 900000000 +((int)this.getY()/100) ) % 2 == 1) {
                setImage(crocodile1Right);
                hungry=true;
            }else if ((now/3/ 900000000 +((int)this.getY()/100) )% 2 == 0) {
                setImage(crocodile2Right);
                hungry=false;
            }
        }else{
            if ((now/3/ 900000000 +((int)this.getY()/100) )% 2 == 1) {
                setImage(crocodile1Left);
                hungry=true;

            }else if ((now/3/ 900000000 +((int)this.getY()/100) ) % 2 == 0) {
                setImage(crocodile2Left);
                hungry=false;
            }
        }
        if (getX()>500 && speed>0)
            setX(-180);
        if (getX()<-50 && speed<0)
            setX(700);
    }
}