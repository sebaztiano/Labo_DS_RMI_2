package sessions;

import java.rmi.Remote;

public interface ISessionHandler extends Remote{
	void createReservationSession(String renter);
	void createManagerSession(String manager);
	void terminateReservationSession(String renter);
	
}
