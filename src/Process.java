import java.util.ArrayList;

public class Process implements Comparable<Process> {
	
	private String name;
    private int id;
    private int arrivalTime;
    private int duration;
    private int remainingDuration;
    private ArrayList<Integer> IOTimes;
    private int startTime;
    private int[] printIOTimes;
    

    public Process(String name, int arrivalTime, int duration, ArrayList<Integer> iotimes) {
        this.name = name;
        //ID used for the Gantt chart
        this.id = Integer.parseInt(name.substring(1)) + 1;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.remainingDuration = duration;
        this.IOTimes = iotimes;
        this.remainingDuration = duration;
        this.startTime = arrivalTime;
        this.printIOTimes = toArray(iotimes);
    }


    public int compareTo(Process aux) {
        if ((this.arrivalTime < aux.getArrivalTime() || this.arrivalTime == aux.getArrivalTime())
                && (this.getDuration() < aux.getDuration())) {
            return -1;
        } else if ((this.arrivalTime > aux.getArrivalTime() || (this.arrivalTime == aux.getArrivalTime())
                && this.getDuration() > aux.getDuration())) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getRemainingDuration() {
        return remainingDuration;
    }
    
    public ArrayList<Integer> getIOTimes()
    {
    	return IOTimes;
    }
    
    public int getStartTime()
    {
    	return startTime;
    }
    
    
    public void setStartTime(int startTime)
    {
    	this.startTime = startTime;
    }

    public void setRemainingDuration(int remainingDuration) {
        this.remainingDuration = remainingDuration;
    }
    
    public static int[] toArray(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
    
    public String toString()
    {
    	String result = "Name: " + name + "\nArrival Time: " + arrivalTime + "\nBurst Time: " + duration + "\nIO Times: ";
    	for(int num : printIOTimes)
    	    result += num + " ";
    	return result;
    }

}
