import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainFile 
{
	public static void main(String args[]) {
		
		ArrayList<Process> processList = new ArrayList<Process>();
		int nbrCores = 0;
		
	try {
		Scanner sc = new Scanner(new FileInputStream("input.txt"));
		
		while(sc.hasNextLine())
		{
			String temp = sc.next();
			if(temp.equals("numOfCPUs:"))
			{
				nbrCores = sc.nextInt();
				for(int i=0; i<4;i++)
					sc.nextLine();
			}
			else
			{
				String name = temp;
				int arriveTime = sc.nextInt();
				int burstTime = sc.nextInt();
				ArrayList<Integer> IOTimes = new ArrayList<Integer>();
				while(sc.hasNextInt())
					IOTimes.add(sc.nextInt());
				processList.add(new Process(name, arriveTime, burstTime, IOTimes));
			}
		}
		
		FCFS fcfs = new FCFS(processList, nbrCores);
		System.out.println();
		fcfs.print();
		//SJF sjf = new SJF(processList, nbrCores);
		//System.out.println();
		//sjf.print();
		//SRTF strf = new SRTF(processList, nbrCores);
		//System.out.println();
		//strf.print();
        //RR rr = new RR(processList, nbrCores);
        //System.out.println();
        //rr.print();
        
		sc.close();
		} 
	catch (FileNotFoundException e)
	{
		System.out.println("Error too bad");
	}

	} 
}
