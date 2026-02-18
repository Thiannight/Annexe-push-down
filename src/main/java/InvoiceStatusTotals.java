public class InvoiceStatusTotals {

    private Double totalPaid;
    private Double totalConfirmed;
    private Double totalDraft;

    public InvoiceStatusTotals(Double totalPaid, Double totalConfirmed, Double totalDraft) {
        this.totalPaid = totalPaid;
        this.totalConfirmed = totalConfirmed;
        this.totalDraft = totalDraft;
    }

    public Double getTotalPaid() {
        return totalPaid;
    }

    public Double getTotalConfirmed() {
        return totalConfirmed;
    }

    public Double getTotalDraft() {
        return totalDraft;
    }

    @Override
    public String toString() {
        return "total_paid = " + totalPaid + "\n" +
                "total_confirmed = " + totalConfirmed + "\n" +
                "total_draft = " + totalDraft;
    }
}
