package cn.panda.ronda.client.codec;

import cn.panda.ronda.base.remoting.codec.CodecInterface;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class MessageDecoder extends ByteToMessageDecoder {

    private CodecInterface codecInterface;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //收到的字节还不足一个int，即Encode阶段写入的数据总长度，先不处理
        if (in.readableBytes() < 4) {
            log.info("no enough readable bytes");
            return;
        }

        //此时收到的字节达到4个字节，提取一个int，即期望接收的数据总长度
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }

        //接收的字节流除去int剩余的字节长度还未达到期望的长度，表示数据未接收完整
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }

        //长度达到了，已经足够，读取出完整的数据
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        //把完整的数据反序列化为对象
        ResponseMessage responseMessage = codecInterface.decode(data, ResponseMessage.class);
        //当decode中把一个对象加入到out中，代表已经解析成功了，之后decode不再被调用
        out.add(responseMessage);
    }

    public void setCodecInterface(CodecInterface codecInterface) {
        this.codecInterface = codecInterface;
    }
}
