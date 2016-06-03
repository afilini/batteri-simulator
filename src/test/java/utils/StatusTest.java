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

package utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Classe per verificare il funzionamento di {@link Status}
 */
public class StatusTest {

    /**
     * Metodo che testa il funzionamento di {@link Status#toString()} in caso il batterio si sia estinto
     */
    
    @Test
    public void testToStringDead() {
        Status status = new Status("name", true, 10);
        Assert.assertEquals("name true 10", status.toString());
    }

    /**
     * Metodo che testa il funzionamento di {@link Status#toString()} in caso il batterio sia sopravvissuto
     */

    @Test
    public void testToStringAlive() {
        Status status = new Status("name", false, 10);
        Assert.assertEquals("name false 10", status.toString());
    }

    /**
     * Metodo che verifica il corretto assegnamento del parametro "n" del costruttore {@link Status#Status(String, boolean, long)}
     */

    @Test
    public void testNumAssignment() {
        Status status = new Status("name", true, 10);
        Assert.assertEquals(0, status.getNum());

        status = new Status("name", false, 10);
        Assert.assertEquals(0, status.getTime());
    }
}