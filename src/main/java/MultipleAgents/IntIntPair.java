package MultipleAgents;

/**
 * Created by noa on 25-Sep-16.
 */
public class IntIntPair {
    //Domain domainNum;
    int domainNum;
    int numVal;

   /* public IntIntPair(Domain dom, int n)
    {
        domainNum = dom;
        numVal = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntIntPair other = (IntIntPair) o;
        if(numVal != other.numVal)
            return false;
        if(domainNum.equals(other.domainNum))
            return true;
        return false;
    }*/

     public IntIntPair(int dom, int n)
    {
        domainNum = dom;
        numVal = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntIntPair other = (IntIntPair) o;
        return (domainNum == other.domainNum && numVal ==other.numVal);
    }

    @Override
    public int hashCode()
    {
        return (2^domainNum)*(3^numVal);
    }


}

