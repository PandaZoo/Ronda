package cn.panda.ronda.base.remoting.codec.msgpack.template;

import cn.panda.ronda.base.helper.MsgPackHelper;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.Map;

public class ResponseMessageTemplate extends AbstractTemplate<ResponseMessage> {

    @Override
    @SuppressWarnings({"Duplicates", "unchecked"})
    public void write(Packer packer, ResponseMessage v, boolean required) throws IOException {
        if (v == null) {
            packer.writeArrayBegin(1);
            packer.writeNil();
            packer.writeArrayEnd();
            return;
        }
        packer.writeArrayBegin(9);

        // write messageId
        packer.write(v.getMessageId());

        // write targetClass
        packer.write(v.getTargetClass());

        // write targetMethod
        packer.write(v.getTargetMethod());

        // writeArgTypes
        writeArgTypes(packer, v.getArgType());

        // writeArgs
        writeArgs(packer, v.getArgs());

        // writeBody
        if (v.getBody() == null) {
            packer.writeNil();
        } else {
            packer.write(v.getBody());
        }

        // writeAttributes
        MsgPackHelper.getTemplateMap().get(Map.class).write(packer, v.getAttributesMap());

        // returnCode
        packer.write(v.getReturnCode());

        // errorMessage
        packer.write(v.getErrorMessage());

        packer.writeArrayEnd();
    }


    private void writeArgTypes(Packer packer, Class[] argTypes) throws IOException {
        if (argTypes == null || argTypes.length == 0) {
            packer.writeNil();
            return;
        }

        packer.writeArrayBegin(argTypes.length);
        for (Class clazz : argTypes) {
            packer.write(clazz.getCanonicalName());
        }
        packer.writeArrayEnd();
    }


    @SuppressWarnings("Duplicates")
    private void writeArgs(Packer packer, Object[] args) throws IOException {
        if (args == null || args.length == 0) {
            packer.writeNil();
            return;
        }

        packer.writeArrayBegin(args.length);
        for (Object obj : args) {
            Template template = MsgPackHelper.getTemplateRegistry().lookup(obj.getClass());
            if (template == null) {
                MsgPackHelper.getTemplateRegistry().register(obj.getClass());
            }
            packer.write(obj);
        }
        packer.writeArrayEnd();

    }

    @Override
    @SuppressWarnings({"Duplicates", "unchecked"})
    public ResponseMessage read(Unpacker unpacker, ResponseMessage to, boolean required) throws IOException {
        to = new ResponseMessage();

        unpacker.readArrayBegin();

        // read messageId
        to.setMessageId(unpacker.read(Long.class));

        // read targetClass
        to.setTargetClass(unpacker.read(String.class));

        // read target method
        to.setTargetMethod(unpacker.read(String.class));

        // read ArgType
        if (unpacker.trySkipNil()) {
            unpacker.readNil();
        } else {
            int argTypeLength = unpacker.readArrayBegin();
            Class[] clazz = new Class[argTypeLength];
            for (int i = 0; i < argTypeLength; i ++) {
                String clazzName = unpacker.read(String.class);
                try {
                    Class<?> aClass = Class.forName(clazzName);
                    clazz[i] = aClass;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            unpacker.readArrayEnd();
            to.setArgType(clazz);

            // read Args

            int argsLength = unpacker.readArrayBegin();
            Object[] args = new Object[argsLength];
            for (int i = 0; i < argsLength; i++) {
                args[i] = unpacker.read(clazz[i]);
            }
            unpacker.readArrayEnd();
            to.setArgs(args);
        }

        // read body
        to.setBody(unpacker.read(String.class));

        // read attributes
        if (! unpacker.trySkipNil()) {
            Map<String, Object> map = (Map<String, Object>) MsgPackHelper.getTemplateMap().get(Map.class).read(unpacker, null);
            to.setAttributesMap(map);
        }

        to.setReturnCode(unpacker.read(Integer.class));
        to.setErrorMessage(unpacker.read(String.class));

        unpacker.readArrayEnd();

        return to;
    }
}
