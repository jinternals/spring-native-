package com.jinternals.demo.hello;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToString;

public class ResourceHelloService implements HelloService{

    public ResourceHelloService(Resource resource) {
        this.resource = resource;
    }

    private final Resource resource;

    @Override
    public String sayHello() {
        try{
            try(InputStream in = this.resource.getInputStream()){
                return copyToString(in, UTF_8);
            }
        }catch (Exception e){
            throw new IllegalStateException("Failed to read resource "+ resource, e);
        }
    }
}
