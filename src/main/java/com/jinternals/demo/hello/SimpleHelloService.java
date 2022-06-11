package com.jinternals.demo.hello;

public class SimpleHelloService implements HelloService{
    @Override
    public String sayHello() {
        return "Hello Native";
    }
}
