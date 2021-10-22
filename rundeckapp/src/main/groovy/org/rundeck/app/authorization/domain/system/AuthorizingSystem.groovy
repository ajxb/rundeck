package org.rundeck.app.authorization.domain.system

import groovy.transform.CompileStatic
import org.rundeck.core.auth.access.Accessor
import org.rundeck.core.auth.access.AuthorizingResource
import org.rundeck.core.auth.access.Singleton
@CompileStatic
interface AuthorizingSystem extends AuthorizingResource<Singleton> {

    /**
     *
     * @return access via READ or ADMIN, OPS_ADMIN or APP_ADMIN
     */
    Accessor<Singleton> getReadOrAnyAdmin()

    /**
     *
     * @return access via READ ADMIN or OPS_ADMIN
     */
    Accessor<Singleton> getReadOrOpsAdmin()

    Accessor<Singleton> getConfigure()

    Accessor<Singleton> getOpsEnableExecution()

    Accessor<Singleton> getOpsDisableExecution()

}