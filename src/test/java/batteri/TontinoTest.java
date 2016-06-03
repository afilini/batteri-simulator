/*
  Copyright (C) 2016 Alekos Filini (alekos.filini@gmail.com)

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

package batteri;

import cms.Food;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

/**
 * Classe di test per il batterio di esempio {@link Tontino}
 */

public class TontinoTest {
    private Food food;

    /**
     * Crea un oggetto di tipo {@link Food} da utilizzare nei test
     */

    @Before
    public void setUp() {
        food = new Food(10, 10);
    }

    /**
     * Controlla lo spostamento in caso il batterio si trovi al centro del campo
     */

    @Test
    public void testSpostaCentro() {
        Tontino tontino = new Tontino(5, 5, Color.BLUE, food);

        tontino.sposta();
        int diffX = 5 - tontino.getX();
        int diffY = 5 - tontino.getY();

        Assert.assertTrue(Math.abs(diffX) <= 1);
        Assert.assertTrue(Math.abs(diffY) <= 1);
    }

    /**
     * Metodo che controlla lo spostamento del batterio in caso si trovi sul limite inferiore del campo
     */

    @Test
    public void testSpostaLimiteMinore() {
        Tontino tontino = new Tontino(0, 0, Color.BLUE, food);

        tontino.sposta();

        Assert.assertTrue(tontino.getX() >= 0);
        Assert.assertTrue(tontino.getY() >= 0);
    }

    /**
     * Metodo che controlla lo spostamento del batterio in caso si trovi sul limite superiore del campo
     */

    @Test
    public void testSpostaLimiteMaggiore() {
        Tontino tontino = new Tontino(9, 9, Color.BLUE, food);

        tontino.sposta();

        Assert.assertTrue(tontino.getX() <= 9);
        Assert.assertTrue(tontino.getY() <= 9);
    }

    /**
     * Metodo che testa che il processo di clonazione avvenga correttamente
     */

    @Test
    public void testClona() {
        Tontino tontino = new Tontino(0, 0, Color.BLUE, food);
        Batterio figlio = tontino.clona();

        Assert.assertEquals(tontino.getX(), figlio.getX());
        Assert.assertEquals(tontino.getY(), figlio.getY());
        Assert.assertEquals(tontino.getColore(), figlio.getColore());
    }

    /**
     * Metodo che testa il nome del batterio
     */

    @Test
    public void testToString() {
        Tontino tontino = new Tontino(5, 5, Color.BLUE, food);
        Assert.assertEquals("Tontino", tontino.toString());
    }
}