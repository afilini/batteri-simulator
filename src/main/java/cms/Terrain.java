/*
  Copyright (C) 2013 - 2016 Alessandro Bugatti (alessandro.bugatti@istruzione.it)
  Copyright (C) 2016 -      Alekos Filini (alekos.filini@gmail.com)

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cms;

import batteri.Batterio;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Classe che rappresenta il terreno di gara
 *
 * @author Alessandro Bugatti   v0   - v0.1
 * @author Alekos Filini        v0.1 -
 * @version 0.2
 */

public class Terrain {
    /**
     * Costruttore della classe
     *
     * @param food            classe di tipo {@link Food} che controlla il cibo immesso nel campo di gara
     * @param listaBatteri    lista dei batteri da visualizzare a schermo
     * @param backgroundColor colore di sfondo dello schermo
     * @param numeroBatteri   {@link HashMap} che contiene il numero dei batteri rimasti in vita
     */

    public Terrain(Food food, LinkedList<Batterio> listaBatteri, Color backgroundColor, HashMap<String, Integer> numeroBatteri) {
        this.food = food;
        batteri = listaBatteri;
        sfondo = backgroundColor;
        this.numeroBatteri = numeroBatteri;
    }

    /**
     * Metodo che disegna l'immagine da visualizzare a schermo
     *
     * @return l'immagine generata contentente batteri e cibo
     */

    public BufferedImage paintImage() {
        BufferedImage ans = new BufferedImage(1024, 700, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = ans.createGraphics();

        g.setPaint(sfondo);
        g.fillRect(0, 0, ans.getWidth(), ans.getHeight());

        LinkedList<Batterio> babies = new LinkedList<>();
        for (Iterator<Batterio> i = batteri.iterator(); i.hasNext(); ) {
            Batterio batterio = i.next();
            g.setColor(sfondo);
            g.fillRect(batterio.getX(), batterio.getY(), 2, 2);
            batterio.run();
            if (batterio.morto()) {
                numeroBatteri.put(batterio.toString(), numeroBatteri.get(batterio.toString()) - 1);
                i.remove();
            } else if (batterio.fecondo()) {
                Batterio b = batterio.clona();
                babies.add(b);
                numeroBatteri.put(batterio.toString(), numeroBatteri.get(batterio.toString()) + 1);
            } else {
                g.setColor(batterio.getColore());
                g.fillRect(batterio.getX(), batterio.getY(), 3, 3);
            }
        }

        batteri.addAll(babies);

        //Ridisegna il cibo a ogni ciclo
        g.setColor(Color.GREEN);
        for (int i = 0; i < food.getWidth(); i++)
            for (int j = 0; j < food.getHeight(); j++)
                if (food.isFood(i, j))
                    g.fillRect(i, j, 2, 2);

        return ans;
    }

    public void toggleFood() {
        food.squareDistribution(50, 500);
    }

    private Food food;
    private Color sfondo;
    LinkedList<Batterio> batteri;
    private HashMap<String, Integer> numeroBatteri;
}
