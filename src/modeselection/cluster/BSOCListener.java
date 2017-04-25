package modeselection.cluster;

public interface BSOCListener {
	public void addingNode(int node);
	public void replacingNode(int target, int replacement);
}
