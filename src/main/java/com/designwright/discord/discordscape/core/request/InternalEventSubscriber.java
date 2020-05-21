//package dev.codesupport.discord.discordscape.core.messaging;
//
//public abstract class InternalEventSubscriber<T> {
//
//    public void receive(InternalRequest<T> internalRequest) {
//
//    }
//
//    public InternalRequest<T> receiveAndReply(InternalRequest<T> internalRequest) {
//        return internalRequest;
//    }
//
//    static <T> InternalEventSubscriber<T> createSubscriberNoReply(InternalBroker.ListenerInvoker invoker) {
//        return new InternalEventSubscriber<T>() {
//            @Override
//            public void receive(InternalRequest<T> internalRequest) {
//                invoker.execute(internalRequest);
//            }
//        };
//    }
//
//    static <T> InternalEventSubscriber<T> createSubscriberWithReply(InternalBroker.ListenerInvoker invoker) {
//        return new InternalEventSubscriber<T>() {
//            @Override
//            public InternalRequest<T> receiveAndReply(InternalRequest<T> internalRequest) {
//                return (InternalRequest<T>) invoker.execute(internalRequest);
//            }
//        };
//    }
//
//}
