package com.game.model;

import com.game.manager.GameElement;
import com.game.manager.GameLoad;
import com.game.manager.ModelManager;
import com.game.model.Enum.Direction;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.awt.*;
import java.util.SplittableRandom;
import java.util.StringJoiner;

public class Boom extends ElementObj {


    private long localGameTime;
    private int imgX;
    private int length;

    @Override
    public void showElement(Graphics graphics) {
        graphics.drawImage(this.getIcon().getImage(),
                this.getX() * this.getW(), this.getY() * this.getH() - 7,
                this.getX() * this.getW() + this.getW(),
                this.getY() * this.getH() + this.getH(),
                0 + imgX * 32, 7,
                32 + imgX * 32, 46, null
        );

    }

    @Override
    protected void updateImage(long gameTime) {
        if (gameTime - localGameTime > 30) {
            imgX++;
            if (imgX > 3) {
                imgX = 0;
            }
            localGameTime = gameTime;
        }
    }

    @Override
    public ElementObj createElement(String str) {
        String strs[] = str.split(",");

        this.setX(((Integer.parseInt(strs[0]) + 16) / 32));
        this.setY(((Integer.parseInt(strs[1]) + 16) / 32));
        this.length = Integer.parseInt(strs[2]);
        this.setIcon(GameLoad.playIconMap.get("boom"));
        this.setW(32);
        this.setH(32);
        System.out.println(GameLoad.playIconMap.get("boom"));
        imgX = 0;
        localGameTime = 1;
        boom(this);
        return this;
    }

    private void boom(Boom boom) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
                boom.setLiveStatus(false);
                ModelManager modelManager = ModelManager.getManager();
                ElementObj elementObj[][] = modelManager.getElementsByKey(GameElement.MAP);
                for (Direction direction: Direction.getDirectionList()) {
                    int temp = 1;
                    int dx = Direction.getChangeByDirection(direction)[0];
                    int dy = Direction.getChangeByDirection(direction)[1];
                    int x =this.getX();
                    int y = this.getY();
                    for (int i = 0; i < temp; i++) {
                        if (direction == Direction.none){
                            temp=1;
                        }
                        x += dx;
                        y +=dy;
                        if(x==0||y==0||x==12||y==14){
                            i=temp-1;
                        }

                        if(elementObj[x][y]!=null && elementObj[x][y].isLiveStatus()!=false){
                            elementObj[x][y].setLiveStatus(false);
                            System.out.println(elementObj[x][y].toString());

                            elementObj[x][y]=null;
                            temp--;
                        }
                        String statement;
                        if(i== (temp-1)) {
                            statement = x + "," + y + "," + direction + "," + "true";
                        }
                        else {
                            statement = x + "," + y + "," + direction + "," + "false";
                        }
                        ElementObj boomPiece = new BoomPiece().createElement(statement);
                        modelManager.addElement(boomPiece,GameElement.BoomPiece,x,y);
                    }
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }



}