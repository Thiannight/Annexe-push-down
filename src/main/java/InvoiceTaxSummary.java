public class InvoiceTaxSummary {

    private int id;
    private Double totalHt;
    private Double totalTva;
    private Double totalTtc;

    public InvoiceTaxSummary(int id, Double totalHt, Double totalTva, Double totalTtc) {
        this.id = id;
        this.totalHt = totalHt;
        this.totalTva = totalTva;
        this.totalTtc = totalTtc;
    }

    public int getId() {
        return id;
    }

    public Double getTotalHt() {
        return totalHt;
    }

    public Double getTotalTva() {
        return totalTva;
    }

    public Double getTotalTtc() {
        return totalTtc;
    }

    @Override
    public String toString() {
        return id +
                " | HT " + totalHt +
                " | TVA " + totalTva +
                " | TTC " + totalTtc;
    }
}
