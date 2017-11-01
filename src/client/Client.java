package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import sessions.IManagerSession;
import sessions.IRentalSession;
import sessions.ISessionHandler;

public class Client extends AbstractTestManagement<IRentalSession,IManagerSession>{
	
	private ISessionHandler sessionHandler;

	public Client(String scriptFile) {
		super(scriptFile);
		try{
			System.setSecurityManager(null);
			Registry registry = LocateRegistry.getRegistry("localhost");
			sessionHandler = (ISessionHandler) registry.lookup(ISessionHandler.class.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected Set<String> getBestClients(IManagerSession ms) throws Exception {
		return ms.getBestClient();
	}

	@Override
	protected String getCheapestCarType(IRentalSession session, Date start, Date end, String region) throws Exception {
		return null;
	}

	@Override
	protected CarType getMostPopularCarTypeIn(IManagerSession ms, String carRentalCompanyName, int year)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IRentalSession getNewReservationSession(String name) throws Exception {
		sessionHandler.createReservationSession(name);
		Registry registry = LocateRegistry.getRegistry("localhost");
		IRentalSession rentalSession = (IRentalSession) registry.lookup(name);
		return rentalSession;
	}

	@Override
	protected IManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {
		sessionHandler.createManagerSession(name);
		Registry registry = LocateRegistry.getRegistry("localhost");
		IManagerSession managerSession = (IManagerSession) registry.lookup(IManagerSession.class.toString());
		return managerSession;
	}

	@Override
	protected void checkForAvailableCarTypes(IRentalSession session, Date start, Date end) throws Exception {
		session.getAllAvailableCarTypes(start, end);
		
	}

	@Override
	protected void addQuoteToSession(IRentalSession session, String name, Date start, Date end, String carType,
			String region) throws Exception {
		ReservationConstraints constraints = new ReservationConstraints(start,end,carType,region);
		session.createQuote(constraints, name);
	}

	@Override
	protected List<Reservation> confirmQuotes(IRentalSession session, String name) throws Exception {
		
		return session.confirmQuotes();
	}

	@Override
	protected int getNumberOfReservationsForCarType(IManagerSession ms, String carRentalName, String carType)
			throws Exception {
		return ms.getNumberOfReservationsForCarType(carRentalName, carType);
	}
}
