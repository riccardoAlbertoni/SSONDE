package test;
import SSONDEv1.DataSimilarity;
public class TestingLevinsteing {

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String functionName= "DataFunction.LevenshteinDistance";
		try{
			DataSimilarity  ist = (DataSimilarity ) Class.forName(functionName).newInstance();
			System.out.println(" sitten : kitten = " + ist.sim("sitten","kitten") );
			System.out.println(" kitten : kotten = " + ist.sim("kitten","kotten") );
			System.out.println(" kozten : kotten = " + ist.sim("kozten","kotten") );
			System.out.println(" rosettacode : raisethysword = " + ist.sim("rosettacode", "raisethysword") );
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
