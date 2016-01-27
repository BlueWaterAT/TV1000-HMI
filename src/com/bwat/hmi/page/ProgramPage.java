package com.bwat.hmi.page;

import com.bwat.hmi.prg.InteractiveJTable;

import java.awt.BorderLayout;

public class ProgramPage extends HMIPage {
    public ProgramPage() {
        super("Path Program");
        setLayout(new BorderLayout());

        add(new InteractiveJTable(), BorderLayout.CENTER);
    }
}
