package akka.java.picalculation;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;

import java.util.concurrent.TimeUnit;

public class Master extends UntypedActor {

    private final int nrOfMessages;
    private final int nrOfElements;

    private double pi = 0;
    private int nrOfResults;
    private final long start = System.currentTimeMillis();

    private final ActorRef listener;
    private final ActorRef workerRouter;

    public Master(final int nrOfWorkers, int nrOfMessages, int nrOfElements, ActorRef listener) {
        this.nrOfMessages = nrOfMessages;
        this.nrOfElements = nrOfElements;
        this.listener = listener;

        workerRouter = this.getContext().actorOf(new Props(Worker.class).withRouter(new RoundRobinRouter(nrOfWorkers)),
                "workerRouter");
    }

    public void onReceive(Object message) {
        if (message instanceof Pi.Calculate) {
            for (int start = 0; start < nrOfMessages; start++) {
                workerRouter.tell(new Pi.Work(start, nrOfElements), getSelf());
            }
        } else if (message instanceof Pi.Result) {
            Pi.Result result = (Pi.Result) message;
            pi += result.getValue();
            nrOfResults += 1;
            if (nrOfResults == nrOfMessages) {
                // Send the result to the listener
                Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                listener.tell(new Pi.PiApproximation(pi, duration), getSelf());
                // Stops this actor and all its supervised children
                getContext().stop(getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}
