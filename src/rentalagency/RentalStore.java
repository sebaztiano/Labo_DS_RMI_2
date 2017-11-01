package rentalagency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.Reservation;
import sessions.ISessionHandler;

public class RentalStore {
    private static Map<String, ICarRentalCompany> rentals;

    public static ICarRentalCompany getRental(String company) {
        ICarRentalCompany out = RentalStore.getRentals().get(company);
        if (out == null) {
            throw new IllegalArgumentException("Company doesn't exist!: " + company);
        }
        return out;
    }
    
    public static synchronized Map<String, ICarRentalCompany> getRentals(){
        if(rentals == null){
            rentals = new HashMap<String, ICarRentalCompany>();
			try{
				Registry registry = LocateRegistry.getRegistry("localhost");
				ICarRentalCompany company = (ICarRentalCompany) registry.lookup("hertz");
				ICarRentalCompany company2 = (ICarRentalCompany) registry.lookup("dockx");
				rentals.put("hertz", company);
				rentals.put("dockx", company2);
			}catch(Exception e){
				e.printStackTrace();
			}
        }
        return rentals;
    }
    
    public static synchronized void registerCompany(String lookup){
		try{
			Registry registry = LocateRegistry.getRegistry("localhost");
			ICarRentalCompany newCompany = (ICarRentalCompany) registry.lookup(lookup);
			rentals.put(lookup, newCompany);
		}catch(Exception e){
			e.printStackTrace();
		}
    	
		
    }
    
    public static synchronized boolean unregisterCompany(String name){
    	boolean companyRemoved = false;
    	if(rentals.containsKey(name)){
    		rentals.remove(name);
    		companyRemoved = true;
    	}
    	return companyRemoved;
    }   
    

}
