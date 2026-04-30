package com.eremina.wallet.exception;

import jakarta.persistence.EntityNotFoundException;

public class WalletNotFoundException extends EntityNotFoundException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}
