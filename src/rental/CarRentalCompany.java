package rental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CarRentalCompany implements ICarRentalCompany{
	
	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	private List<String> regions;
	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		setRegions(regions);
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
		logger.log(Level.INFO, this.toString());
	}

	/********
	 * NAME *
	 ********/
	
	@Override
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    @Override
    public List<String> getRegions() {
        return this.regions;
    }
    
    @Override
    public boolean hasRegion(String region) {
        return this.regions.contains(region);
    }
	
	/*************
	 * CAR TYPES *
	 *************/
    
    @Override
	public Collection<CarType> getAllCarTypes() {
		return carTypes.values();
	}
	@Override
	public CarType getCarType(String carTypeName) {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	
	public boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName)) {
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		} else {
			throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
		}
	}
	
	@Override
	public Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}
	
	/*********
	 * CARS *
	 *********/
	
	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}
	
	public List<Car> getCars() {
        return cars;
    }

	/****************
	 * RESERVATIONS *
	 ****************/
	
	@Override
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
                        new Object[]{name, client, constraints.toString()});
		
				
		if(!regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
				+ "> No cars available to satisfy the given constraints.");

		CarType type = getCarType(constraints.getCarType());
		
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}
	
	@Override
	public synchronized Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
	                + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random()*availableCars.size()));
		
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		
		return res;
	}

	public synchronized void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
	
	@Override
	public String toString() {
		return String.format("<%s> CRC is active in regions %s and serving with %d car types", name, listToString(regions), carTypes.size());
	}
	
	private static String listToString(List<? extends Object> input) {
		StringBuilder out = new StringBuilder();
		for (int i=0; i < input.size(); i++) {
			if (i == input.size()-1) {
				out.append(input.get(i).toString());
			} else {
				out.append(input.get(i).toString()+", ");
			}
		}
		return out.toString();
	}
	
	@Override
	public List<Reservation> getRenterReservations(String renter){
		List<Reservation> renterReservations = new ArrayList<Reservation>();
		for(Car car: cars){
			renterReservations.addAll(car.getRenterReservations(renter));
		}
		return renterReservations;
	}
	
	@Override
	public int getNumberOfReservationsForCarType(String carTypeName){
		int amountOfCarType = 0;
		CarType carType = getCarType(carTypeName);
		for(Car car: cars){
			if (car.getType()==carType)
			amountOfCarType += car.getNumberReservations();
		}
		return amountOfCarType;
	}
	
	@Override
	public Map<String, Integer> getReservationForEachCarType() {
        Collection<CarType> carTypes = this.getAllCarTypes();
        Map<String, Integer> solution = new HashMap<String, Integer>();
        for(CarType carType:carTypes){
            solution.put(carType.getName(), 0);
        }
        Collection<Car> cars = getCars();
        for (Car car:cars){
            int amountOfReservations = car.getAllReservations().size();
            Integer currentNumber = solution.get(car.getType().getName()) + amountOfReservations;
            solution.put(car.getType().getName(), currentNumber);
        }
        return solution;
    }
	
	public int getReservationCartype(String carType){
		Map<String,Integer> reservationMap = getReservationForEachCarType();
		return reservationMap.get(carType);
	}
	
	public Map<String, Integer> getCustomersPerCompany(Map<String, Integer> nbReservationsPerCustomer) {
        Collection<Car> cars = getCars();
        for(Car car: cars){
            List<Reservation> reservations = car.getAllReservations();
            for(Reservation reservation:reservations){
                String carRenter = reservation.getCarRenter();
                AddOneToHashMapValue(nbReservationsPerCustomer, carRenter);
            }
        }
        
        return nbReservationsPerCustomer;
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	
	private void AddOneToHashMapValue(Map<String,Integer> hashMap, String key){
        
        if(hashMap.get(key) != null){
            hashMap.put(key, 1);
        }
        else{
            int currentAmount = hashMap.get(key);
            currentAmount++;
            hashMap.put(key, currentAmount);
        }
    }
}