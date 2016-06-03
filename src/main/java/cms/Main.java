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

import video.VideoStreamer;

/**
 * Classe che contiene il main da avviare e si occupa del parsing degli arguments passati da linea di comando.
 * Crea un nuovo {@link Simulator} e un nuovo {@link VideoStreamer} e avvia lo stream
 */

public class Main {
    public static void main(String args[]) {
        String rtmpUrl = args[0];
        String pushUrl = args[1];

        int len = Math.min(6, args.length - 2);

        String[] nomiBatteri = new String[len];
        System.arraycopy(args, 2, nomiBatteri, 0, len);

        Simulator simulator = new Simulator(nomiBatteri, pushUrl);
        VideoStreamer streamer = new VideoStreamer(simulator, rtmpUrl, 1024, 700, 8);
        streamer.start();
    }
}
