package org.rboug.application.elibrary.util;

import java.util.Random;


@ThirteenDigits
public class IsbnGenerator implements NumberGenerator {

    // ======================================
    // =          Business methods          =
    // ======================================

    public String generateNumber() {
        return "13-" + new Random().nextInt();
    }
}