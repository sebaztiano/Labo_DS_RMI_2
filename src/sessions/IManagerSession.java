package sessions;

import java.io.IOException;
import java.rmi.Remote;
import java.util.Set;

public interface IManagerSession extends Remote{
	public Set<String> getBestClient();
	public int getNumberOfReservationsForCarType(String carRentalName, String carType);
	public void registerCompany(String lookup) throws NumberFormatException, IOException;
	public boolean unregisterCompany(String name);
	public Set<String> getAllRentalCompanies();
}
