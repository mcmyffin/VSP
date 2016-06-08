/**
 * 
 */
package diceservice.Dice.controller;

import java.io.Serializable;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * created by Christian Zen
 * christian.zen@outlook.de
 * Date of creation: 26.04.2016
 */
public class DiceController implements Serializable {

	private static final long serialVersionUID = 1337L;
	@Expose
	private int number;

	public DiceController() {
		Random rnd = new Random();
		this.number = rnd.nextInt(6) + 1;
	}

	public int getNumber() {
		return this.number;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}