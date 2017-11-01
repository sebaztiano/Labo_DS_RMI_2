package rentalagency;

import java.util.Set;

public interface IRentalStore {
	
	public Set<String> getBestClient();
	public int getNumberOfReservationsForCarType(String carRentalName, String carType);
	

}
