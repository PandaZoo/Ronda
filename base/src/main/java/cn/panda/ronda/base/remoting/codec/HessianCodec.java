package cn.panda.ronda.base.remoting.codec;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianCodec implements CodecInterface {

    @Override
    public CodecTypeEnum type() {
        return CodecTypeEnum.HESSIAN;
    }

    @Override
    public byte[] encode(Object object) throws IOException {
        if (object == null) {
            throw new NullPointerException();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(object);
        return os.toByteArray();
    }

    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) throws IOException {
        if (bytes == null) {
            throw new NullPointerException();
        }

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        return (T) hi.readObject();
    }
}
