package Models;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Car extends Thread{

    //define is the car want to swap
    public boolean wantsSwap = false;

    //locker
    private final ReentrantLock locker;

    //id
    private final int id;
    public long getId(){
        return id;
    }

    //parking where car car park
    private CarParking carParking;
    public void setCarParking(CarParking carParking){
        this.carParking = carParking;
    }
    public CarParking getCarParking(){
        return this.carParking;
    }


    //constructor
    public Car(int id){
        System.out.println("Создание машины с id = " + id);
        this.id = id;
        locker = new ReentrantLock();
    }


    //randomize waiting time to wait parking space
    public int getWaitingTime(){
        return (int)(Math.random()*(3001-2000) + 2000);
    }


    //randomize standing time in paking
    public int getStandingTime(){
        return (int)(Math.random()*(10001-5000) + 5000);
    }


    //life of thread
    @Override
    public void run(){
        System.out.println("Машина-"+id+" подъехала к парковке");
        int waitingTime = getWaitingTime();

        //if car did not find free space it will wait some random time
        if(!this.carParking.hasEmptySpace()) {
            System.out.println("Машина-" + id + " не нашла свободное место и будет ждать не более " + waitingTime + " мл.сек");
        }

        try{

            //use semaphore to control amount of cars
            if(this.carParking.carAmountControllerSemaphore.tryAcquire(waitingTime, TimeUnit.MILLISECONDS)) {

                //park car
                locker.lock();

                try {
                    int spaceNumber = carParking.parkCar(this);
                    System.out.println("Машина-" + id + " припарковалась на месте " + spaceNumber);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    locker.unlock();
                }

                //when car parked, it will stand some time
                int standingTime = getStandingTime();
                System.out.println("Машина-" + id + " будет стоять " + standingTime + " мл.сек");

                //car stands
                Thread.sleep(standingTime/2);

                //car try to swap
                locker.lock();
                carParking.trySwap(this);
                locker.unlock();

                //car continue to stand
                Thread.sleep(standingTime/2);

                //drive away car
                locker.lock();
                try {
                    carParking.parkOffCar(this);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    locker.unlock();
                }
                System.out.println("Машина-" + id + " покидает стоянку");

                //free space to other car
                carParking.carAmountControllerSemaphore.release();
            }
            else {
                //if car wait too match it drive away
                System.out.println("Машина-"+id+" не дождалась места и уехала");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    //randomize if car wants swap
    public void generateWantsSwapping(){
        int value = (int) (Math.random() * 5);
        wantsSwap = value == 0;
    }

    //try to swap cars in other way
    /*if(wantsSwap) {

                    Thread.sleep(standingTime/2);

                    long waitingSwapTime = standingTime/2 + System.currentTimeMillis();
                    long waitingTimeAfterSwap = 0;
                    while(System.currentTimeMillis() <= waitingSwapTime){

                        if(carParking.trySwap(this)) {
                            waitingTimeAfterSwap = waitingSwapTime - System.currentTimeMillis();
                            break;
                        }

                    }
                    Thread.sleep(waitingTimeAfterSwap);
                }else{
                    Thread.sleep(standingTime);
                }*/

}