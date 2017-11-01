package rental;

import java.rmi.Remote;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICarRentalCompany extends Remote{
	
	public String getName();
	public List<String> getRegions();
	public boolean hasRegion(String region);
	public Collection<CarType> getAllCarTypes();
	public CarType getCarType(String carTypeName);
	public boolean isAvailable(String carTypeName, Date start, Date end);
	public Set<CarType> getAvailableCarTypes(Date start, Date end);
	public Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException;
	public Reservation confirmQuote(Quote quote) throws ReservationException;
	public void cancelReservation(Reservation res);
	public List<Reservation> getRenterReservations(String renter);
	public int getNumberOfReservationsForCarType(String carTypeName);
	public Map<String, Integer> getReservationForEachCarType();
	public Map<String, Integer> getCustomersPerCompany(Map<String, Integer> nbReservationsPerCustomer);
	public int getReservationCartype(String carType);

}
