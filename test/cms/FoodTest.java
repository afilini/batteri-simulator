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

package cms;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Classe che verifica il funzionamento degli oggetti di tipo {@link Food}
 */

public class FoodTest {

    /**
     * Metodo che controlla che il campo sia vuoto alla sua generazione
     */

    @Test
    public void testEmptyAtGeneration() {
        Food f = new Food(50, 50);
        boolean foundFood = false;
        for (int i = 0; i < f.getWidth(); i++)
            for (int j = 0; j < f.getHeight(); j++)
                if (f.isFood(i, j))
                    foundFood = true;

        Assert.assertFalse(foundFood);
    }

    /**
     * Metodo che controlla la distribuzione su un campo grande quanto il quadrato da riempire
     */

    @Test
    public void testSquareDistributionUpperLimit() {
        Food f = new Food(50, 50);
        f.squareDistribution(50, 50 * 50);

        boolean foundFood = false;
        for (int i = 0; i < f.getWidth(); i++)
            for (int j = 0; j < f.getHeight(); j++)
                if (f.isFood(i, j))
                    foundFood = true;

        Assert.assertTrue(foundFood);
    }

    /**
     * Metodo che controlla la distribuzione con quadrato da riempire al minimo
     */

    @Test
    public void testSquareDistributionLowerLimit() {
        Food f = new Food(50, 50);
        f.squareDistribution(2, 50 * 10);

        boolean foundFood = false;
        for (int i = 0; i < f.getWidth(); i++)
            for (int j = 0; j < f.getHeight(); j++)
                if (f.isFood(i, j))
                    foundFood = true;

        Assert.assertTrue(foundFood);
    }

    /**
     * Metodo che verifica il cibo venga veramente mangiato
     */

    @Test
    public void testEatFood() {
        Food f = new Food(2, 2);
        f.squareDistribution(2, 4);

        for (int i = 0; i < f.getWidth(); i++)
            for (int j = 0; j < f.getHeight(); j++)
                if (f.isFood(i, j)) {
                    f.eatFood(i, j);
                    Assert.assertFalse(f.isFood(i, j));
                }
    }

    /**
     * Metodo che verifica la corretta gestione della larghezza
     */

    @Test
    public void testGetWidth() {
        Food f = new Food(42, 42);
        Assert.assertEquals(42, f.getWidth());
    }

    /**
     * Metodo che verifica la corretta gestione dell'altezza
     */

    @Test
    public void testGetHeight() {
        Food f = new Food(42, 42);
        Assert.assertEquals(42, f.getHeight());
    }
}