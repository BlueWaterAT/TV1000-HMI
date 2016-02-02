package com.bwat.hmi.page;

import com.bwat.hmi.prg.Programmer;

import java.awt.BorderLayout;

public class ProgramPage extends HMIPage {
    public ProgramPage() {
        super("Path Program");
        setLayout(new BorderLayout());

        add(new Programmer(), BorderLayout.CENTER);
    }
}
