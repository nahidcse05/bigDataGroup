import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.apache.commons.math3.stat.inference.*;
import org.apache.commons.math3.stat.correlation.*;

/**
 * @author Md. Mustafizur Rahman (mr4xb@virginia.edu)
 * Big Data in Mental Health
 * Group Project 
 */

public class analysis{

	HashMap<String, ArrayList<Double>> list = new HashMap<String, ArrayList<Double>>();
	HashMap<String, ArrayList<Double>> toplevelList = new HashMap<String, ArrayList<Double>>();
	double white [];
	double nonWhite[];
	int nonWhiteSize;
	int whiteSize;
	Random r;
	
	public analysis(){
		r = new Random();
	}
	
	
	double getMean(double [] data)
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.length;
    }

    double getVariance(double [] data)
    {
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/data.length;
    }

    double getStdDev(double [] data)
    {
        return Math.sqrt(getVariance(data));
    }
	
	//read the data file here
	public void readcsv(String fileName)
	{
		BufferedReader fileReader = null;
		try {

			fileReader = new BufferedReader(new FileReader(fileName));
			String line;
			int lineCounter = 0;
			
			while ((line = fileReader.readLine()) != null) {
				
				String infos[] = line.split(",");
				
				int age = Integer.parseInt(infos[6]);
				
				if(age<17 || age>24) continue;
				
				lineCounter++;
				String gender = infos[3];
				String ethnic = infos[4];
				
				int visitCount = Integer.parseInt(infos[9]);
				
				if(visitCount<2 || visitCount>6) continue;
				
				int studyPeriodCount = Integer.parseInt(infos[10]);
				int normalizer = 10;
				
				double normalizedCount = (double) visitCount/(normalizer*studyPeriodCount);
				//double normalizedCount = (double) visitCount;
				
				String key = ethnic+gender;
				if(!list.containsKey(key)){
					ArrayList<Double> values = new ArrayList<>();
					values.add(normalizedCount);
					list.put(key, values);
				}else{
					ArrayList<Double> values = list.get(key);
					values.add(normalizedCount);
					list.put(key, values);
				}
				
				
				if(!toplevelList.containsKey(ethnic)){
					ArrayList<Double> values = new ArrayList<>();
					values.add(normalizedCount);
					toplevelList.put(ethnic, values);
				}else{
					ArrayList<Double> values = toplevelList.get(ethnic);
					values.add(normalizedCount);
					toplevelList.put(ethnic, values);
				}
			}
			System.out.println("Total Data Point:" + lineCounter);
			System.out.println("White :" + toplevelList.get("White").size());
			System.out.println("White Male:" + list.get("WhiteM").size());
			
			System.out.println("AfricanAmerican :" + toplevelList.get("AfricanAmerican").size());
			System.out.println("Asian :" + toplevelList.get("Asian").size());
			System.out.println("Hispanic :" + toplevelList.get("Hispanic").size());
			
			whiteSize = toplevelList.get("White").size();
			nonWhiteSize = toplevelList.get("AfricanAmerican").size() + toplevelList.get("Asian").size()+ toplevelList.get("Hispanic").size(); 
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void compairWise(ArrayList<Double> aList, ArrayList<Double> bList, String aGroup, String bGroup){
		// a should be the small 
		// b should be the larger
		
		ArrayList<Integer> randomNumberList = new ArrayList<Integer>(); 
		double a [] = new double [aList.size()];
		double b [] = new double [aList.size()];
		
		for(int i = 0; i<aList.size();){
			a[i] = aList.get(i);
			while(true){
				int randomIndex = r.nextInt(bList.size());
				if(!randomNumberList.contains(randomIndex)){
					randomNumberList.add(randomIndex);
					b[i] = bList.get(randomIndex);
					break;
				}
			}
			i++;
		}
		
		randomNumberList.clear();
		
		/*for(int i = 0; i<aList.size();){
			
			System.out.println("a"+a[i]+"b"+b[i]);
			
		}*/
		
		int df = aList.size()+ aList.size() - 2;
		TTest ttest = new TTest();
		System.out.println("-----------------------------");
		System.out.println(bGroup+" Vs "+ aGroup);
		
		System.out.println(aGroup+" Mean:"+ getMean(a));
		System.out.println(aGroup+" Standard Deviation:"+ getStdDev(a));
		System.out.println(bGroup+" Mean:"+ getMean(b));
		System.out.println(bGroup+" Standard Deviation:"+ getStdDev(b));
		
		System.out.println("t_statistic:"+ ttest.t(a, b));
		System.out.println("p value:"+ ttest.tTest(a, b));
		System.out.println("DF:"+ df);
		double corr = new PearsonsCorrelation().correlation(a, b);
		System.out.println("Correlation: "+ corr);
		System.out.println("\n\n");
	}

	public void compare(){
		
		
		compairWise(toplevelList.get("Asian"), toplevelList.get("White"), "Asian", "White");
		compairWise(toplevelList.get("AfricanAmerican"), toplevelList.get("White"), "AfricanAmerican", "White");
		compairWise(toplevelList.get("Hispanic"), toplevelList.get("White"), "Hispanic", "White");
		
		compairWise(list.get("AsianM"), list.get("WhiteM"), "Asian Male", "White Male");
		compairWise(list.get("AsianF"), list.get("WhiteF"), "Asian Female", "White Female");
		
		compairWise(list.get("AfricanAmericanM"), list.get("WhiteM"), "AfricanAmerican Male", "White Male");
		compairWise(list.get("AfricanAmericanF"), list.get("WhiteF"), "AfricanAmerican Female", "White Female");
		
		compairWise(list.get("HispanicM"), list.get("WhiteM"), "Hispanic Male", "White Male");
		compairWise(list.get("HispanicF"), list.get("WhiteF"), "Hispanic Female", "White Female");
		
		white = new double [nonWhiteSize];
		nonWhite = new double [nonWhiteSize];
		ArrayList<Integer> randomNumberList = new ArrayList<Integer>(); 
		
		for(int i = 0; i<nonWhiteSize;){
			
			while(true){
				int randomIndex = r.nextInt(whiteSize);
				if(!randomNumberList.contains(randomIndex)){
					randomNumberList.add(randomIndex);
					white[i] = toplevelList.get("White").get(randomIndex);
					break;
				}
			}
			i++;
		}
		
		
		/*for(int i = 0; i<nonWhiteSize;){
			System.out.println("Random Number:"+randomNumberList.get(i)+", white["+i+"]: "+ white[i]);
			i++;
		}*/
		
		
		for(int i=0; i<toplevelList.get("AfricanAmerican").size(); i++){
			nonWhite[i] = toplevelList.get("AfricanAmerican").get(i);
		}
		
		int j = 0;
		for(int i=toplevelList.get("AfricanAmerican").size(); i<toplevelList.get("AfricanAmerican").size()+toplevelList.get("Asian").size(); i++){
			nonWhite[i] = toplevelList.get("Asian").get(j);
			j++;
		}
		
		j = 0;
		for(int i=toplevelList.get("AfricanAmerican").size()+toplevelList.get("Asian").size(); i<nonWhiteSize; i++){
			nonWhite[i] = toplevelList.get("Hispanic").get(j);
			j++;
		}
		
		int df = (int)(nonWhiteSize+nonWhiteSize - 2);
		TTest ttest = new TTest();
		System.out.println("-----------------------------");
		System.out.println("White Vs Non-White");
		
		System.out.println("White Mean:"+ getMean(white));
		System.out.println("White Standard Deviation:"+ getStdDev(white));
		System.out.println("Non-White Mean:"+ getMean(nonWhite));
		System.out.println("Non-White Standard Deviation:"+ getStdDev(nonWhite));
		
		System.out.println("t_statistic:"+ ttest.t(white, nonWhite));
		System.out.println("p value:"+ ttest.tTest(white, nonWhite));
		System.out.println("DF:"+ df);
		double corr = new PearsonsCorrelation().correlation(white, nonWhite);
		System.out.println("Correlation: "+ corr);
		
		
		
	}
	
	public static void main(String[] args){
		analysis ans = new analysis();
		String fileName = "./data/mainData.csv";
		ans.readcsv(fileName);
		ans.compare();
		System.out.println();
	}
}
