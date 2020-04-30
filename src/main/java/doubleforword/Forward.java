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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author lengchunyun
 */
@Slf4j
public class Forward {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 2, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(3), Executors.defaultThreadFactory());
    private static final String CHANNEL = "channel";
    private static final String SERIAL = "serial";
    private static SerialPort serialPort;

    public static void main(String[] args) {
        SerialUtils.listPort();
        System.out.print("请输入要打开的串口号（默认COM3）: ");
        Scanner scanner = new Scanner(System.in);
        String port;
        port= scanner.nextLine();
        if ("".equals(port)){
            port= "COM3";
        }
        serialPort = SerialUtils.openSerialPort(port, 9600);
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
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            super.exceptionCaught(ctx, cause);
                            eventExecutors.shutdownGracefully();
                        }

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ForwardHandler());

                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("192.168.10.174", 9090).sync();
            if (channelFuture.isSuccess()) {
                GlobInfo.MAP.put(CHANNEL, channelFuture.channel());
                SerialUtils.setListenerToSerialPort(serialPort, serialPortEvent -> {
                    if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                        byte[] bytes = SerialUtils.readData(serialPort);
                        if (GlobInfo.MAP.get(CHANNEL) != null) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ((Channel) GlobInfo.MAP.get("channel")).writeAndFlush(Unpooled.copiedBuffer(bytes));
                        }
                        log.info(LocalDateTime.now()+" 收到来自设备的数据：{}" ,ByteUtil.toHex(bytes));
                    }
                });
                System.out.print("请输入设备的sn码: ");
                Scanner scanner = new Scanner(System.in);
                String next = scanner.nextLine();
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
