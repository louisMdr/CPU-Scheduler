import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SRTF 
{
	
	 private static ArrayList<Process> ReadyList;
	 private static ArrayList<Integer>[] CPUList;
	 private static int[] clockCycle;
	
	 public SRTF(ArrayList<Process> process, int nbrCores) {
		 	//the processes are the initial values in it (shallow copy)
		 	ReadyList = (ArrayList<Process>) process.clone();

	        int amountOfProcesses = process.size();
	        
	        clockCycle = new int[nbrCores];
	        for(int i = 0; i<clockCycle.length;i++)
	        	clockCycle[i] = 0;
	        
	        //array of arraylist for each CPUS 
	        CPUList = new ArrayList[nbrCores];
	        //initialize each cpu in the list
	        for (int i = 0; i < nbrCores; i++) { 
	        	CPUList[i] = new ArrayList<Integer>(); 
	        }
	        
	        while(!ReadyList.isEmpty())
	        {  
		        for(int i=0; i<CPUList.length;i++)
		        {
		        	
			        //check if I'm the smallest clock time
			        int smallestclockTime = clockCycle[i];
			        for(int j=0;j<clockCycle.length;j++)
			        {
			        	if(clockCycle[j] < smallestclockTime)
			        		smallestclockTime = clockCycle[j];
			        }
			        
			        //check if im the smallest, if not I yield to the next CPU
			        if(clockCycle[i] > smallestclockTime)
			        	continue;
			      
		        	
		        	//This is to get the smallest arrival time process index in ready list
		        	int readyProcessInd;
		        	//check if list is really not empty (not enough to check in while condition)
			        if(!ReadyList.isEmpty())
			        	readyProcessInd = arrivalMinP(ReadyList, clockCycle[i]);
			        else
			        	break;
		        	
		        	//check if a process has arrived before or at my clock time
		        	if(clockCycle[i] >= ReadyList.get(readyProcessInd).getStartTime())
		        	{
		        		Process running = ReadyList.remove(readyProcessInd);
		        		//fetch the io list
		        		ArrayList<Integer> IOrequest = running.getIOTimes();
		        		
		        		//to store the first 1
		        		int IOrequestTime;
		        		
		        		//check if there's ios to do or not
		        		if(!IOrequest.isEmpty())
		        		{
		        			//IOrequestTime = IOrequest.remove(0);
		        			
		        			//This means we reached a io time - reset my application here
		        			if(execTimeWithIO(running) == 0)
		        			{
		        				running.setStartTime(clockCycle[i] + 2);
		        				IOrequest.remove(0);
		        				ReadyList.add(running);
		        			}
		        			else if(execTimeWithIO(running) > 0)
		        			{
		        				CPUList[i].add(running.getId());
		        				clockCycle[i] = CPUList[i].size();
		        				running.setStartTime(clockCycle[i]);
		        				running.setRemainingDuration(running.getRemainingDuration()-1);
		        				//put it at 0 because it came first (in case of ties)
		        				ReadyList.add(0,running);
		        			}
		        		}
		        		else
		        		{
		        			//check if there's nothing more to process
		        			if(running.getRemainingDuration() != 0)
		        			{
		        				//if no io just add 1 and reset start and remaining time
			        			CPUList[i].add(running.getId());
			        			clockCycle[i] = CPUList[i].size();
			        			running.setStartTime(clockCycle[i]);
			        			running.setRemainingDuration(running.getRemainingDuration()-1);
			        			//put it at 0 because it came first (in case of ties)
			        			ReadyList.add(0,running);
		        			}
		        			
		        		}
		        	}
		        	else
		        	{
		        		CPUList[i].add(0);
		        		clockCycle[i]++;
		        	}
		        	
		        }
	        }
	        
	        //finding the longest running cpu/total length
	        int totalTime = CPUList[0].size();
        	//find total length
        	for(int j=1;j<CPUList.length;j++)
        	{
        		if(CPUList[j].size() > totalTime)
        			totalTime = CPUList[j].size();
        	}
	        
	        //to sum all the turn times
	        int turnTimes = 0;
	        //Loop on each process for start/end of process execution times
	        for(Process pp : process)
	        {
	        	System.out.println(pp);
	        	int lastExecuteTime = CPUList[0].lastIndexOf(pp.getId());
	        	int firstExecuteTime = CPUList[0].indexOf(pp.getId());
	        	for(int k=1;k<CPUList.length;k++)
	        	{
	        		int tempLastTime = CPUList[k].lastIndexOf(pp.getId());
	        		int tempFirstTime = CPUList[k].indexOf(pp.getId());
	        		if(tempLastTime > lastExecuteTime)
	        			lastExecuteTime = tempLastTime;
	        		
	        		//-1 not kept as that's if its not found in a cpu
	        		if((tempFirstTime < firstExecuteTime) && (tempFirstTime != -1))
	        			firstExecuteTime = tempFirstTime;
	        		else if(firstExecuteTime == -1)
	        			firstExecuteTime = tempFirstTime;
	        	}
	        	//++ because indexing differences
	        	int turnTime = (++lastExecuteTime) - pp.getArrivalTime();
	        	//add to total turn times
	        	turnTimes += (turnTime - pp.getDuration());
	        	System.out.println("Turnaround Time: " + turnTime);
	        	int responseTime = firstExecuteTime - pp.getArrivalTime();
	        	System.out.println("Response Time: " + responseTime);
	        	System.out.println();
	        }
	        //Calculating cpu average waiting time
	        double avgWaitTime = (double)turnTimes/amountOfProcesses;
	        System.out.printf("Average Waiting Time: %.3f%%\n", avgWaitTime);
	        //Calculating cpu utilization
	        System.out.println("CPU Utilization: ");
	        double totalUtil;
	        for(int l = 0; l<CPUList.length;l++)
	        {
	        	System.out.print("CPU " + (l+1) + ": ");
	        	//number of cycles where something is being done
	        	int nbrofNon0s = 0;
	        	for(int nbr : CPUList[l])
	        	{
	        		if(nbr != 0)
	        			nbrofNon0s++;
	        	}
	        	totalUtil = ((double)nbrofNon0s / totalTime)*100;
	        	System.out.printf("%.3f%%\n", totalUtil);
	        }
	    }
	 
	 //To find which process is the shortest arrival time with switch if =
	 public int arrivalMinP(ArrayList<Process> process, int clockCycle) {
		 	//This will hold processes which start before the clockCycle
	        ArrayList<Process> smallStarts = new ArrayList<Process>(process.size());
	        
	        //add processes with the above condition
	        for(Process p : process)
	        {
	        	if(p.getStartTime() <=clockCycle)
	        		smallStarts.add(p);
	        }
	        
	        //initial index of process to pick
	        int minP = 0;
	        //check if there are small starting times or not
	        if(!smallStarts.isEmpty())
	        {
	        	for(int i=1;i<smallStarts.size();i++)
		        {
		        	if(execTimeWithIO(smallStarts.get(i)) < execTimeWithIO(smallStarts.get(minP)))
		        		minP = i;
		        }
	        	return process.indexOf(smallStarts.get(minP));
	        }
	        else
	        {
	        	for(int i=0; i<process.size();i++)
	        	{
	        		if(process.get(i).getArrivalTime() < process.get(minP).getArrivalTime())
	        			minP = i;
	        	}
	        	return minP;	
	        }
	        
	 }
	 
	 //calculates executing time of a process before io burst or before its completion
	 public int execTimeWithIO(Process pro)
	 {
		 int proCycleTime = pro.getRemainingDuration();
		 if(!pro.getIOTimes().isEmpty())
			 return ((pro.getRemainingDuration() + pro.getIOTimes().get(0)) % pro.getDuration());
		 else
			 return proCycleTime;
	 }

	 
	 public void print()
	 {
		 int i = 1;
		 for(ArrayList<Integer> List : CPUList)
		 {
			 System.out.println("CPU " + i + ":");
			 for(int num : List)
				 System.out.print(num + "-");
			 System.out.println();
			 i++;
		 }
	 }
	 
}
