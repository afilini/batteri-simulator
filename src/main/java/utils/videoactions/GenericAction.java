package utils.videoactions;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of utils.videoactions
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, aprile 2016
 */

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
