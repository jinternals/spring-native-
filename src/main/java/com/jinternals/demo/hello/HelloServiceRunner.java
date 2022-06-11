package com.jinternals.demo.hello;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static java.lang.System.out;

@Component
@ImportRuntimeHints(HelloServiceRunner.Registrar.class)
public class HelloServiceRunner implements ApplicationRunner {
    private final ObjectProvider<HelloService> helloServices;

    public HelloServiceRunner(ObjectProvider<HelloService> helloServices) {
        this.helloServices = helloServices;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        out.println(getHelloMessage(args));
    }

    String getHelloMessage(ApplicationArguments arguments) throws Exception {
        if (arguments.containsOption("bean")) {
            HelloService service = this.helloServices.getIfUnique();
            return service != null ? service.sayHello() : "No bean found";
        } else if (arguments.containsOption("reflection")) {
            String implementationName = SimpleHelloService.class.getName();
            Class<?> implementationClass = ClassUtils.forName(implementationName, getClass().getClassLoader());
            Method method = implementationClass.getMethod("sayHello");
            Object instance = BeanUtils.instantiateClass(implementationClass);
            return (String) ReflectionUtils.invokeMethod(method, instance);
        } else if (arguments.containsOption("resource")) {
            ResourceHelloService helloService = new ResourceHelloService(new ClassPathResource("hello.text"));
            return helloService.sayHello();
        }
        return "No option provided";
    }

    static class Registrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints
                    .reflection()
                    .registerConstructor(SimpleHelloService.class.getDeclaredConstructors()[0])
                    .registerMethod(ReflectionUtils.findMethod(SimpleHelloService.class, "sayHello"));

            hints
                    .resources()
                    .registerPattern("hello.text");

        }

    }
}
