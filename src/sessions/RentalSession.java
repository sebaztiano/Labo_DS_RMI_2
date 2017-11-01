package sessions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import rentalagency.RentalStore;


public class RentalSession implements IRentalSession {
    List<Quote> availableQuotes = new ArrayList<Quote>();
    
    public Set<String> getAllRentalCompanies(){
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    @Override
    public Quote createQuote(ReservationConstraints constraints, String guest) throws ReservationException{
        Map<String, ICarRentalCompany> rentals = RentalStore.getRentals();
        String carType = constraints.getCarType();
        Date startDate = constraints.getStartDate();
        Date endDate = constraints.getEndDate();
        
        for(String company: rentals.keySet()){
            ICarRentalCompany carCompany = rentals.get(company);
            Boolean isAvailable = carCompany.isAvailable(carType, startDate,endDate); 
            if(isAvailable){
                try{
                    Quote quote = carCompany.createQuote(constraints, guest);
                    this.availableQuotes.add(quote);
                    return quote;
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            
        }
        throw new ReservationException("no reservations possible");
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Quote> getCurrentQuotes() {
        return this.availableQuotes;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> confirmedQuotes = new ArrayList<Reservation>();
        List<Quote> quotes = getCurrentQuotes();
        Map<String, ICarRentalCompany> rentals = RentalStore.getRentals();
        try{
            for(Quote quote:quotes){
                ICarRentalCompany company = rentals.get(quote.getRentalCompany());
                Reservation reservation = company.confirmQuote(quote);
                confirmedQuotes.add(reservation);
            }
        }
        catch(Exception e){
            for(Reservation reservation: confirmedQuotes){
                ICarRentalCompany company = rentals.get(reservation.getRentalCompany());
                company.cancelReservation(reservation);
            }
            throw new ReservationException("");
        }
        return confirmedQuotes;
    }
    
    @Override
    public Set<CarType> getAllAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCartypes = new HashSet<CarType>();
        Map<String,ICarRentalCompany> companies = RentalStore.getRentals();
        Set<String> keySet = companies.keySet();
        for(String key: keySet){
            ICarRentalCompany company = companies.get(key);
            availableCartypes.addAll(this.getAvailableCarTypes(company,start, end));
        }
        return availableCartypes;
    }
    
    private Set<CarType> getAvailableCarTypes(ICarRentalCompany company, Date start, Date end){
        return company.getAvailableCarTypes(start, end);
    }

	@Override
	public CarType getCheapestCarType(Date start, Date end) {
		Set<CarType> availableCarTypes = getAllAvailableCarTypes(start, end);
		CarType cheapestCarType = null;
		for(CarType currentCarType: availableCarTypes){
			if(cheapestCarType == null){
				cheapestCarType = currentCarType;
			}
			else{
				if(currentCarType.getRentalPricePerDay()<cheapestCarType.getRentalPricePerDay()){
					cheapestCarType = currentCarType;
				}
			}
		}
		return cheapestCarType;
	}

    
    
    

    
    
}
