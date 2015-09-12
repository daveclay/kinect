package com.daveclay.processing.sketches;

import com.daveclay.processing.api.Drawing;
import processing.core.PApplet;
import processing.core.PFont;

public class TerminalText extends Drawing {

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
    PFont font;
    int position;
    int x;
    int y;

    public TerminalText(PApplet canvas,
                        PFont font,
                        int x,
                        int y) {
        super(canvas);
        this.font = font;
        this.x = x;
        this.y = y;
        next();
    }

    public void draw() {
        if (random(1) > .5f) {
            if (position < s.length()) {
                position++;
            } else {
                next();
            }
        }
        pushStyle();
        textFont(font);
        text(s.substring(0, position), x, y);
        popStyle();
    }

    private void next() {
        s = tech[(int) random(tech.length)];
        position = 0;
    }
}
