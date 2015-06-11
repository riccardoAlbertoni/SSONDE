package DataFunction;
import SSONDEv1.DataSimilarity;
import SSONDEv1.Debug;
public class LevenshteinDistance implements DataSimilarity <String> {

	public double sim(String s, String t) {
		s = s.toLowerCase();
		t = t.toLowerCase();

		int [][] d=new int [s.length()][t.length()];

		//		for (int i=0;i<s.length();i++){
		//			for(int j=0;j<t.length();j++){
		//				d[i][j]=new int();
		//			}
		//		}

		for (int i=0; i<s.length();i++) {
			d[i][0]=i;
		}  
		for (int j=0; j<t.length();j++) {
			d[0][j]=j;
		}

		for(int j=1; j< t.length(); j++){
			for (int i=1; i< s.length(); i++){
				if (s.charAt(i)== t.charAt(j)){ 
					d[i][j]=d[i-1][j-1];
				}
				else
				{ 
					d[i][j]= Math.min(d[i-1][j]+1 ,d[i][j-1]+1); /* delection and Insertion*/
				}
			}
		}
		if (Debug.printDebug) {
			for (int i=0; i<s.length();i++ )
			{

				System.out.println(" ");
				for(int ii=0; ii< t.length();ii++)
					System.out.print(d[i][ii] + "\t");
			}
		}
		return d[s.length()-1][t.length()-1];//1-(d[s.length()-1][t.length()-1])/(double) (Math.max(s.length(),t.length()));

	}




}