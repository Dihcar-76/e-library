package org.rboug.application.elibrary.util;

import java.io.Serializable;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

public interface NumberGenerator extends Serializable {

    // ======================================
    // =          Business methods          =
    // ======================================

    String generateNumber();
}