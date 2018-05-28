package cn.panda.ronda.base.remoting.codec.msgpack.template;

import cn.panda.ronda.base.helper.MsgPackHelper;
import cn.panda.ronda.base.remoting.message.RequestMessage;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.Map;

public class RequestMessageTemplate extends AbstractTemplate<RequestMessage> {

    @Override
    @SuppressWarnings({"Duplicates", "unchecked"})
    public void write(Packer packer, RequestMessage requestMessage, boolean b) throws IOException {
        if (requestMessage == null) {
            packer.writeArrayBegin(1);
            packer.writeNil();
            packer.writeArrayEnd();
            return;
        }
        packer.writeArrayBegin(7);

        // write messageId
        packer.write(requestMessage.getMessageId());

        // write targetClass
        packer.write(requestMessage.getTargetClass());

        // write targetMethod
        packer.write(requestMessage.getTargetMethod());

        // writeArgTypes
        writeArgTypes(packer, requestMessage.getArgType());

        // writeArgs
        writeArgs(packer, requestMessage.getArgs());

        // writeBody
        if (requestMessage.getBody() == null) {
            packer.writeNil();
        } else {
            packer.write(requestMessage.getBody());
        }

        // writeAttributes
        MsgPackHelper.getTemplateMap().get(Map.class).write(packer, requestMessage.getAttributesMap());

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
    public RequestMessage read(Unpacker unpacker, RequestMessage requestMessage, boolean b) throws IOException {
        requestMessage = new RequestMessage();

        unpacker.readArrayBegin();

        // read messageId
        requestMessage.setMessageId(unpacker.read(Long.class));

        // read targetClass
        requestMessage.setTargetClass(unpacker.read(String.class));

        // read target method
        requestMessage.setTargetMethod(unpacker.read(String.class));

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
            requestMessage.setArgType(clazz);

            // read Args

            int argsLength = unpacker.readArrayBegin();
            Object[] args = new Object[argsLength];
            for (int i = 0; i < argsLength; i++) {
                args[i] = unpacker.read(clazz[i]);
            }
            unpacker.readArrayEnd();
            requestMessage.setArgs(args);
        }

        // read body
        requestMessage.setBody(unpacker.read(String.class));

        // read attributes
        if (! unpacker.trySkipNil()) {
            Map<String, Object> read = (Map<String, Object>)MsgPackHelper.getTemplateMap().get(Map.class).read(unpacker, null);
            requestMessage.setAttributesMap(read);
        }

        unpacker.readArrayEnd();

        return requestMessage;
    }

}
