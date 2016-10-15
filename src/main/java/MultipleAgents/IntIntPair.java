package MultipleAgents;

/**
 * Created by noa on 25-Sep-16.
 */
public class IntIntPair {
    //Domain domainNum;
    int firstNum;
    int secondNum;

     public IntIntPair(int dom, int n)
    {
        firstNum = dom;
        secondNum = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntIntPair other = (IntIntPair) o;
        return (firstNum == other.firstNum && secondNum ==other.secondNum);
    }

    @Override
    public int hashCode()
    {
        return (2^firstNum)*(3^ secondNum);
    }


}

