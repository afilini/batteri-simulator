/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package batteri;

import cms.Food;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author mattia
 */
public class ScarsoGerm extends Batterio {
    private int direzione;
    private final int sens = 10;
    private int operazioni;
    private int verso;
    Food fo;
    boolean ciboPresente = false;
    boolean cibino = false;
    public ScarsoGerm(int x, int y, Color c, Food f){
        super(x,y,c,f);
        fo = f;
        direzione = 0;
        operazioni = 0;
        verso = new Random().nextInt(2);
    }
    @Override
    protected void Sposta(){
        if (!ciboPresente){
            int controlloX = x;
            int controlloY = y;
            if(direzione == 0){
                //direzione = 1;
                for (int i = -5; i < 5; i++){
                    for (int j = -5; j < 5; j++){
                        if(controlloX+sens*j < food.getWidth() && controlloY+sens*i < food.getHeight() && controlloX+sens*j > 1 && controlloY+sens*i > 1){
                            if(fo.isFood(controlloX + sens*j, controlloY + sens*i)){
                                x = controlloX+ sens*j;
                                y = controlloY+ sens*i;
                                ciboPresente = true;
                                return;
                            }
                        }
                    }
                }
                
            }
            if(!ciboPresente){
                if(verso == 0){
                    if(x+1 >= fo.getWidth()){
                        verso = 1-verso;                                              
                    }
                    else
                        x++;
                }
                if(verso == 1){
                    if(x-1 <= 0){
                        verso = 1-verso;                                              
                    }
                    else
                        x--;
                }
               
            }
        }
        else{
            cibino = false;
            int controlloX = x;
            int controlloY = y;
            for (int i = -5; i < 5; i++){
                for (int j = -5; j < 5; j++){
                    if(controlloX + j < food.getWidth() && controlloY + i < food.getHeight() && controlloX + j > 1 && controlloY + i > 1){
                        if(fo.isFood(controlloX + j, controlloY + i)){
                            x = controlloX + j;
                            y = controlloY + i;
                            cibino = true;
                            return;
                        }
                    }
                }
            }
                
            if (!cibino){
                ciboPresente = false;
                //y = y + new Random().nextInt(30)-15;
            }    
        }
        
    }
    
    @Override
    public Batterio Clona(){
       return new ScarsoGerm(x,y,colore,food); 
    }
    
    @Override
    public String toString(){
        return "ScarsoGerm";
    }
    
}
