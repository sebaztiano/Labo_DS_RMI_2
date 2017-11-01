package sessions;

import java.rmi.Remote;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public interface IRentalSession extends Remote{
    Quote createQuote(ReservationConstraints constraints,String guest) throws ReservationException;
    List<Quote> getCurrentQuotes();
    List<Reservation> confirmQuotes() throws ReservationException;
    Set<CarType> getAllAvailableCarTypes(Date start, Date end);
    CarType getCheapestCarType(Date start, Date end);
}
