public class MyCounter {
    int counter;
    MyCounter(){/*counter=0;*/}

    public synchronized void inc_counter(){
        System.out.println("inc_counter");
        counter=counter+1;
    }
}
