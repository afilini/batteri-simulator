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

/**
 * Classe che rappresenta lo stato di un batterio alla fine della partita
 */

public class Status {
    /**
     * Boolean che rappresenta se la famiglia di batteri si sia estinta o meno durante la simulazione
     */
    protected boolean dead;
    /**
     * Ora dell'estinzione (se si e' verificata)
     */
    protected long time;
    /**
     * Numero di batteri rimasti in caso non si fosse estinto
     */
    protected long num;
    /**
     * Nickname dell'account BCMS
     */
    protected String name;

    public Status(String name, boolean dead, long n) {
        this.name = name;
        this.dead = dead;
        if (dead)
            this.time = n;
        else
            this.num = n;
    }

    @Override
    public String toString() {
        return String.format("%s %b %d", name, dead, dead ? time : num);
    }

    public boolean isDead() {
        return dead;
    }

    public long getTime() {
        return time;
    }

    public long getNum() {
        return num;
    }

    public String getName() {
        return name;
    }
}
