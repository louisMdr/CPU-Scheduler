import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class RR 
{
	
	 private static ArrayList<Process> ReadyList;
	 private static ArrayList<Integer>[] CPUList;
	 private static int[] clockCycle;
	
	 public RR(ArrayList<Process> process, int nbrCores) {
		 	//the processes are the initial values in it (shallow copy)
		 	ReadyList = (ArrayList<Process>) process.clone();
		 	
		 	//Read the amount of quantum for rr
		 	Scanner q = new Scanner(System.in);
			System.out.print("Quantum q for Round Robin: ");
			int quantum = q.nextInt();
		 	
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
			        	readyProcessInd = arrivalMinP(ReadyList);
			        else
			        	break;
		        	
			        if(clockCycle[i] >= ReadyList.get(readyProcessInd).getStartTime())
		        	{
		        		Process running = ReadyList.remove(readyProcessInd);
		        		//fetch the io list
		        		ArrayList<Integer> IOrequest = running.getIOTimes();
		        		
		        		//to store the first 1
		        		int IOrequestTime;
	        			int runTime;
	        			
		        		//check if there's ios to do or not
		        		if(!IOrequest.isEmpty())
		        		{

		        			if(quantum > execTimeWithIO(running))
		        			{
		        				runTime = execTimeWithIO(running);
		        				IOrequestTime = IOrequest.remove(0);
		        				//setting the new burst-time left of that process
		        				for(int j=0; j<runTime;j++)
			        				CPUList[i].add(running.getId());
		        				clockCycle[i] = CPUList[i].size();
		        				running.setRemainingDuration(running.getRemainingDuration() - runTime);
			        			running.setStartTime(clockCycle[i] + 2);
			        			ReadyList.add(running);
		        			}
		        			else
		        			{
		        				runTime = quantum;
		        				for(int j=0; j<runTime;j++)
			        				CPUList[i].add(running.getId());
		        				clockCycle[i] = CPUList[i].size();
		        				running.setRemainingDuration(running.getRemainingDuration() - runTime);
			        			running.setStartTime(clockCycle[i]);
		        				ReadyList.add(running);
		        			}
		        		}
		        		else
		        		{
		        			if(quantum > execTimeWithIO(running))
		        			{
		        				runTime = running.getRemainingDuration();
		        				//setting the new burst-time left of that process
		        				for(int j=0; j<runTime;j++)
			        				CPUList[i].add(running.getId());
		        				clockCycle[i] = CPUList[i].size();
		        			}
		        			else
		        			{
		        				runTime = quantum;
		        				for(int j=0; j<runTime;j++)
			        				CPUList[i].add(running.getId());
		        				clockCycle[i] = CPUList[i].size();
		        				running.setRemainingDuration(running.getRemainingDuration() - runTime);
			        			running.setStartTime(clockCycle[i]);
		        				ReadyList.add(running);
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
	 
	//To find which process is the shortest arrival time without switch if =
		 public int arrivalMinP(ArrayList<Process> process) {
			 //Process at position 0
		        int minP = 0;
		        for (Process p : process) {
		            if (p.getStartTime() < process.get(minP).getStartTime()) {
		                minP = process.indexOf(p);
		            }
		            //to check in case there's a conflict for which to pick
		            else if(p.getStartTime() == process.get(minP).getStartTime() && p.getArrivalTime() < process.get(minP).getArrivalTime())
		            {
		            	minP = process.indexOf(p);	
		            }
		            
		        }
		        return minP;
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
