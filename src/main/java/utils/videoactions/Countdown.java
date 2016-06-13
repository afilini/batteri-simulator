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

import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Countdown extends GenericAction {


    /**
     * Costruttore per creare countdown ad un dato offset
     *
     * @param container IContainer dello stream video
     * @param coder     Coder da utilizzare per la codifica dei pacchetti
     * @param framerate Framrate del video da creare
     * @param duration  Durata totale dell'azione
     * @param offset    Offset dall'inizio dello stream (viene utilizzato per settare correttamente il timestamp dei pacchetti)
     */

    public Countdown(IContainer container, IStreamCoder coder, IRational framerate, int duration, int offset) {
        super(container, coder, framerate, duration, offset);
    }

    /**
     * Costruttore per creare countdown all'inizio dello stream
     *
     * @param container IContainer dello stream video
     * @param coder     Coder da utilizzare per la codifica dei pacchetti
     * @param framerate Framrate del video da creare
     * @param duration  Durata totale dell'azione
     */

    public Countdown(IContainer container, IStreamCoder coder, IRational framerate, int duration) {
        super(container, coder, framerate, duration);
    }

    /**
     * Metodo che genera il countdown e lo aggiunge allo stream
     */

    @Override
    public void run() {
        int frameCount = 0;

        for (; frameCount < framerate.getDouble() * duration; frameCount++) {
            // Creo un'immagine vuota e aggiungo lo sfondo
            BufferedImage image = new BufferedImage(1024, 700, BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = image.getGraphics();
            g.drawRect(0, 0, 1024, 700);

            g.setColor(Color.blue);
            long durationMSecs = duration * 1000 - frameCount * frameDuration;

            // Creo la stringa della durata e la stampo al centro dello schermo
            g.setFont(new Font("Consolas", Font.BOLD, 72));
            String duration = String.format("%02d:%02d.%03d", ((durationMSecs / 1000) % 3600) / 60, ((durationMSecs / 1000) % 60), durationMSecs % 1000);
            FontMetrics fm = g.getFontMetrics();

            g.drawString(duration, (1024 - fm.stringWidth("duration")) / 2, (700 - fm.getHeight()) / 2);

            IPacket packet = IPacket.make();
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

            // Calcolo il timestamp del frame e lo aggiungo al container
            long timeStamp = (long) ((offset + frameCount / framerate.getDouble()) * 1000 * 1000);

            IVideoPicture outFrame = converter.toPicture(image, timeStamp);
            outFrame.setQuality(0);

            coder.encodeVideo(packet, outFrame, 0);
            outFrame.delete();

            if (packet.isComplete()) {
                container.writePacket(packet);
                System.out.println("[ENCODER] [COUNTDOWN] writing packet of size " + packet.getSize() + " for elapsed time " + frameDuration);
            }

            try {
                Thread.sleep((long) (1 / framerate.getDouble() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[ENCODER] [COUNTDOWN] scritti " + frameCount + " frames");

        // Flusho il buffer dei pacchetti dello stream
        container.flushPackets();
    }
}

