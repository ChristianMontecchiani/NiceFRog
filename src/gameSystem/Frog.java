package gameSystem;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import sample.Main;

import java.io.File;
import java.util.List;



public class Frog extends Entity {

    private final static String jump = new File(Main.AUDIO_PATH + "jump.wav").toURI().toString();
    private final static String goal = new File(Main.AUDIO_PATH + "goal.wav").toURI().toString();
    private final static String bonusSound = new File(Main.AUDIO_PATH + "bonus.wav").toURI().toString();
    private final static AudioClip bonus = new AudioClip(bonusSound);
    private final static AudioClip frogJump = new AudioClip(jump);
    private final static AudioClip frogGoal = new AudioClip(goal);

    List<Entity> entities;


    Image imgW1;
    Image imgA1;
    Image imgS1;
    Image imgD1;
    Image imgW2;
    Image imgA2;
    Image imgS2;
    Image imgD2;


    double movementV = 31.2;
    double movementH = 15;
    double crocSpeed = 0; //serve per capire se si muove verso destra o sinistra

    Scene game;
    boolean timeExpired = false;
    boolean isDeath = true;//per evitare che i key pressed/realesed in eccesso spostino l'animazione della morte
    boolean noMove = false;//per evitare che la rana continui a spostarsi se morta
    boolean carDeath = false;//per continuare a rimanere nell' if anche se finisce collisione
    private boolean singleClick = true;//per continuare a rimanere nell' if anche se finisce collisione
    private static boolean death = false;


    int size = 30;//serve a fare lo scaling della rana


    public Frog(String link, Scene scene, List<Entity> interceptable) {
        setImage(new Image(new File(link).toURI().toString(), size, size, true, true));
        setX(135);
        setY(475);
        this.entities = interceptable;
        imgW1 = new Image(new File(Main.IMAGE_PATH + "froggerUp.png").toURI().toString(), size, size, true, true);
        imgA1 = new Image(new File(Main.IMAGE_PATH + "froggerLeft.png").toURI().toString(), size, size, true, true);
        imgS1 = new Image(new File(Main.IMAGE_PATH + "froggerDown.png").toURI().toString(), size, size, true, true);
        imgD1 = new Image(new File(Main.IMAGE_PATH + "froggerRight.png").toURI().toString(), size, size, true, true);
        imgW2 = new Image(new File(Main.IMAGE_PATH + "froggerUpJump.png").toURI().toString(), size, size, true, true);
        imgA2 = new Image(new File(Main.IMAGE_PATH + "froggerLeftJump.png").toURI().toString(), size, size, true, true);
        imgS2 = new Image(new File(Main.IMAGE_PATH + "froggerDownJump.png").toURI().toString(), size, size, true, true);
        imgD2 = new Image(new File(Main.IMAGE_PATH + "froggerRightJump.png").toURI().toString(), size, size, true, true);

        game = scene;
    }

