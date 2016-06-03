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

import cms.Simulator;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.image.BufferedImage;

public class SimulatorAction extends GenericAction {
    long firstTimeStamp;
    long lastTimeStamp = -1;

    private final Simulator simulator;

    /**
     * Costruttore per creare azioni di simulazione ad un dato offset
     *
     * @param container IContainer dello stream video
     * @param coder     Coder da utilizzare per la codifica dei pacchetti
     * @param framerate Framrate del video da creare
     * @param duration  Durata totale dell'azione
     * @param offset    Offset dall'inizio dello stream (viene utilizzato per settare correttamente il timestamp dei pacchetti)
     * @param simulator Simulatore da portare nello stream
     */

    public SimulatorAction(IContainer container, IStreamCoder coder, IRational framerate, int duration, int offset, Simulator simulator) {
        super(container, coder, framerate, duration, offset);
        this.simulator = simulator;
    }

    /**
     * Costruttore per creare azioni di simulazione con offset a 0
     *
     * @param container IContainer dello stream video
     * @param coder     Coder da utilizzare per la codifica dei pacchetti
     * @param framerate Framrate del video da creare
     * @param duration  Durata totale dell'azione
     * @param simulator Simulatore da portare nello stream
     */

    public SimulatorAction(IContainer container, IStreamCoder coder, IRational framerate, int duration, Simulator simulator) {
        this(container, coder, framerate, duration, 0, simulator);
    }

    @Override
    public void run() {
        simulator.setupSimulation();

        firstTimeStamp = System.currentTimeMillis();
        lastTimeStamp = offset;

        while (System.currentTimeMillis() - firstTimeStamp < duration * 1000) {
            long now = System.currentTimeMillis();
            BufferedImage image = simulator.paintImage();
            IPacket packet = IPacket.make();
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
            long timeStamp = (now - firstTimeStamp) * 1000 + offset;
            IVideoPicture outFrame = converter.toPicture(image, timeStamp);

            coder.encodeVideo(packet, outFrame, 1000000);
            outFrame.delete();

            if (packet.isComplete()) {
                container.writePacket(packet);
                System.out.println("[ENCODER] writing packet of size " + packet.getSize() + " for elapsed time " + ((timeStamp - lastTimeStamp) / 1000));
                lastTimeStamp = timeStamp;
            }

            try {
                Thread.sleep(Math.max((long) (1000 / framerate.getDouble()) - (System.currentTimeMillis() - now), 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        simulator.endSimulation();
    }
}
