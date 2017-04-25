package modeselection.qlearning;

public class QActionInfo {
	private int visits = 0;
	private double qValue = 0.0;
	
	public int getVisits() {return visits;}
	
	public double getQ() {return qValue;}
	
	public void setQ(double newQ) {
		qValue = newQ;
		visits += 1;
	}
	
	@Override
	public String toString() {
		return String.format("Q:%6.2f;V:%d", qValue, visits);
	}
	
	@Override
	public int hashCode() {return toString().hashCode();}
	
	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
}
