package org.rboug.application.elibrary.util;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.logging.Logger;


@Loggable
@Interceptor
public class LoggingInterceptor implements Serializable {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private Logger logger;

    // ======================================
    // =          Business methods          =
    // ======================================

    @AroundInvoke
    private Object intercept(InvocationContext ic) throws Exception {
        logger.info(ic.getTarget().getClass().getName() + "." + ic.getMethod().getName());
        try {
            return ic.proceed();
        } finally {
            logger.fine(ic.getTarget().getClass().getName() + "." + ic.getMethod().getName());
        }
    }
}