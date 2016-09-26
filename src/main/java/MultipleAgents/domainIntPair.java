package MultipleAgents;

import burlap.mdp.core.Domain;

/**
 * Created by noa on 25-Sep-16.
 */
public class domainIntPair {
    Domain domain;
    int num;

    public domainIntPair(Domain dom, int n)
    {
        domain = dom;
        num = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        domainIntPair other = (domainIntPair) o;
        if(num != other.num)
            return false;
        if(domain.equals(other.domain))
            return true;
        return false;
    }
}

