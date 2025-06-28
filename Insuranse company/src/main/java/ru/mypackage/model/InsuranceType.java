package ru.mypackage.model;

// Модель вида страхования
public class InsuranceType {
    private int id;
    private String name;
    private String agentPercentage;

    public InsuranceType(int id, String name, String agentPercentage) {
        this.id = id;
        this.name = name;
        this.agentPercentage = agentPercentage;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAgentPercentage() { return agentPercentage; }
    public void setAgentPercentage(String agentPercentage) { this.agentPercentage = agentPercentage; }
} 