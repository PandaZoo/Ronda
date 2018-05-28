package cn.panda.ronda.base.remoting.codec;

import cn.panda.ronda.base.helper.MsgPackHelper;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.unpacker.Unpacker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MsgpackCodec implements CodecInterface {

    @Override
    public CodecTypeEnum type() {
        return CodecTypeEnum.MSGPACK;
    }

    @Override
    public byte[] encode(Object object) throws IOException {
        MessagePack messagePack = MsgPackHelper.getMessagePack();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Packer packer = messagePack.createPacker(outputStream);
        AbstractTemplate template = MsgPackHelper.getTemplateMap().get(object.getClass());
        if (template == null) {
            packer.write(object);
        } else {
            template.write(packer, object);
        }

        return outputStream.toByteArray();
    }

    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        Unpacker unpacker = MsgPackHelper.getMessagePack().createUnpacker(byteInputStream);
        AbstractTemplate template = MsgPackHelper.getTemplateMap().get(clazz);
        if (template == null) {
            return unpacker.read(clazz);
        } else {
            return (T) template.read(unpacker, null);
        }
    }
}
