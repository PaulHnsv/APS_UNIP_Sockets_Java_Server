package config;

import java.io.InputStream;
import java.util.Scanner;

import main.ServidorTCPBasico;

public class ServerConfig implements Runnable {

	private InputStream cliente;
	private ServidorTCPBasico servidor;

	public ServerConfig(InputStream cliente, ServidorTCPBasico servidor) {
		this.cliente = cliente;
		this.servidor = servidor;
	}

	public void run() {
		// quando chegar uma msg, distribui pra todos
		Scanner s = new Scanner(this.cliente);
		while (s.hasNextLine()) {
			servidor.distribuiMensagem(s.nextLine(), servidor.cliente);
		}
		s.close();
	}

}
