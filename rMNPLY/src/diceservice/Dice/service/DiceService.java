package diceservice.Dice.service;

import static spark.Spark.*;

import Common.Abstract.MainAbstract;
import YellowPage.RegistrationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import diceservice.Dice.util.IpFinder;
import diceservice.Dice.controller.DiceController;
import util.ServiceTemplateBank;

import javax.imageio.spi.RegisterableService;
import javax.servlet.Registration;

/**
 * created by Christian Zen christian.zen@outlook.de Date of creation:
 * 26.04.2016
 */
public class DiceService extends MainAbstract{

	public static int port = 5006;
	public static String ip = IpFinder.getIP();
	private static String name = "group_42";
	private static String description = "Dice Service";
	private static String service = "dice";
	public static String URL = "http://" + ip + ":" + port;
	public static String URLService = URL + "/dice";

	public DiceService(int port, String ip, String name, String description, String service, String urlService) {
		super(port, ip, name, description, service, urlService);
	}


	public static void main(String[] args) {
		port(port);
		Gson gson = new Gson();

		DiceService main = new DiceService(port,ip,name,description,service,URLService);
		RegistrationService service = new RegistrationService(main);
		service.startRegistration();

		// find and remove old Dice Services

		// Gives a single dice roll
		get("/dice", (req, res) -> {
			res.status(200);
			res.header("Content-Type", "application/json");
			Gson gsonB = new GsonBuilder().create();
			return gsonB.toJson(new DiceController(), DiceController.class);
		});

		exception(Exception.class, (ex,req,res) -> {
			res.status(500);
			ex.printStackTrace();
			res.body("Unerwarteter FEHLER");
		});
	}
}
