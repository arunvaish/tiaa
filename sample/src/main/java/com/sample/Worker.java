package com.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Worker
{
    static Object monitor = new Object();

    static boolean one = true;
    static boolean two = false;
    static boolean three = false;
    static int itemNumber = 0;
    static int numOfBolt = 0;
    static int numberOfMachine = 0;
    static int requiredBolt = 2;
    static int requiredMachin = 1;
    static int assembleTime = 6 * 1000; // milli
    static int builtProducts = 0;
    static int timeTaken = 0;
    static List<String> items = new ArrayList<String>();

    public static void main(String[] args)
    {
        setItem(6, 3);
        /**
         * t1,t2,t3 are worker thread
         */
        Thread t1 = new Thread(new WorkerImpl(1));
        Thread t2 = new Thread(new WorkerImpl(2));
        Thread t3 = new Thread(new WorkerImpl(3));
        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * 
     * @param numberOfBolt
     * @param numberOfMachine
     * arranging alternative BOLT and MACHINE for belt
     */
    private static void setItem(int numberOfBolt, int numberOfMachine)
    {
        int total = numberOfBolt + numberOfMachine;
        String item[] = new String[total];
        int count = 0;
        int itemToSet = numberOfBolt;
        String itemString = "BOLT";
        boolean setAll = false;
        for (int i = 0; i < total; i++)
        {
            if ((i % 2 == 0 && count < itemToSet) || setAll)
            {
                count++;
                item[i] = itemString;
            }
            if (count > numberOfMachine)
            {
                setAll = true;
            }
        }
        for (int i = 0; i < item.length; i++)
        {
            if (item[i] == null)
            {
                item[i] = "MACHINE";
            }
        }
        items.addAll(Arrays.asList(item));
    }

    static class WorkerImpl implements Runnable
    {
        int workerId;

        static Object employee = new Object();

        public WorkerImpl(int workerId)
        {
            this.workerId = workerId;
        }

        public void run()
        {
            int totalItems = items.size();
            pickItemsFromBelt(totalItems);
        }

        private void pickItemsFromBelt(int totalItems)
        {
            try
            {
                while (true)
                {
                    if (itemNumber >= totalItems)
                    {
                        break;
                    }
                    synchronized (employee)
                    {
                        if (1 == workerId)
                        {
                            if (!one)
                            {
                                putItemToWorkShop();
                                itemNumber++;
                            }
                            else
                            {
                                one = false;
                                two = true;
                                three = false;
                                employee.notifyAll();
                            }
                        }
                        if (2 == workerId)
                        {
                            if (!two)
                            {
                                putItemToWorkShop();
                                itemNumber++;
                            }
                            else
                            {
                                one = false;
                                two = false;
                                three = true;
                                employee.notifyAll();
                            }
                        }
                        if (3 == workerId)
                        {
                            if (!three)
                            {
                                putItemToWorkShop();
                                itemNumber++;
                            }
                            else
                            {
                                one = true;
                                two = false;
                                three = false;
                                employee.notifyAll();
                            }
                        }
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }

        private void putItemToWorkShop() throws InterruptedException
        {
            if (numOfBolt >= requiredBolt && numberOfMachine >= requiredMachin)
            {
                builtProducts = builtProducts + 1;
                timeTaken = timeTaken + assembleTime;
                if(numOfBolt > requiredBolt)
                    numOfBolt = numOfBolt - requiredBolt;
                else 
                    numOfBolt = 0;
                if(numberOfMachine > requiredMachin)
                    numberOfMachine = numberOfMachine - requiredMachin;
                else
                    numberOfMachine = 0;
                System.out.println("Workers are taking rest for "+ (assembleTime/1000) + " seconds");
                Thread.sleep(assembleTime);
            }

            if (itemNumber >= items.size())
            {
                productsBuild();
                return;
            }
            if (items.get(itemNumber) == "BOLT")
            {
                numOfBolt = numOfBolt + 1;
            }
            else
            {
                numberOfMachine = numberOfMachine + 1;
            }
        }

        private void productsBuild()
        {
            System.out.println("Total Products = " + builtProducts);
            System.out.println("Total Time Taken = " + (timeTaken / 1000));

        }
    }
}
