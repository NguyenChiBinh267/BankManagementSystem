package com.bankmanagement;

import java.awt.*;

public class PrimaryButton extends StyledButton {
    public PrimaryButton(String text) {
        super(text, UIStyle.PRIMARY, UIStyle.PRIMARY_HOVER, Color.WHITE, UIStyle.PRIMARY);
    }
}
