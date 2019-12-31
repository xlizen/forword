package doubleforword;

import cocmmon.ByteUtil;
import cocmmon.GlobInfo;
import cocmmon.SerialUtils;
import gnu.io.SerialPort;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


/**
 * @author lengchunyun
 */
@Slf4j
@ChannelHandler.Sharable
public class ForwardHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final String SERIAL = "serial";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int i = msg.readableBytes();
        byte[] bytes = new byte[i];
        msg.readBytes(bytes);
        log.info(LocalDateTime.now() + "接收到来自服务器的数据为: {}", ByteUtil.toHex(bytes));
        if (GlobInfo.MAP.get(SERIAL) != null) {
            SerialUtils.sendData((SerialPort) GlobInfo.MAP.get(SERIAL), bytes);
        }
    }
}
