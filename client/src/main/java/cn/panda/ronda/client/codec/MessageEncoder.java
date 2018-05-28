package cn.panda.ronda.client.codec;

import cn.panda.ronda.base.remoting.codec.CodecInterface;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageEncoder extends MessageToByteEncoder {

    private CodecInterface codecInterface;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = codecInterface.encode(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    public void setCodecInterface(CodecInterface codecInterface) {
        this.codecInterface = codecInterface;
    }
}
