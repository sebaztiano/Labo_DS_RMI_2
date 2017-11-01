package sessions;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class SessionHandler implements ISessionHandler {
	
	
	boolean isBinded = false;
	Map<String, RentalSession> clientSessions = new HashMap<String, RentalSession>();
	IManagerSession managerSession;
	
	@Override
	public void createReservationSession(String renter){
		try{
			RentalSession session = new RentalSession();
			IRentalSession stub = (IRentalSession) UnicastRemoteObject.exportObject(session,0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(renter, stub);
			clientSessions.put(renter, session);
			
		} catch(Exception e){
			System.err.println("Server Exception: " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void createManagerSession(String manager){
		
		if (!isBinded){
			
			try{
				ManagerSession session = new ManagerSession();
				IManagerSession stub = (IManagerSession) UnicastRemoteObject.exportObject(session,0);
				Registry registry = LocateRegistry.getRegistry();
				registry.bind(IManagerSession.class.toString(), stub);
				isBinded = true;
				
			} catch(Exception e){
				System.err.println("Server Exception: " + e.toString());
				e.printStackTrace();
			}		
		}
	}
	
	public void terminateReservationSession(String renter){
		try{
			Registry registry = LocateRegistry.getRegistry();
			registry.unbind(renter);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void terminateManagerSession(String manager){
		try{
			Registry registry = LocateRegistry.getRegistry();
			registry.unbind(IManagerSession.class.toString());
			isBinded = false;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
