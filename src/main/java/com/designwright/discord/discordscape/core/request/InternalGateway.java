package com.designwright.discord.discordscape.core.request;

import com.designwright.discord.discordscape.core.exception.InternalRequestException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InternalGateway {

    public final Map<String, ListenerMethod> noReplySubscriberMap;
    public final Map<String, ListenerMethod> replySubscriberMap;

    private final ApplicationContext context;

    public InternalGateway(ApplicationContext context) {
        noReplySubscriberMap = new HashMap<>();
        replySubscriberMap = new HashMap<>();
        this.context = context;
    }

    @PostConstruct
    public void init() {
        Map<String, Object> beanMap = context.getBeansWithAnnotation(InternalRequestService.class);
        mapListenerBeans(beanMap);
    }

    void mapListenerBeans(Map<String, Object> beanMap) {
        for (Object bean : beanMap.values()) {
            mapListenerBean(bean);
        }
    }

    void mapListenerBean(Object bean) {
        for (Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(InternalRequestListener.class)) {
                mapListenerMethodFromBean(method, bean);
            }
        }
    }

    void mapListenerMethodFromBean(Method method, Object bean) {
        if (
                method.getParameterCount() == 1
                        && method.getParameters()[0].getType().isAssignableFrom(InternalRequest.class)
        ) {
            if (method.getReturnType().isAssignableFrom(InternalRequest.class)) {
                InternalRequestListener listenerAnnotation = method.getAnnotation(InternalRequestListener.class);
                String requestMethod = listenerAnnotation.value().toString();
                Class<?> returnType = getGenericParameterType(method.getGenericReturnType());
                Class<?> argumentType = getGenericParameterType(method.getParameters()[0].getParameterizedType());
                String mapKey = requestMethod + ":" + argumentType.getCanonicalName();
                if (returnType != null && argumentType != null) {
                    replySubscriberMap.put(
                            mapKey,
                            new ListenerMethod(
                                    method,
                                    bean,
                                    returnType,
                                    argumentType,
                                    request -> {
                                        try {
                                            return (InternalRequest<?>) method.invoke(bean, request);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            throw new InternalRequestException("Failed to invoke listener method", e);
                                        }
                                    })
                    );
                }
            } else if (method.getReturnType().isAssignableFrom(void.class)) {
                InternalRequestListener listenerAnnotation = method.getAnnotation(InternalRequestListener.class);
                String requestMethod = listenerAnnotation.value().toString();
                Class<?> argumentType = getGenericParameterType(method.getParameters()[0].getParameterizedType());
                String mapKey = requestMethod + ":" + argumentType.getCanonicalName();
                if (argumentType != null) {
                    noReplySubscriberMap.put(
                            mapKey,
                            new ListenerMethod(
                                    method,
                                    bean,
                                    null,
                                    argumentType,
                                    request -> {
                                        try {
                                            return (InternalRequest<?>) method.invoke(bean, request);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            throw new InternalRequestException("Failed to invoke listener method", e);
                                        }
                                    })
                    );
                }
            } else {
                log.warn("InternalEventListener annotation found on method with invalid return type");
            }
        } else {
            log.warn("InternalEventListener annotation found on method with invalid parameters");
        }
    }

    Class<?> getGenericParameterType(Type object) {
        Class<?> classType = null;

        if (object instanceof ParameterizedType) {
            Type returnTypeParameter = ((ParameterizedType) object).getActualTypeArguments()[0];

            if (returnTypeParameter instanceof Class) {
                classType = (Class<?>) returnTypeParameter;
            } else {
                log.warn("InternalEventListener annotation found on method with invalid return type");
            }
        } else {
            log.warn("InternalEventListener annotation found on method with invalid return type");
        }

        return classType;
    }

    public <T> InternalRequest<T> sendAndReceive(InternalRequest<T> internalRequest) {
        String requestMethod = internalRequest.getType().toString();
        Class<?> payloadType = internalRequest.getPayload().get(0).getClass();
        String mapKey = requestMethod + ":" + payloadType.getCanonicalName();
        ListenerMethod listenerMethod;
        if (replySubscriberMap.containsKey(mapKey)) {
            listenerMethod = replySubscriberMap.get(mapKey);
        } else {
            log.error("No listener exists for " + mapKey);
            throw new IllegalStateException("No listener exists for " + mapKey);
        }

        //noinspection unchecked
        return (InternalRequest<T>) listenerMethod.getInvoker().execute(internalRequest);
    }

    public <T> void send(InternalRequest<T> internalRequest) {
        String requestMethod = internalRequest.getType().toString();
        Class<?> payloadType = internalRequest.getPayload().get(0).getClass();
        String mapKey = requestMethod + ":" + payloadType.getCanonicalName();
        ListenerMethod listenerMethod;
        if (noReplySubscriberMap.containsKey(mapKey)) {
            listenerMethod = noReplySubscriberMap.get(mapKey);
        } else {
            log.error("No listener exists for " + mapKey);
            throw new IllegalStateException("No listener exists for " + mapKey);
        }

        listenerMethod.getInvoker().execute(internalRequest);
    }

    @Data
    private static class ListenerMethod {

        private final Method method;
        private final Object classObject;
        private final Class<?> returnType;
        private final Class<?> argumentType;
        private final ListenerInvoker invoker;

    }

    @FunctionalInterface
    interface ListenerInvoker {
        InternalRequest<?> execute(InternalRequest<?> event);
    }

}
