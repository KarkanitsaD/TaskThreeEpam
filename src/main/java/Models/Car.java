package Models;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Car extends Thread{

    public boolean wantsSwap = false;
    private final ReentrantLock locker;
    private final int id;
    public long getId(){
        return id;
    }

    private CarParking carParking;
    public void setCarParking(CarParking carParking){
        this.carParking = carParking;
    }
    public CarParking getCarParking(){
        return this.carParking;
    }

    public Car(int id){
        System.out.println("Создание машины с id = " + id);
        this.id = id;
        locker = new ReentrantLock();
    }

    public int getWaitingTime(){
        return (int)(Math.random()*(3001-2000) + 2000);
    }

    public int getStandingTime(){
        return (int)(Math.random()*(10001-5000) + 5000);
    }

    @Override
    public void run(){
        System.out.println("Машина-"+id+" подъехала к парковке");
        System.out.println("Машина-"+id+" проверяет наличие свободного места");
        int waitingTime = getWaitingTime();
        if(!this.carParking.hasEmptySpace())
            System.out.println("Машина-"+id+" не нашла свободное место и будет ждать не более " + waitingTime + " мл.сек");
        try{
            if(this.carParking.carAmountControllerSemaphore.tryAcquire(waitingTime, TimeUnit.MILLISECONDS)) {
                locker.lock();

                try {
                    int spaceNumber = carParking.parkCar(this);
                    System.out.println("Машина-" + id + " припарковалась на месте " + spaceNumber);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    locker.unlock();
                }

                int standingTime = getStandingTime();
                System.out.println("Машина-" + id + " будет стоять " + standingTime + " мл.сек");
                Thread.sleep(standingTime/2);
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

                locker.lock();
                carParking.trySwap(this);
                locker.unlock();


                Thread.sleep(standingTime/2);

                locker.lock();
                try {
                    carParking.parkOffCar(this);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    locker.unlock();
                }
                System.out.println("Машина-" + id + " покидает стоянку");
                carParking.carAmountControllerSemaphore.release();
            }
            else {
                System.out.println("Машина-"+id+" не дождалась места и уехала");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }



    public void generateWantsSwapping(){
        int value = (int) (Math.random() * 5);
        wantsSwap = value == 0;
    }

}