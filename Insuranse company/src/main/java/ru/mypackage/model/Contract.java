package ru.mypackage.model;

// Модель страхового договора
public class Contract {
    private int id;
    private int idBranch;
    private int idClient;
    private int idAgent;
    private int idInsuranceType;
    private String dateSigned;
    private String insuranceAmount;
    private String tariffRate;
    private String branchName;
    private String agentFio;
    private String insuranceTypeName;

    public Contract(int id, int idBranch, int idClient, int idAgent, int idInsuranceType, String dateSigned, String insuranceAmount, String tariffRate, String branchName, String agentFio, String insuranceTypeName) {
        this.id = id;
        this.idBranch = idBranch;
        this.idClient = idClient;
        this.idAgent = idAgent;
        this.idInsuranceType = idInsuranceType;
        this.dateSigned = dateSigned;
        this.insuranceAmount = insuranceAmount;
        this.tariffRate = tariffRate;
        this.branchName = branchName;
        this.agentFio = agentFio;
        this.insuranceTypeName = insuranceTypeName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdBranch() { return idBranch; }
    public int getIdClient() { return idClient; }
    public int getIdAgent() { return idAgent; }
    public int getIdInsuranceType() { return idInsuranceType; }
    public String getDateSigned() { return dateSigned; }
    public String getInsuranceAmount() { return insuranceAmount; }
    public String getTariffRate() { return tariffRate; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getAgentFio() { return agentFio; }
    public void setAgentFio(String agentFio) { this.agentFio = agentFio; }
    public String getInsuranceTypeName() { return insuranceTypeName; }
    public void setInsuranceTypeName(String insuranceTypeName) { this.insuranceTypeName = insuranceTypeName; }

    // Возвращает строку с зарплатой агента для договора
    public String getSalaryString() {
        try {
            double amount = Double.parseDouble(insuranceAmount.replace(",", "."));
            double rate = Double.parseDouble(tariffRate.replace(",", "."));
            ru.mypackage.model.InsuranceType type = ru.mypackage.model.InsuranceTypesTable.getInstance().findById(idInsuranceType);
            double percent = 0;
            if (type != null) {
                percent = Double.parseDouble(type.getAgentPercentage().replace(",", "."));
            }
            double salary = amount * rate * percent / 100.0;
            return String.format("%.2f", salary);
        } catch (Exception e) {
            return "-";
        }
    }

    // Возвращает строку со страховым платежом (страховая сумма * тарифная ставка)
    public String getPaymentString() {
        try {
            double amount = Double.parseDouble(insuranceAmount.replace(",", "."));
            double rate = Double.parseDouble(tariffRate.replace(",", "."));
            double payment = amount * rate;
            return String.format("%.2f", payment);
        } catch (Exception e) {
            return "-";
        }
    }
} 