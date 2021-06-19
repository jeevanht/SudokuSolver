package solver;
import java.util.List;

public class Cage {
	
	private int cageTotal;
    private List<Index> indexList;

    public Cage() {
    }

    public Cage(int total, List<Index> indexList) {
        this.cageTotal = total;
        this.indexList = indexList;
    }

    public int getCageTotal() {
        return cageTotal;
    }

    public void setCageTotal(int total) {
        this.cageTotal = total;
    }

    public List<Index> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Index> indexList) {
        this.indexList = indexList;
    }

}
