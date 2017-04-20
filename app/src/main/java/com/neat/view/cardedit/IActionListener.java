package com.neat.view.cardedit;

public interface IActionListener {
        void onActionComplete(CreditCardFragment fragment);
        void onEdit(CreditCardFragment fragment, String edit, boolean validValue);

    }