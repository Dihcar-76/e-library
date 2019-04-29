package org.rboug.application.elibrary.util;

import java.util.Random;


@EightDigits
public class IssnGenerator implements NumberGenerator {

    // ======================================
    // =          Business methods          =
    // ======================================

    public String generateNumber() {
        return "8-" + new Random().nextInt() / 1000;
    }
}