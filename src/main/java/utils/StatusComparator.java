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

import java.util.Comparator;

/**
 * Comparatore usato per ordinare gli stati finali dei batteri. Viene data precedenza ai
 * batteri rimasti in vita in maggior numero, in caso di morte vengono privilegiati
 * i batteri che sono sopravvissuti piu a lungo
 */

public final class StatusComparator implements Comparator<Status> {
    @Override
    public int compare(Status o1, Status o2) {
        if (o1.isDead() && o2.isDead())
            return (int) (o2.getTime() - o1.getTime());

        if (o1.isDead())
            return 1;

        if (o2.isDead())
            return -1;

        return (int) (o2.getNum() - o1.getNum());
    }
}
