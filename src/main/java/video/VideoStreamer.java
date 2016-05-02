package video;

import cms.Simulator;
import com.xuggle.xuggler.*;
import utils.videoactions.SimulatorAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

    @Override
    public void run() {
        IContainer container = IContainer.make();
        IContainerFormat containerFormat = IContainerFormat.make();

        containerFormat.setOutputFormat("flv", url, null);
        container.setInputBufferLength(0);

        System.out.println("Opening output container " + url);
        int retVal = container.open(url, IContainer.Type.WRITE, containerFormat);
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
        InputStream is = VideoStreamer.class.getResourceAsStream("/libx264-hq.ffpreset");
        try {
            props.load(is);
        } catch (IOException e) {
            System.err.println("You need the libx264-hq.ffpreset file from the in your classpath.");
            System.exit(1);
        }
        Configuration.configure(props, coder);

        coder.open();
        container.writeHeader();

        try {
            SimulatorAction simulation = new SimulatorAction(container, coder, frameRate, 5 * 60, simulator);
            simulation.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        container.writeTrailer();
    }
}