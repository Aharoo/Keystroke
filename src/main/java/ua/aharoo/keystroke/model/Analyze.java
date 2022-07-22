package ua.aharoo.keystroke.model;

public class Analyze {

    private String symbol;
    private double pressTime;
    private double timeBetweenPress;

    public Analyze() {}

    public Analyze(String symbol, double pressTime, double timeBetweenPress) {
        this.symbol = symbol;
        this.pressTime = pressTime;
        this.timeBetweenPress = timeBetweenPress;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPressTime() {
        return pressTime;
    }

    public void setPressTime(double pressTime) {
        this.pressTime = pressTime;
    }

    public double getTimeBetweenPress() {
        return timeBetweenPress;
    }

    public void setTimeBetweenPress(double timeBetweenPress) {
        this.timeBetweenPress = timeBetweenPress;
    }

    @Override
    public String toString() {
        return "Analyze{" +
                "symbol='" + symbol + '\'' +
                ", pressTime=" + pressTime +
                ", timeBetweenPress=" + timeBetweenPress +
                '}';
    }
}
