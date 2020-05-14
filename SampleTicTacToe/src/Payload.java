import java.io.Serializable;

public class Payload implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String message;
    private boolean isOn = false;
    private int number;
    private PayloadType payloadType;

    public void IsOn(boolean isOn)
    {
        this.isOn = isOn;
    }

    public boolean IsOn()
    {
        return this.isOn;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setPayloadType(PayloadType payloadType)
    {
        this.payloadType = payloadType;
    }

    public PayloadType getPayloadType()
    {
        return this.payloadType;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return this.number;
    }

    @Override
    public String toString()
    {
        return String.format("Type[%s], isOn[%s], Number[%s], Message[%s]",
                getPayloadType().toString(), IsOn()+"", getNumber(), getMessage());
    }
}
