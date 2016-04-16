package cms;

import cms.videoactions.Countdown;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VideoStreamer extends Thread {

    private MainNoGUI main;

    //private static String url = "rtmp://live.vng.hitbox.tv/push/afilini?key=Ok8KMuPA";
    private static String url = "rtmp://localhost/mytv/test";
    private static int height = 700;
    private static int width = 1024;

    private final static int FPS = 8;

    public VideoStreamer(MainNoGUI main) {
        super("Streamer");
        this.main = main;
    }

    @Override
    public void run() {
        IContainer container = IContainer.make();
        IContainerFormat containerFormat_live = IContainerFormat.make();

        containerFormat_live.setOutputFormat("flv", url, null);
        container.setInputBufferLength(0);

        int retVal = container.open(url, IContainer.Type.WRITE, containerFormat_live);
        if (retVal < 0) {
            System.err.println("Could not open output container for live stream");
            System.exit(1);
        }
        IStream stream = container.addNewStream(0);
        IStreamCoder coder = stream.getStreamCoder();
        ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
        coder.setNumPicturesInGroupOfPictures(3);
        coder.setCodec(codec);
        coder.setBitRate(4000000);
        coder.setPixelType(IPixelFormat.Type.YUV420P);
        coder.setHeight(height);
        coder.setWidth(width);

        System.out.println("[ENCODER] video size is " + width + "x" + height);
        coder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
        coder.setGlobalQuality(5);
        IRational frameRate = IRational.make(FPS, 1);
        coder.setFrameRate(frameRate);
        coder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
        Properties props = new Properties();
        InputStream is = VideoStreamer.class.getResourceAsStream("/libx264-normal.ffpreset");
        try {
            props.load(is);
        } catch (IOException e) {
            System.err.println("You need the libx264-normal.ffpreset file from the Xuggle distribution in your classpath.");
            System.exit(1);
        }
        Configuration.configure(props, coder);

        coder.open();

        container.writeHeader();

        long firstTimeStamp = System.currentTimeMillis();
        long lastTimeStamp = -1;
        int i;


        try {
            Countdown countdown = new Countdown(container, coder, frameRate, 10);
            countdown.run();

            main.setStartTime();
            i = 0;

            while (System.currentTimeMillis() - firstTimeStamp < ((60 * 5) + 20) * 1000) {
                //long iterationStartTime = System.currentTimeMillis();
                long now = System.currentTimeMillis();
                BufferedImage image = main.paintImage();
                IPacket packet = IPacket.make();
                IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                long timeStamp = (now - firstTimeStamp) * 1000;
                IVideoPicture outFrame = converter.toPicture(image, timeStamp);
                outFrame.setQuality(3);

                if (i % 8 == 0) {
                    outFrame.setKeyFrame(true);
                }

                coder.encodeVideo(packet, outFrame, 0);
                outFrame.delete();

                if (packet.isComplete()) {
                    container.writePacket(packet);
                    System.out.println("[ENCODER] writing packet of size " + packet.getSize() + " for elapsed time " + ((timeStamp - lastTimeStamp) / 1000));
                    lastTimeStamp = timeStamp;
                }

                try {
                    Thread.sleep(Math.max((long) (1000 / frameRate.getDouble()) - (System.currentTimeMillis() - now), 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        container.writeTrailer();
    }
}