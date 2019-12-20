package doubleforword;

import cocmmon.GlobInfo;
import cocmmon.SerialUtils;
import gnu.io.SerialPort;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ForwordHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final String SERIAL = "serial";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        int i = msg.readableBytes();
        byte[] bytes = new byte[i];
        msg.readBytes(bytes);
        if (GlobInfo.MAP.get(SERIAL) != null) {
            SerialUtils.sendData((SerialPort) GlobInfo.MAP.get(SERIAL), bytes);
        }
    }
}
