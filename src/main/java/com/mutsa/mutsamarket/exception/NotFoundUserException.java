package com.mutsa.mutsamarket.exception;

import com.mutsa.mutsamarket.exception.CustomException;

public class NotFoundUserException extends CustomException {

    private static final String MESSAGE = "해당 유저가 존재하지 않습니다.";

    public NotFoundUserException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
