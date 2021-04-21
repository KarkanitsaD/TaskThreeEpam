import Models.Car;
import Models.CarParking;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        CarParking carParking = new CarParking(5);
        for(int i = 1; i <= 30; i++){
            Car car = new Car(i);
            car.setCarParking(carParking);
            car.generateWantsSwapping();
            car.start();
            Thread.sleep(700);
        }
    }
}


