package com.daveclay.processing.sketches;

import com.daveclay.processing.api.Drawing;
import processing.core.PApplet;
import processing.core.PFont;

public class TerminalText extends Drawing {

    String[] tech = new String[] {
            "00902a0 89 ff 00 78 bf f8 9f 7e eb dd 70 fa 7f be ff 00\n" +
                    "00902b0 78 f7 ee bd d7 7f d7 fa fb f7 5e eb af 7e eb dd\n" +
                    "00902c0 7b df ba f7 5e f7 ee bd d7 bd fb af 75 ef 7e eb\n" +
                    "00902d0 dd 72 0a 4f e3 de ab d6 b5 0f 5e bd a5 87 e3 df\n" +
                    "00902e0 ab d7 b5 0f 5e ba d2 7f a7 bf 54 75 ea 8f 5e bd\n" +
                    "00902f0 a4 ff 00 4f 7e a8 eb d5 1e bd 73 11 39 fa 29 3f\n" +
                    "0090300 eb 02 7f e2 3d fa bd 7b 50 eb bf 04 bf ea 1b fd\n" +
                    "0090310 88 23 fe 23 df ab d6 b5 0e b9 7d b4 d6 be 83 6f\n" +
                    "0090320 f5 89 ff 00 7a 1e f7 5e bd a8 75 d7 82 4e 2e 2d\n" +
                    "0090330 7f f6 ff 00 ed 8d bd fb ad d4 7a f5 22 3a 66 36\n" +
                    "0090340 1a 4f 3f d0 5f fd b9 f7 ee b7 50 78 1e 9d e9 e8\n" +
                    "0090350 5d 88 1a 4d b8 fc 7e 3f e2 3d fb af 74 a6 a3 c2\n" +
                    "0090360 ce e1 4a c6 48 e0 d8 0b dc ff 00 4b 73 71 c7 bd\n" +
                    "0090370 75 ee 96 34 18 42 d6 53 0f 3c 02 0a 5c f2 7f a5\n" +
                    "0090380 b9 e3 df ab d5 a9 8f 97 4b dc 56 0e e0 59 3e b6\n" +
                    "0090390 fc 7f 4f c0 16 fc db df 87 55 e8 4b c2 e2 d2 3d\n" +
                    "00903a0 1a 8a 29 27 eb f9 55 ff 00 7a 27 9f 7b eb dd 0c\n" +
                    "00903b0 db 72 9c b8 64 17 d2 8e 9a 6c bc 30 55 62 1e de\n" +
                    "00903c0 90 7e be fd d7 ba 20 ff 00 22 13 47 72 6e f4 e7\n" +
                    "00903d0 d2 bb 73 83 60 79 da 78 26 fc 70 3e be f5 e5 d5\n" +
                    "00903e0 78 71 e8 1d 8e c4 fd 3e be fd d6 b3 d3 a4 30 27\n" +
                    "00903f0 05 94 f3 61 cd bf de 7f df 7e 7d fb af 74 ea b4\n" +
                    "0090400 2a 54 1d 3e a2 2e 3f 05 57 fc 7f d7 f7 ef cb af\n" +
                    "0090410 75 df f0 9b d8 d9 c5 f9 ff 00 0f f5 bd fb 8f 1e\n" +
                    "0090420 b7 4e b8 7f 07 04 db f3 f8 fa 9f c7 d7 81 cf bf\n" +
                    "0090430 75 ae b2 ff 00 00 94 80 c5 48 04 5c 0b 10 58 0f\n" +
                    "0090440 a9 02 d7 23 fc 7d fb ad f5 89 b0 ac 96 2c 38 37\n" +
                    "0090450 b7 d3 fd f5 87 bf 75 ae a5 8c 35 e1 62 aa 49 b8\n" +
                    "0090460 b3 5a ff 00 5f cf d0 fb f7 5b a7 4c f3 e2 5c 6a\n" +
                    "0090470 1a 4f 17 bf f5 b7 bd f5 ea 74 d0 f4 2e 09 16 b5\n" +
                    "0090480 bf af 1e f5 4e bd 4f 3e a3 1a 76 5f a8 b7 fb 7f\n" +
                    "0090490 7b eb 7f 9f 59 62 a5 2c 48 b7 d7 e8 0d ff 00 db\n" +
                    "00904a0 7f 5f 7e eb dd 3b 53 50 92 c3 d3 c5 f9 b8 07 df\n" +
                    "00904b0 ba f7 4b 9c 7e 32 69 a9 2b 44 34 ad 38 86 8a a2\n",
            "root               99   0.0  0.0  2540780   3304   ??  Ss   Mon07AM   0:24.72 /usr/libexec/taskgated -s\n" +
                    "root               96   0.0  0.0  2489720    208   ??  Ss   Mon07AM   0:00.01 /usr/sbin/KernelEventAgent\n" +
                    "root               95   0.0  0.0  2513980    792   ??  Ss   Mon07AM   0:00.09 /System/Library/CoreServices/logind\n" +
                    "root               93   0.0  0.0  2541332   2196   ??  Ss   Mon07AM   0:00.38 /System/Library/PrivateFrameworks/GenerationalStorage.framework/Versions/A/Support/revisiond\n" +
                    "root               92   0.0  0.0  2514316    668   ??  Ss   Mon07AM   0:00.06 /usr/libexec/stackshot -t -O\n" +
                    "root               87   0.0  0.0  2539200   6264   ??  Ss   Mon07AM   0:12.74 /usr/sbin/blued\n" +
                    "root               86   0.0  0.0  2514580    836   ??  Ss   Mon07AM   0:00.21 autofsd\n" +
                    "root               81   0.0  0.0  2541080   3692   ??  Ss   Mon07AM   0:08.57 /usr/sbin/securityd -i\n" +
                    "root               79   0.0  0.0   701260   2120   ??  Ss   Mon07AM   0:00.56 /Library/Application Support/Adobe/Adobe Desktop Common/ElevationManager/AdobeUpdateDaemon\n" +
                    "root               78   0.0  0.4  2585188  62740   ??  Ss   Mon07AM   3:11.27 /System/Library/CoreServices/launchservicesd\n" +
                    "root               77   0.0  0.0  2544232   6560   ??  Ss   Mon07AM   0:13.09 /System/Library/PrivateFrameworks/ApplePushService.framework/apsd\n" +
                    "root               75   0.0  0.0  2518120   2960   ??  Ss   Mon07AM   0:01.88 /usr/sbin/wirelessproxd\n" +
                    "root               74   0.0  0.0  2566076   6996   ??  Ss   Mon07AM   0:24.74 /usr/libexec/opendirectoryd\n" +
                    "root               73   0.0  0.0  2542152   2904   ??  Us   Mon07AM   0:01.13 /System/Library/PrivateFrameworks/WirelessDiagnostics.framework/Support/awdd\n" +
                    "root               70   0.0  0.1  2553800   8416   ??  Ss   Mon07AM   0:46.18 /usr/libexec/coreduetd\n" +
                    "root               67   0.0  0.0  2539792   1284   ??  Ss   Mon07AM   0:00.55 /usr/libexec/diskarbitrationd\n" +
                    "root               66   0.0  0.0  2514460   1424   ??  Ss   Mon07AM   0:00.21 /System/Library/CoreServices/iconservicesagent\n" +
                    "root               61   0.0  0.1  2700640  14044   ??  Ss   Mon07AM   9:23.04 /System/Library/Frameworks/CoreServices.framework/Frameworks/Metadata.framework/Support/mds\n" +
                    "root               59   0.0  0.0  2525952   2328   ??  SNs  Mon07AM   0:01.95 /usr/libexec/warmd\n" +
                    "root               58   0.0  0.1  2551552  11796   ??  Us   Mon07AM   1:19.05 /usr/libexec/airportd\n" +
                    "root               55   0.0  0.0  2539900   2068   ??  Ss   Mon07AM   0:12.85 /System/Library/CoreServices/powerd.bundle/powerd\n" +
                    "root               54   0.0  0.0  2544636   5108   ??  Ss   Mon07AM   0:33.28 /usr/libexec/configd\n" +
                    "root               51   0.0  0.0  2514072   1268   ??  Ss   Mon07AM   0:05.37 /usr/libexec/thermald\n" +
                    "root               49   0.0  0.0  2555916   3216   ??  Ss   Mon07AM   5:53.96 /System/Library/Frameworks/CoreServices.framework/Versions/A/Frameworks/FSEvents.framework/Versions/A/Support/fseventsd\n",

            "lo0: flags=8049<UP,LOOPBACK,RUNNING,MULTICAST> mtu 16384\n" +
                    "\toptions=3<RXCSUM,TXCSUM>\n" +
                    "\tinet6 ::1 prefixlen 128 \n" +
                    "\tinet 127.0.0.1 netmask 0xff000000 \n" +
                    "\tinet6 fe80::1%lo0 prefixlen 64 scopeid 0x1 \n" +
                    "\tnd6 options=1<PERFORMNUD>\n" +
                    "gif0: flags=8010<POINTOPOINT,MULTICAST> mtu 1280\n" +
                    "stf0: flags=0<> mtu 1280\n" +
                    "en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500\n" +
                    "\tether 6c:40:08:b0:58:78 \n" +
                    "\tinet6 fe80::6e40:8ff:feb0:5878%en0 prefixlen 64 scopeid 0x4 \n" +
                    "\tinet 192.168.1.131 netmask 0xffffff00 broadcast 192.168.1.255\n" +
                    "\tnd6 options=1<PERFORMNUD>\n" +
                    "\tmedia: autoselect\n" +
                    "\tstatus: active\n" +
                    "en1: flags=8963<UP,BROADCAST,SMART,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1500\n" +
                    "\toptions=60<TSO4,TSO6>\n" +
                    "\tether 72:00:07:30:4c:80 \n" +
                    "\tmedia: autoselect <full-duplex>\n" +
                    "\tstatus: inactive\n" +
                    "en2: flags=8963<UP,BROADCAST,SMART,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1500\n" +
                    "\toptions=60<TSO4,TSO6>\n" +
                    "\tether 72:00:07:30:4c:81 \n" +
                    "\tmedia: autoselect <full-duplex>\n" +
                    "\tstatus: inactive\n" +
                    "p2p0: flags=8843<UP,BROADCAST,RUNNING,SIMPLEX,MULTICAST> mtu 2304\n" +
                    "\tether 0e:40:08:b0:58:78 \n" +
                    "\tmedia: autoselect\n" +
                    "\tstatus: inactive\n" +
                    "awdl0: flags=8943<UP,BROADCAST,RUNNING,PROMISC,SIMPLEX,MULTICAST> mtu 1452\n" +
                    "\tether c6:a1:2f:c1:05:da \n" +
                    "\tinet6 fe80::c4a1:2fff:fec1:5da%awdl0 prefixlen 64 scopeid 0x8 \n" +
                    "\tnd6 options=1<PERFORMNUD>\n" +
                    "\tmedia: autoselect\n" +
                    "\tstatus: active\n" +
                    "bridge0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500\n" +
                    "\toptions=63<RXCSUM,TXCSUM,TSO4,TSO6>\n" +
                    "\tether 6e:40:08:0b:fd:00 \n" +
                    "\tConfiguration:\n" +
                    "\t\tid 0:0:0:0:0:0 priority 0 hellotime 0 fwddelay 0\n" +
                    "\t\tmaxage 0 holdcnt 0 proto stp maxaddr 100 timeout 1200\n" +
                    "\t\troot id 0:0:0:0:0:0 priority 0 ifcost 0 port 0\n" +
                    "\t\tipfilter disabled flags 0x2\n" +
                    "\tmember: en1 flags=3<LEARNING,DISCOVER>\n" +
                    "\t        ifmaxaddr 0 port 5 priority 0 path cost 0\n" +
                    "\tmember: en2 flags=3<LEARNING,DISCOVER>\n" +
                    "\t        ifmaxaddr 0 port 6 priority 0 path cost 0\n" +
                    "\tnd6 options=1<PERFORMNUD>\n" +
                    "\tmedia: <unknown type>\n" +
                    "\tstatus: inactive\n",

            "dtrace: 2559 dynamic variable drops with non-empty dirty list\n" +
                    "SYSCALL(args) \t\t = return\n" +
                    "thread_selfid(0x0, 0x0, 0x0)\t\t = 3860235 0\n" +
                    "csops(0x0, 0x0, 0x7FFF58872168)\t\t = 0 0\n" +
                    "issetugid(0x0, 0x0, 0x7FFF58872168)\t\t = 0 0\n" +
                    "shared_region_check_np(0x7FFF588700A8, 0x0, 0x7FFF58872168)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/dtrace/libdtrace_dyld.dylib\\0\", 0x7FFF58871238, 0x7FFF58872168)\t\t = 0 0\n" +
                    "open(\"/usr/lib/dtrace/libdtrace_dyld.dylib\\0\", 0x0, 0x0)\t\t = 3 0\n" +
                    "pread(0x3, \"\\312\\376\\272\\276\\0\", 0x1000, 0x0)\t\t = 4096 0\n" +
                    "pread(0x3, \"\\317\\372\\355\\376\\a\\0\", 0x1000, 0x1000)\t\t = 4096 0\n" +
                    "fcntl(0x3, 0x3D, 0x7FFF5886F5A0)\t\t = 0 0\n" +
                    "mmap(0x10739D000, 0x2000, 0x5, 0x12, 0x3, 0x1000)\t\t = 0x10739D000 0\n" +
                    "mmap(0x10739F000, 0x1000, 0x3, 0x12, 0x3, 0x3000)\t\t = 0x10739F000 0\n" +
                    "mmap(0x1073A0000, 0x1FC0, 0x1, 0x12, 0x3, 0x4000)\t\t = 0x1073A0000 0\n" +
                    "close(0x3)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/dtrace/libdtrace_dyld.dylib\\0\", 0x7FFF58871BB8, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/libSystem.B.dylib\\0\", 0x7FFF58871068, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libcache.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libcommonCrypto.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libcompiler_rt.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libcopyfile.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libcorecrypto.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libdispatch.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libdyld.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libkeymgr.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/liblaunch.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libmacho.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libquarantine.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libremovefile.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_asl.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_blocks.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_c.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_configuration.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_coreservices.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_coretls.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_dnssd.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_info.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_kernel.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_m.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_malloc.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_network.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_networkextension.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_notify.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_platform.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_pthread.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_sandbox.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_secinit.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_stats.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libsystem_trace.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libunc.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libunwind.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/system/libxpc.dylib\\0\", 0x7FFF58870C58, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/libobjc.A.dylib\\0\", 0x7FFF5886FF38, 0x1)\t\t = 0 0\n" +
                    "stat64(\"/usr/lib/libauto.dylib\\0\", 0x7FFF5886FF38, 0x1)\t\t = 0 0\nsysctl(0x7FFF58872A80, 0x4, 0x0)\t\t = 0 0\n" +
                    "sysctl(0x7FFF58872A80, 0x4, 0x1073D2000)\t\t = 0 0\n" +
                    "shm_open(0x7FFF94579CCA, 0x0, 0x0)\t\t = 3 0\n" +
                    "mmap(0x0, 0x1000, 0x1, 0x1, 0x3, 0x0)\t\t = 0x107424000 0\n" +
                    "close_nocancel(0x3)\t\t = 0 0\n" +
                    "open_nocancel(\"/etc/.mdns_debug\\0\", 0x0, 0x0)\t\t = -1 Err#2\n" +
                    "issetugid(0x7FFF8CE3DC65, 0x0, 0x0)\t\t = 0 0\n" +
                    "issetugid(0x7FFF8CE3DC65, 0x0, 0x0)\t\t = 0 0\n" +
                    "sysctl(0x7FFF588728A4, 0x2, 0x7FFF588728A0)\t\t = 0 0\n" +
                    "sysctl(0x7FFF588728A4, 0x3, 0x107425000)\t\t = 0 0\n" +
                    "madvise(0x107425000, 0x40000, 0x9)\t\t = 0 0\n" +
                    "sysctl(0x7FFF588728A4, 0x2, 0x7FFF588728A0)\t\t = 0 0\n" +
                    "sysctl(0x7FFF588728A4, 0x3, 0x107425000)\t\t = 0 0\n" +
                    "madvise(0x107425000, 0x40000, 0x9)\t\t = 0 0\n" +
                    "sysctl(0x7FFF588728A4, 0x2, 0x7FFF588728A0)\t\t = 0 0\n" +
                    "sysctl(0x7FFF588728A4, 0x3, 0x107425000)\t\t = 0 0\n" +
                    "madvise(0x107425000, 0x40000, 0x9)\t\t = 0 0\n" +
                    "write_nocancel(0x1, \"root            89019  73.9  0.2  2588288  28208 s000  S+    9:19PM   0:02.31 /usr/sbin/dtrace -x dynvarsize 4m -x evaltime exec -n ^J #pragma D option quiet^J ^J \\n\\0\", 0xA4)\t\t = 164 0\n",

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
        pushStyle();
        textFont(font);
        drawLines();
        popStyle();
    }

    void drawLines() {
        if (random(1) > .9f) {
            if (position < s.split("\n").length - 1) {
                position++;
            } else {
                next();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < position; i++) {
            sb.append(s.split("\n")[i]).append("\n");
        }
        text(sb.toString(), x, y);
    }

    void drawChars() {
        if (random(1) > .5f) {
            if (position < s.length()) {
                position++;
            } else {
                next();
            }
        }
        text(s.substring(0, position), x, y);
    }

    private void next() {
        s = tech[(int) random(tech.length)];
        position = 0;
    }
}
