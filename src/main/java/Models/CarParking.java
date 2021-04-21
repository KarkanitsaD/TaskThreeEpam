package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class CarParking  {
    private final int amountOfParkingSpaces;
    public final Semaphore carAmountControllerSemaphore;
    private final List<Car> parkingSpaces;

    public int getAmountOfParkingSpaces(){
        return amountOfParkingSpaces;
    }

    public CarParking(int amountOfParkingSpaces){
        this.amountOfParkingSpaces = amountOfParkingSpaces;
        carAmountControllerSemaphore = new Semaphore(this.amountOfParkingSpaces, false);
        parkingSpaces = new ArrayList<Car>(this.amountOfParkingSpaces);
        formParkingSpaces(this.amountOfParkingSpaces);
    }

    private void formParkingSpaces(int amountOfParkingSpaces){
        for(int i = 0; i < amountOfParkingSpaces; i++){
            parkingSpaces.add(null);
        }
    }

    public boolean hasEmptySpace(){
        return parkingSpaces.contains(null);
    }

    public int parkCar(Car car){
        for(int i = 0; i < amountOfParkingSpaces; i++){
            if(parkingSpaces.get(i) == null){
                parkingSpaces.set(i, car);
                return i+1;
            }
        }
        return -1;
    }

    public void parkOffCar(Car car){
        for(int i = 0; i < amountOfParkingSpaces; i++){
            if(parkingSpaces.get(i) == car){
                parkingSpaces.set(i, null);
            }
        }
    }

    public boolean trySwap(Car car){
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
