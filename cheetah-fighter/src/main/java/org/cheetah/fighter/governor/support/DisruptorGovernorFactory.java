package org.cheetah.fighter.governor.support;

import org.cheetah.fighter.governor.Governor;
import org.cheetah.fighter.governor.GovernorFactory;

/**
 * Created by Max on 2016/3/2.
 */
@Deprecated
public class DisruptorGovernorFactory implements GovernorFactory {
    @Override
    public Governor createGovernor() {
        return new DisruptorGovernor();
    }
}
