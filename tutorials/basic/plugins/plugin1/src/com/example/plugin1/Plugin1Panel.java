package com.example.plugin1;

import java.awt.Label;

import javax.swing.JPanel;

public class Plugin1Panel extends JPanel {

	private static final long serialVersionUID = -6420919219685347035L;

	public Plugin1Panel(){
		this.add(new Label("I am Panel 1!"));
	}
}