    public void control() {
        //movimento
        if (!death) {

            game.setOnKeyPressed(event -> {

                if ((event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) && singleClick && getY() > 120) {
                    singleClick = false;
                    if (isDeath) {
                        move(0, -movementV);
                        setImage(imgW2);
                    }

                } else if ((event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) && singleClick && getX() > 10) {
                    singleClick = false;
                    if (isDeath) {
                        move(-movementH, 0);
                        setImage(imgA2);
                    }

                } else if ((event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) && singleClick && getY() < 475) {
                    singleClick = false;
                    if (isDeath) {
                        move(0, movementV);
                        setImage(imgS2);
                    }

                } else if ((event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) && singleClick && getX() < 330) {
                    singleClick = false;
                    if (isDeath) {
                        move(movementH, 0);
                        setImage(imgD2);
                    }
                }
            });
        }

        if (!death) {


            game.setOnKeyReleased(event -> {

                if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                    if (isDeath) {
                        setImage(imgW1);
                        singleClick = true;
                        frogJump.play(20);
                    }
                } else if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                    if (isDeath) {
                        setImage(imgA1);
                        singleClick = true;
                        frogJump.play(20);
                    }
                } else if (event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) {
                    if (isDeath) {
                        setImage(imgS1);
                        singleClick = true;
                        frogJump.play(20);
                    }
                } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                    if (isDeath) {
                        setImage(imgD1);
                        singleClick = true;
                        frogJump.play(20);
                    }
                }
            });
        }
    }


    @Override
    public void movement(Long now) {
        control();

        if (getX() < 0 || getX() > 350 || getY()>505) {
            GameScene.lifelost = true;
            death = true;
            GameScene.timeLeft = GameScene.timeMax;
            GameScene.FROGGER_LIVES--;
            setX(135);
            setY(475);
            Death.frogDie.play(20);
        }

        if (getY() == 475 && getX() == 135) {
            death = false;
            noMove = false;
            carDeath = false;
            timeExpired = false;
            GameScene.lifelost = false;
        }

        if (GameScene.timeLeft == 0 || timeExpired) {
            timeExpired = true;
            death = true;
            isDeath = false;
            isDeath = Death.carDeath(now, this);
            GameScene.timeLeft = GameScene.timeMax;
        }

        if(getY()>260)
        if (Collision.specificCollision(entities, this, Vehicle.class) || Collision.specificCollision(entities, this, Snake.class) || carDeath) {
            carDeath = true;
            death = true;
            isDeath = false;
            isDeath = Death.carDeath(now, this);
            GameScene.timeLeft=GameScene.timeMax;
        }


        //ACQUA
        if (getY() < 260 && getY() > 107) {
            if (Collision.specificCollision(entities, this, Turtle.class) && !noMove) {

                Turtle turtle = Collision.getOne(entities, this, Turtle.class);
                if (!turtle.isWet())
                    this.move(turtle.getSpeed(), 0);
                else {
                    death = true;
                    isDeath = false;
                    noMove = true;
                    isDeath = Death.waterDeath(now, this);
                    GameScene.timeLeft=GameScene.timeMax;
                }

            } else if (Collision.specificCollision(entities, this, Log.class) && !noMove) {
                Log log = Collision.getOne(entities, this, Log.class);
                this.move(log.getSpeed(), 0);

            } else if (Collision.specificCollision(entities, this, Crocodile.class) && !noMove) {
                Crocodile croc = Collision.getOne(entities, this, Crocodile.class);
                crocSpeed = croc.getSpeed();
                this.move(crocSpeed, 0);
                if (croc.isHungry())
                    if ((this.getX() >= croc.getX() + 65 && crocSpeed > 0) || (this.getX() <= croc.getX() + 25 && crocSpeed < 0)) {
                        death = true;
                        isDeath = false;
                        noMove = true;
                        isDeath = Death.waterDeath(now, this);
                        GameScene.timeLeft=GameScene.timeMax;
                    }

                    } else {
                        death = true;
                        isDeath = false;
                        noMove = true;
                        isDeath = Death.waterDeath(now, this);
                        GameScene.timeLeft=GameScene.timeMax;
                    }

            }


            //ZONA VITTORIA
            if (getY() < 107) {
                if (Collision.specificCollision(entities, this, Burrow.class)) {
                    frogGoal.play(20);
                    GameScene.points += 100 + (Main.difficulty * 30);
                    GameScene.scoreLabel.setText("Score: " + GameScene.points);
                    Burrow b = Collision.getOne(entities, this, Burrow.class);
                    if (!b.isFull()) {
                        if (Collision.specificCollision(entities, this, Bonus.class)) {
                            GameScene.points += 150 + (Main.difficulty *30);
                            GameScene.scoreLabel.setText("Score: " + GameScene.points);
                            System.out.println("bonus");
                            bonus.play(20);
                        }

                        this.setX(135);
                        this.setY(475);
                        b.setFrogEnd();
                        GameScene.burrowCounter++;
                        GameScene.timeLeft=GameScene.timeMax;
                        RandomBonus.removePos((int) b.getX());
                    } else {
                        death = true;
                        isDeath = false;
                        isDeath = Death.waterDeath(now, this);

                    }


                } else {
                    death = true;
                    isDeath = false;
                    isDeath = Death.waterDeath(now, this);
                }
            }
    }
}

