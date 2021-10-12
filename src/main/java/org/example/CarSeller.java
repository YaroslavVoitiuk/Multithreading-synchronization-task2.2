package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarSeller {

    private static final int THREAD_SHORT_SLEEP = 1000;
    private static final int THREAD_LONG_SLEEP = 2000;
    private static final int ZERO = 0;
    private final CarShop carShop;

    public CarSeller(CarShop carShop){
        this.carShop = carShop;
    }

    Lock lock = new ReentrantLock(true);
    Condition carCondition = lock.newCondition();

    public void printBorder(){
        System.out.println("------------------");
    }

    public Car sellCar(){
        lock.lock();
        try {
            System.out.println("Покупатель " + Thread.currentThread().getName() + " хочет купить автомобиль");
            while (carShop.getCars().size() == ZERO){
                System.out.println("Машин нет в наличии...\nОжидаем поставку...");
                printBorder();
                carCondition.await();
            }
            Thread.sleep(THREAD_SHORT_SLEEP);
            System.out.println("Покупатель " + Thread.currentThread().getName() + " купил новую машину");
            printBorder();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return carShop.getCars().remove(ZERO);
    }

    public void receiveCar(){
        lock.lock();
        try {
            System.out.println("С завода пришла поставка, розгружаем....");
            Thread.sleep(THREAD_LONG_SLEEP);
            carShop.getCars().add(new Car());
            System.out.println("Автосалон получил машину ");
            printBorder();
            carCondition.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}