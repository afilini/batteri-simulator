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

package utils.videoactions;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;

public abstract class GenericAction {
    protected IContainer container;
    protected IStreamCoder coder;

    protected int duration;
    protected IRational framerate;

    protected int offset;
    protected long frameDuration;

    /**
     * Costruttore padre che assegna i parametri essenziali al funzionamento di un'azione:
     *
     * @param container IContainer dello stream video
     * @param coder     Coder da utilizzare per la codifica dei pacchetti
     * @param framerate Framrate del video da creare
     * @param duration  Durata totale dell'azione
     * @param offset    Offset dall'inizio dello stream (viene utilizzato per settare correttamente il timestamp dei pacchetti)
     */

    public GenericAction(IContainer container, IStreamCoder coder, IRational framerate, int duration, int offset) {
        this.container = container;
        this.coder = coder;

        this.framerate = framerate;
        this.duration = duration;
        this.offset = offset;

        this.frameDuration = (long) (1000 / framerate.getDouble());
    }

    /**
     * Costruttore padre che assegna i parametri essenziali al funzionamento di un'azione con offset a 0
     *
     * @param container IContainer dello stream video
     * @param coder     Coder da utilizzare per la codifica dei pacchetti
     * @param framerate Framrate del video da creare
     * @param duration  Durata totale dell'azione
     */

    public GenericAction(IContainer container, IStreamCoder coder, IRational framerate, int duration) {
        this(container, coder, framerate, duration, 0);
    }

    /**
     * Metodo da eseguire per lanciare l'azione video
     */
    abstract void run();

    public int getEndTime() {
        return offset + duration;
    }
}
