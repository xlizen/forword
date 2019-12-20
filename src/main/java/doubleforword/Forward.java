package doubleforword;

import cocmmon.ByteUtil;
import cocmmon.GlobInfo;
import cocmmon.SerialUtils;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author lengchunyun
 */
public class Forward {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 2, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(3), Executors.defaultThreadFactory());
    private static final String CHANNEL = "channel";
    private static final String SERIAL = "serial";
    private static SerialPort serialPort;

    public static void main(String[] args) {
        serialPort = SerialUtils.openSerialPort("COM3", 9600);
        assert serialPort != null;
        GlobInfo.MAP.put(SERIAL, serialPort);
        THREAD_POOL_EXECUTOR.execute(Forward::run);
    }


    private static void startServer0() throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ForwordHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("192.168.10.174", 9090).sync();
            if (channelFuture.isSuccess()) {
                GlobInfo.MAP.put(CHANNEL, channelFuture.channel());
                SerialUtils.setListenerToSerialPort(serialPort, serialPortEvent -> {
                    if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                        byte[] bytes = SerialUtils.readData(serialPort);
                        if (GlobInfo.MAP.get(CHANNEL) != null) {
                            ((Channel) GlobInfo.MAP.get("channel")).writeAndFlush(Unpooled.copiedBuffer(bytes));
                        }
                        System.out.println(LocalDateTime.now()+" 收到的数据：" + ByteUtil.toHex(bytes));
                    }
                });
                System.out.print("请输入设备的sn码: ");
                Scanner scanner = new Scanner(System.in);
                String next = scanner.next();
                channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(next.getBytes(StandardCharsets.US_ASCII)));
            }
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    private static void run() {
        try {
            startServer0();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
