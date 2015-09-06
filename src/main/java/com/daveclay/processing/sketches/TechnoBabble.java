package com.daveclay.processing.sketches;


import com.daveclay.processing.api.CanvasAware;
import com.daveclay.processing.api.NoiseColor;
import com.daveclay.processing.api.SketchRunner;
import com.daveclay.processing.api.image.ImageFrame;
import com.daveclay.processing.api.image.ImgProc;
import org.apache.commons.lang3.StringUtils;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class TechnoBabble extends PApplet {


    public static void main(String[] args) {
        SketchRunner.run(new TechnoBabble());
    }

    PApplet ref = this;
    ImgProc imgProc;
    Info[] infos;
    ImageLib artLib;
    BlurGeneticGraphic blurGeneticGraphic;
    GeneticID geneticID;
    GlitchImages glitchImages;
    ImageFrame screenOverlay;
    ImageFrame imageFrame;
    PFont orator23;
    PFont orator9;

    public void setup() {
        size(800, 800, P2D);
        orator9 = createFont("OratorStd", 9);
        orator23 = createFont("OratorStd", 23);

        infos = new Info[] {
                info(10, 60),
                info(10, 300),
                info(10, 660),
        };
        artLib = ImageLib.art(this);
        geneticID = new GeneticID();
        blurGeneticGraphic = new BlurGeneticGraphic(this);
        screenOverlay = new ImageFrame(this, loadImageByName(this, "screen-lines.png"), 0, 0);
        glitchImages = new GlitchImages(this, ImageLib.glitches(this));
        /*
        String[] fontList = PFont.list();
        for (String f : fontList) {
            System.out.println(f);
        }
        */
        imageFrame = new ImageFrame(this, loadImageByName(this, artLib.files[3]), 10, 10);
    }

    public void draw() {
        background(0);
        blendMode(SCREEN);
        blurGeneticGraphic.draw();
        geneticID.draw();
        for (Info info : infos) {
            info.draw();
        }
        screenOverlay.draw();
        glitchImages.draw();
    }

    class GeneticID {

        String s;

        void draw() {
            if (s == null || random(1) > .78f) {
                next();
            }

            pushStyle();
            textFont(orator23);
            text(s, 10, 50);
            popStyle();
        }

        void next() {
            s = random(1) > .15f ? "AUX" : "ERR";
            s += StringUtils.rightPad(Integer.toHexString(64 + (int) random(670)).toUpperCase(), 4, "X") + ".";
            s += random(1) > .9f ? "GEN" : "RNA";
        }
    }

    static class GlitchImages extends CanvasAware {
        private final ImageLib imageLib;
        private ImageFrame imageFrame;

        public GlitchImages(PApplet canvas, ImageLib imageLib) {
            super(canvas);
            this.imageLib = imageLib;
            imageLib.loadImages();
            next();
        }

        void draw() {
            if (canvas.random(1) > .6f) {
                next();
            }
            if (canvas.random(1) > .8f) {
                imageFrame.draw();
            }
        }

        void next() {
            PImage image = imageLib.pickRandomImage();
            float ratio = 1f;
            if (image.width > canvas.width) {
                ratio = (float) canvas.width / (float) image.width;
            } else if (image.height > canvas.height) {
                ratio = (float) canvas.height / (float) image.height;
            }

            if (ratio != 1f) {
                image.resize((int) (image.width * ratio), (int)(image.height * ratio));
            }
            imageFrame = new ImageFrame(canvas, image, 0, 0);
        }
    }

    static class BlurGeneticGraphic extends CanvasAware {
        private final ImageLib imageLib;
        private ImageFrame imageFrame;

        public BlurGeneticGraphic(PApplet canvas) {
            super(canvas);
            imageLib = ImageLib.genetics(canvas);
            imageLib.loadImages();
            next();
        }

        void draw() {
            if (imageFrame.blur().allBlack) {
                next();
            }
            imageFrame.draw();
        }

        void next() {
            PImage image = imageLib.pickRandomImage();
            float ratio = 1f;
            if (image.width > canvas.width) {
                ratio = (float) canvas.width / (float) image.width;
            } else if (image.height > canvas.height) {
                ratio = (float) canvas.height / (float) image.height;
            }

            if (ratio != 1f) {
                image.resize((int) (image.width * ratio), (int)(image.height * ratio));
            }
            imageFrame = new ImageFrame(canvas, image, 0, 0);
        }
    }

    static class GeneticSeries extends CanvasAware {
        GeneticBand[] geneticBands = new GeneticBand[50];

        public GeneticSeries(PApplet canvas) {
            super(canvas);
            int prevX = 0;
            for (int i = 0; i < geneticBands.length; i++) {
                geneticBands[i] = new GeneticBand(canvas, prevX, 175);
                prevX += geneticBands[i].width + 2;
            }
        }

        void draw() {
            for (GeneticBand geneticBand : geneticBands) {
                geneticBand.draw();
            }
        }
    }

    static class GeneticBand extends CanvasAware {
        final PApplet canvas;
        NoiseColor noiseColor;
        int width;
        int height = 40;
        int x = 0;
        int y = 0;

        public GeneticBand(PApplet canvas, int x, int y) {
            super(canvas);
            this.canvas = canvas;
            this.x = x;
            this.y = y;
            noiseColor = new NoiseColor(canvas, .01f);
            this.width = (int) canvas.random(12);
        }

        void draw() {
            pushStyle();
            noStroke();
            fill(noiseColor.nextColor(255));
            roundrect(x, y, width, height, 10);
            popStyle();
        }
    }

    Info info(int x, int y) {
        return new Info(x, y);
    }

    class Info {
        String[] tech = new String[] {
                "lo0: flags=8049<UP,LOOPBACK,RUNNING,MULTICAST> mtu 16384\n" +
                        "\toptions=3<RXCSUM,TXCSUM>\n" +
                        "\tinet6 ::1 prefixlen 128 \n" +
                        "\tinet 127.0.0.1 netmask 0xff000000 \n" +
                        "\tinet6 fe80::1%lo0 prefixlen 64 scopeid 0x1 \n" +
                        "\tnd6 options=1<PERFORMNUD>\n",

                "gif0: flags=8010<POINTOPOINT,MULTICAST> mtu 1280\n" +
                        "stf0: flags=0<> mtu 1280\n" +
                        "en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500\n" +
                        "\tether 6c:40:08:b0:58:78 \n" +
                        "\tinet6 fe80::6e40:8ff:feb0:5878%en0 prefixlen 64 scopeid 0x4 \n" +
                        "\tinet 192.168.1.131 netmask 0xffffff00 broadcast 192.168.1.255\n" +
                        "\tnd6 options=1<PERFORMNUD>\n",

                "\tmedia: autoselect\n" +
                        "\tstatus: active\n" +
                        "en1: flags=8963<UP,BROADCAST,SMART,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1500\n" +
                        "\toptions=60<TSO4,TSO6>\n" +
                        "\tether 72:00:07:30:4c:80 \n" +
                        "\tmedia: autoselect <full-duplex>\n" +
                        "\tstatus: inactive\n",

                "en2: flags=8963<UP,BROADCAST,SMART,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1500\n" +
                        "\toptions=60<TSO4,TSO6>\n" +
                        "\tether 72:00:07:30:4c:81 \n" +
                        "\tmedia: autoselect <full-duplex>\n" +
                        "\tstatus: inactive\n",

                "p2p0: flags=8843<UP,BROADCAST,RUNNING,SIMPLEX,MULTICAST> mtu 2304\n" +
                        "\tether 0e:40:08:b0:58:78 \n" +
                        "\tmedia: autoselect\n" +
                        "\tstatus: inactive\n",

                "awdl0: flags=8943<UP,BROADCAST,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1452\n" +
                        "\tether 06:1d:9f:9d:b8:71 \n" +
                        "\tinet6 fe80::41d:9fff:fe9d:b871%awdl0 prefixlen 64 scopeid 0x8 \n" +
                        "\tnd6 options=1<PERFORMNUD>\n" +
                        "\tmedia: autoselect\n" +
                        "\tstatus: active\n",

                "bridge0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500\n" +
                        "\toptions=63<RXCSUM,TXCSUM,TSO4,TSO6>\n" +
                        "\tether 6e:40:08:0b:fd:00 \n",

                "\tConfiguration:\n" +
                        "\t\tid 0:0:0:0:0:0 priority 0 helotime 0 fwddelay 0\n" +
                        "\t\tmaxage 0 holdcnt 0 proto stp maxaddr 100 timeout 1200\n" +
                        "\t\troot id 0:0:0:0:0:0 priority 0 ifcost 0 port 0\n" +
                        "\t\tipfilter disabled flags 0x2\n",

                "\trevolt: en1 flags=3<LEARNING,DISCOVER>\n" +
                        "\t        ifmaxaddr 0 port 5 priority 0 path cost 0\n" +
                        "\trevolt: en2 flags=3<LEARNING,DISCOVER>\n" +
                        "\t        ifmaxaddr 0 port 6 priority 0 path cost 0\n" +
                        "\tnd6 options=1<PERFORMNUD>\n" +
                        "\tmedia: <unknown type>\n" +
                        "\tstatus: inactive",

                "machdep.xcpm.forced_idle_ratio: 100\n" +
                        "machdep.xcpm.forced_idle_period: 30000000\n" +
                        "machdep.xcpm.deep_idle_log: 0\n" +
                        "machdep.xcpm.qos_txfr: 1\n" +
                        "machdep.xcpm.deep_idle_count: 0\n" +
                        "machdep.xcpm.deep_idle_last_stats: n/a\n" +
                        "machdep.xcpm.deep_idle_total_stats: n/a\n" +
                        "machdep.xcpm.cpu_thermal_level: 0\n" +
                        "machdep.xcpm.gpu_thermal_level: 0\n" +
                        "machdep.xcpm.io_thermal_level: 0\n" +
                        "machdep.xcpm.io_control_engages: 0\n" +
                        "machdep.xcpm.io_control_disengages: 4\n" +
                        "machdep.xcpm.io_filtered_reads: 0\n" +
                        "machdep.eager_timer_evaluations: 45"
        };

        String s;
        int position;
        int x;
        int y;

        public Info(int x, int y) {
            this.x = x;
            this.y = y;
            next();
        }

        void draw() {
            if (random(1) > .5f) {
                if (position < s.length()) {
                    position++;
                } else {
                    next();
                }
            }
            pushStyle();
            textFont(orator9);
            text(s.substring(0, position), x, y);
            popStyle();
        }

        private void next() {
            s = tech[(int) random(tech.length)];
            position = 0;
        }
    }

    public static class ImageLib {

        private PImage[] images;
        private String[] files;
        private PApplet canvas;

        public ImageLib(PApplet canvas, String[] files) {
            this.files = files;
            this.canvas = canvas;
            this.images = new PImage[files.length];
        }

        PImage[] pickRandomImages() {
            int num = (int) canvas.random(5);
            PImage[] picked = new PImage[num];
            for (int i = 0; i < num; i++) {
                picked[i] = pickRandomImage();
            }
            return picked;
        }

        PImage pickRandomImage() {
            int index = (int) canvas.random(images.length);
            System.out.println(files[(index)]);
            return images[index];
        }


        ImageLib loadImages() {
            for (int i = 0; i < files.length; i++) {
                images[i] = loadImageByName(canvas, files[i]);
                images[i].loadPixels();
            }
            return this;
        }

        public static ImageLib geneticsOverlay(PApplet canvas) {
            return new ImageLib(canvas, new String[] {
                    "screen-lines.png"
            });
        }

        public static ImageLib genetics(PApplet canvas) {
            return new ImageLib(canvas, new String[] {
                    "genetics-circle-1.png",
                    "genetics-circle-2.png",
                    "genetics-circle-3.png",
                    "genetics-circle-4.png",
                    "genetics-circle-5.png",
                    "genetics-circle-6.png",
                    "genetics-circle-7.png",
                    "genetics-chart.png",
                    "genetics-column-glow.png"
            });
        }

        public static ImageLib art(PApplet canvas) {
            return new ImageLib(canvas, new String[] {
                    "2aF.png",
                    "call III.png",
                    "dup rejesus process.png",
                    "identify.png",
                    "insect I.png",
                    "insect V red.png",
                    "light, movement II.png",
                    "medic.png",
                    "messianic.png",
                    "pump six.png",
                    "untitled body I.png",
                    "untitled body IV.png",
                    "untitled connection II.png",
                    "untitled connection III.png",
                    "untitled connection V.png",
                    "untitled figure III.png",
                    "untitled figure IV.png",
                    "untitled form I.png",
                    "untitled form III.png",
                    "untitled machine I.png",
                    "untitled machine II.png",
                    "untitled machine III.png",
                    "untitled motion I.png",
                    "untitled motion II.png",
                    "untitled recline III.png",
                    "untitled texture IV.png",
                    "within.png" });
        }

        public static ImageLib glitches(PApplet canvas) {
            return new ImageLib(canvas, new String[] {
                    "genetics-circle-glitch-1.png",
                    "genetics-circle-glitch-2.png",
                    "genetics-circle-glitch-3.png",
                    "genetics-circle-glitch-4.png",
                    "genetics-circle-glitch-5.png"
            });
        }
    }

        public static PImage loadImageByName(PApplet canvas, String name) {
            return canvas.loadImage("/Users/daveclay/work/rebel belly after video/" + name);
        }
}
