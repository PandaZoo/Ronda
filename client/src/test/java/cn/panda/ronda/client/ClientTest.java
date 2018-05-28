package cn.panda.ronda.client;

import cn.panda.ronda.base.helper.MsgPackHelper;
import cn.panda.ronda.base.remoting.codec.MsgpackCodec;
import cn.panda.ronda.base.remoting.message.RequestMessage;
import cn.panda.ronda.base.remoting.message.ResponseMessage;
import cn.panda.ronda.client.demo.HelloService;
import cn.panda.ronda.client.transport.client.RondaClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.msgpack.unpacker.Unpacker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientTest {


    @Test
    public void test() {
        RondaClient rondaClient = new RondaClient();
        ResponseMessage responseMessage = rondaClient.invoke(genRequestMessage());
        log.info("返回结果是: {}", responseMessage);
    }

    private RequestMessage genRequestMessage() {
        RequestMessage message = new RequestMessage();
        message.setMessageId(1L);
        message.setTargetClass("cn.panda.ronda.client.demo.HelloService");
        message.setTargetMethod("sayHello");
        message.setBody("");
        message.setArgs(null);
        message.setArgType(null);
        return message;
    }

    @Test
    public void printClassName() {
        log.info("结果是: {}", String.valueOf(HelloService.class.getName()));
    }

    @Test
    public void msgPackSerialize() throws IOException {
        RequestMessage requestMessage = genRequestMessage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MsgPackHelper.getTemplateMap().get(RequestMessage.class).write(MsgPackHelper.getMessagePack().createPacker(outputStream), requestMessage);
        byte[] bytes = outputStream.toByteArray();
        System.out.println(Arrays.toString(bytes));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        RequestMessage dRequestMessage = null;
        RequestMessage read = (RequestMessage) MsgPackHelper.getTemplateMap().get(RequestMessage.class).read(MsgPackHelper.getMessagePack().createUnpacker(inputStream), dRequestMessage);
        System.out.println(read);
    }

    @Test
    public void serializeResponse() throws IOException {
        ResponseMessage  responseMessage = new ResponseMessage();
        responseMessage.setMessageId(1L);
        responseMessage.setTargetClass("cn.panda.ronda.client.demo.HelloService");
        responseMessage.setTargetMethod("sayHello");
        responseMessage.setBody("");
        responseMessage.setArgs(null);
        responseMessage.setArgType(null);
        responseMessage.setReturnCode(1);
        responseMessage.setErrorMessage("我老是楼层大森林里都发");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MsgPackHelper.getTemplateMap().get(ResponseMessage.class).write(MsgPackHelper.getMessagePack().createPacker(outputStream), responseMessage);
        byte[] bytes = outputStream.toByteArray();
        System.out.println(Arrays.toString(bytes));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ResponseMessage read = (ResponseMessage) MsgPackHelper.getTemplateMap().get(ResponseMessage.class).read(MsgPackHelper.getMessagePack().createUnpacker(inputStream), null);
        System.out.println(read);
    }

    @Test
    public void serializeA() throws IOException {
        ClassA classA = new ClassA(ClientTest.class, "我是我");
        ClassATemplate classATemplate = new ClassATemplate();
        MsgPackHelper.getMessagePack().register(ClassA.class, classATemplate);
        byte[] bytes = MsgPackHelper.getMessagePack().write(classA);
        System.out.println(Arrays.toString(bytes));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Unpacker unpacker = MsgPackHelper.getMessagePack().createUnpacker(inputStream);
        ClassA a = new ClassA();
        classATemplate.read(unpacker, a);
        System.out.println(a);
    }

    @Test
    public void serializeMap() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("111", 2);
        map.put("222", 3L);
        map.put("333", "3333");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MsgPackHelper.getTemplateMap().get(Map.class).write(MsgPackHelper.getMessagePack().createPacker(outputStream), map);
        byte[] bytes = outputStream.toByteArray();
        System.out.println(Arrays.toString(bytes));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Map<String, Object> dMap = null;
        Object read = MsgPackHelper.getTemplateMap().get(Map.class).read(MsgPackHelper.getMessagePack().createUnpacker(inputStream), dMap);
        System.out.println(read);
    }
}
