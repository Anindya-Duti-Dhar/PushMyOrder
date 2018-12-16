package duti.com.pushmyorder.library;



public class SpinnerValue {
    public String displayText = "";
    public String valueText = "";
    public SpinnerValue(String displayText, String valueText)
    {
        this.displayText = displayText;
        this.valueText = valueText;
    }
    public String toString()
    {
        return( displayText );
    }


}
