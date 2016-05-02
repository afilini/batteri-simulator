package utils.videoactions;

import cms.Simulator;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.image.BufferedImage;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of utils.videoactions
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, aprile 2016
 */

public class SimulatorAction extends GenericAction {
    long firstTimeStamp;
    long lastTimeStamp = -1;

    private final Simulator simulator;

    /**
     * Costruttore unico per creare azioni di simulazione ad un dato offset
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
     * Costruttore unico per creare azioni di simulazione con offset a 0
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

            coder.encodeVideo(packet, outFrame, 100000);
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
