package cn.panda.ronda.spring.service.impl;

import cn.panda.ronda.spring.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author asdsut
 * created at 28/05/2018
 */
@Service
public class HelloServiceImpl implements HelloService {

    public String sayHello() {
        return "hello";
    }
}
