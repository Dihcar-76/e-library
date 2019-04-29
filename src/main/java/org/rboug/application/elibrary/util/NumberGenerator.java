package org.rboug.application.elibrary.util;

import java.io.Serializable;


public interface NumberGenerator extends Serializable {

    // ======================================
    // =          Business methods          =
    // ======================================

    String generateNumber();
}