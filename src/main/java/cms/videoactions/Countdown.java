package cms.videoactions;

import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of cms.videoactions
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, marzo 2016
 */

public class Countdown {
    private IContainer container;
    private IStreamCoder coder;

    private int duration;
    private IRational framerate;

    private int offset;
    private long frameDuration;

    public Countdown(IContainer container, IStreamCoder coder, IRational framerate, int duration, int offset) {
        this.container = container;
        this.coder = coder;

        this.framerate = framerate;
        this.duration = duration;
        this.offset = offset;

        this.frameDuration = (long) (1000 / framerate.getDouble());
    }

    public Countdown(IContainer container, IStreamCoder coder, IRational framerate, int duration) {
        this(container, coder, framerate, duration, 0);
    }

    public void run() {
        int frameCount = 0;

        for (; frameCount < framerate.getDouble() * duration; frameCount++) {
            BufferedImage image = new BufferedImage(1024, 700, BufferedImage.TYPE_3BYTE_BGR);

            Graphics g = image.getGraphics();

            g.setColor(Color.yellow);
            g.drawRect(0, 0, 1024, 700);

            g.setColor(Color.blue);
            long durationMSecs = duration * 1000 - frameCount * frameDuration;

            g.setFont(new Font("Consolas", Font.BOLD, 72));
            String duration = String.format("%02d:%02d.%03d", ((durationMSecs / 1000) % 3600) / 60, ((durationMSecs / 1000) % 60), durationMSecs % 1000);
            FontMetrics fm = g.getFontMetrics();

            g.drawString(duration, (1024 - fm.stringWidth("duration")) / 2, (700 - fm.getHeight()) / 2);

            IPacket packet = IPacket.make();
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

            long timeStamp = (long) ((offset + frameCount / framerate.getDouble()) * 1000 * 1000);

            IVideoPicture outFrame = converter.toPicture(image, timeStamp);
            outFrame.setQuality(0);

            if (frameCount % 8 == 0)
                outFrame.setKeyFrame(true);

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
        container.flushPackets();
    }
}

