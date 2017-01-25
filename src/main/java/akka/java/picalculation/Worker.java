package akka.java.picalculation;

import akka.actor.UntypedActor;

public class Worker extends UntypedActor {

    /*static BigDecimal ONE = new BigDecimal(1);
    static BigDecimal TWO = new BigDecimal(2);
    static BigDecimal FOUR = TWO.pow(2);*/

    private double calculatePiFor(int start, int nrOfElements) {
//        BigDecimal acc = new BigDecimal(0);
        double acc = 0;
        for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
            acc += calculatePiFor(i);
        }
        return acc;
    }

    public static double calculatePiFor(int elem) {
            return (4.0 * (1 - (elem % 2) * 2)) / (2 * elem + 1);
        /*return FOUR.multiply(ONE.subtract(new BigDecimal(elem).remainder(TWO).multiply(TWO)))
                .divide(TWO.multiply(new BigDecimal(elem)).add(ONE), 100, RoundingMode.HALF_UP);*/
    }


    public void onReceive(Object message) {
        if (message instanceof Pi.Work) {
            Pi.Work work = (Pi.Work) message;
            double result = calculatePiFor(work.getStart(), work.getNrOfElements());
            getSender().tell(new Pi.Result(result), getSelf());
        } else {
            unhandled(message);
        }
    }
}
