package Models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class CarParking  {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarParking.class);

    //amount of parking space
    private final int amountOfParkingSpaces;
    public int getAmountOfParkingSpaces(){
        return amountOfParkingSpaces;
    }

    //semaphore to control amount of cars
    public final Semaphore carAmountControllerSemaphore;

    //parking spaces
    private final List<Car> parkingSpaces;

    //constructor
    public CarParking(int amountOfParkingSpaces){
        this.amountOfParkingSpaces = amountOfParkingSpaces;
        carAmountControllerSemaphore = new Semaphore(this.amountOfParkingSpaces, false);
        parkingSpaces = new ArrayList<Car>(this.amountOfParkingSpaces);
        formParkingSpaces(this.amountOfParkingSpaces);
    }


    //parking spaces formation
    private void formParkingSpaces(int amountOfParkingSpaces){

        for(int i = 0; i < amountOfParkingSpaces; i++){
            parkingSpaces.add(null);
        }

    }


    //check is there empty space
    public boolean hasEmptySpace(){
        return parkingSpaces.contains(null);
    }

    //park car if possible return space number else return -1
    public int parkCar(Car car){

        for(int i = 0; i < amountOfParkingSpaces; i++){
            if(parkingSpaces.get(i) == null){
                parkingSpaces.set(i, car);

                LOGGER.error("Car - " + car.getId() + " parked");

                return i+1;
            }
        }

        LOGGER.error("Car - " + car.getId() + " did not park");

        return -1;
    }

    //drive away car
    public void parkOffCar(Car car){

        LOGGER.error("Car - " + car.getId() + " park off");

        for(int i = 0; i < amountOfParkingSpaces; i++){
            if(parkingSpaces.get(i) == car){
                parkingSpaces.set(i, null);
            }
        }
    }

    //try to swap cars, if possible return true, else return false
    public boolean trySwap(Car car){

        LOGGER.error("Car - " + car.getId() + " try swap");

        for(int i = 0; i < parkingSpaces.size(); i++){
            if(parkingSpaces.get(i)!=null && car.equals(parkingSpaces.get(i))){
                if(i > 0 && parkingSpaces.get(i-1)!=null && parkingSpaces.get(i-1).wantsSwap){
                    Collections.swap(parkingSpaces, i, i-1);
                    parkingSpaces.get(i).wantsSwap = false;
                    parkingSpaces.get(i-1).wantsSwap = false;
                    System.out.println("СМЕНА мест. Машина-" + parkingSpaces.get(i).getId() + ", Машина-" + parkingSpaces.get(i-1).getId());
                    return true;
                }
                if(i+1 < amountOfParkingSpaces && parkingSpaces.get(i+1)!=null && parkingSpaces.get(i+1).wantsSwap){
                    Collections.swap(parkingSpaces, i, i+1);
                    parkingSpaces.get(i).wantsSwap = false;
                    parkingSpaces.get(i+1).wantsSwap = false;
                    System.out.println("СМЕНА мест. Машина-" + parkingSpaces.get(i).getId() + ", Машина-" + parkingSpaces.get(i+1).getId());
                    return true;
                }
                return false;
            }
        }
        return false;
    }


}
