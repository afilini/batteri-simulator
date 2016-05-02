package cms;

import video.VideoStreamer;

/**
 * Copyright (C) Alekos Filini (afilini) - All Rights Reserved
 * <p/>
 * This file is part of cms
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alekos Filini <alekos.filini@gmail.com>, marzo 2016
 */

public class MainNoGUI {
    public static void main (String args[]) {
        String rtmpUrl = args[0];
        String pushUrl = args[1];

        String[] nomiBatteri = new String[args.length - 2];
        System.arraycopy(args, 2, nomiBatteri, 0, args.length - 2);

        Simulator simulator = new Simulator(nomiBatteri, pushUrl);
        VideoStreamer streamer = new VideoStreamer(simulator, rtmpUrl, 1024, 700, 8);
        streamer.start();
    }
}
