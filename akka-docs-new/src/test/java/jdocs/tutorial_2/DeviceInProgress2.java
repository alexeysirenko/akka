package jdocs.tutorial_2.inprogress2;

//#device-with-read

import java.util.Optional;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

class Device extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    String groupId;

    String deviceId;

    public Device(String groupId, String deviceId) {
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    public static Props props(String groupId, String deviceId) {
        return Props.create(Device.class, groupId, deviceId);
    }

    //#read-protocol-2
    final static class ReadTemperature {
        Long requestId;

        public ReadTemperature(Long requestId) {
            this.requestId = requestId;
        }
    }

    final static class RespondTemperature {
        Long requestId;
        Optional<Double> value;

        public RespondTemperature(Long requestId, Optional<Double> value) {
            this.requestId = requestId;
            this.value = value;
        }
    }
    //#read-protocol-2

    Optional<Double> lastTemperatureReading = Optional.empty();

    @Override
    public void preStart() {
        log.info("Device actor {}-{} started", groupId, deviceId);
    }

    @Override
    public void postStop() {
        log.info("Device actor {}-{} stopped", groupId, deviceId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReadTemperature.class, r -> {
                    sender().tell(new RespondTemperature(r.requestId, lastTemperatureReading), getSelf());
                })
                .build();
    }

}

//#device-with-read
