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

package video;

import cms.Simulator;
import com.xuggle.xuggler.*;
import utils.videoactions.SimulatorAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe che si occupa della gestione dello stream video.
 * <p>
 * Si tratta di un thread che viene lanciato per lasciare spazio al thread principale di compiere altre azioni,
 * ad esempio gestire una eventuale interfaccia grafica o semplicemente mostrare a schermo delle statistiche sullo
 * stream in corso.
 */

public class VideoStreamer extends Thread {
    private Simulator simulator;

    private String url;
    private final int height;
    private final int width;

    private final int FPS;

    public VideoStreamer(Simulator simulator, String url, int width, int height, int fps) {
        super("Streamer");
        this.simulator = simulator;
        this.url = url;

        this.width = width;
        this.height = height;
        this.FPS = fps;
    }

    /**
     * Metodo che viene lanciato dal thread
     */

    @Override
    public void run() {
        // Creo un container che verra' streammato
        IContainer container = IContainer.make();
        IContainerFormat containerFormat = IContainerFormat.make();

        /*
         * Lo creo di tipo flv perche' altri formati come l'mp4 non possono essere
         * trasmessi in diretta (e' necessario conoscere a priori il timestamp di ogni frame)
         */
        containerFormat.setOutputFormat("flv", url, null);
        container.setInputBufferLength(0);

        System.out.println("Opening output container " + url);
        int retVal = container.open(url, IContainer.Type.WRITE, containerFormat);
        if (retVal < 0) {
            System.err.println("Could not open output container for live stream");
            System.exit(1);
        }

        /*
         * Creo uno stream (per il video) e imposto come codec H.264
         */
        IStream stream = container.addNewStream(ICodec.ID.CODEC_ID_H264);
        IStreamCoder coder = stream.getStreamCoder();
        ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
        coder.setNumPicturesInGroupOfPictures(3);
        coder.setCodec(codec);
        coder.setBitRate(1000000);

        /*
         * Imposto le dimensioni del video e la rappresentazione dei pixel
         */
        coder.setPixelType(IPixelFormat.Type.YUV420P);
        coder.setHeight(height);
        coder.setWidth(width);

        System.out.println("[ENCODER] video size is " + width + "x" + height);
        coder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
        IRational frameRate = IRational.make(FPS, 1);
        coder.setFrameRate(frameRate);
        coder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
        Properties props = new Properties();

        /*
         * Carico il profilo per la codifica del video in ffmpeg
         */
        InputStream is = VideoStreamer.class.getResourceAsStream("/libx264-normal.ffpreset");
        try {
            props.load(is);
        } catch (IOException e) {
            System.err.println("You need the libx264-normal.ffpreset file from the in your classpath.");
            System.exit(1);
        }
        Configuration.configure(props, coder);

        coder.open();
        container.writeHeader();

        try {
            /*
             * Creo un'azione di simulazione passandogli il simulatore della durata di 5 minuti
             */
            SimulatorAction simulation = new SimulatorAction(container, coder, frameRate, 5 * 60, simulator);
            simulation.run();
        } catch (Exception e) {
            e.printStackTrace();
            container.writeTrailer();
            System.exit(1);
        }

        /*
         * Chiudo lo stream
         */
        container.writeTrailer();
        System.exit(0);
    }
}