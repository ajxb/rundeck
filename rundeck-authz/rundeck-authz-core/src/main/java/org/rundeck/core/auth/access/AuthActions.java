package org.rundeck.core.auth.access;

import java.util.Arrays;
import java.util.List;

/**
 * Represents access request allowed by any actions (OR).
 */
public interface AuthActions {

    /**
     * @return list of actions any are allowed
     */
    List<String> getAnyActions();

    /**
     * @param actions
     */
    default AuthActions or(AuthActions actions) {
        return AccessLevels.or(this, actions);
    }

    /**
     * @param actions
     */
    default AuthActions or(String... actions) {
        return AccessLevels.or(this, Arrays.asList(actions));
    }
}
