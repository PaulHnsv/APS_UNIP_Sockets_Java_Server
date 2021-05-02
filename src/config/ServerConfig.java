package config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import main.ServidorTCPBasico;

public class ServerConfig implements Runnable {

	private List<ServerConfig> clientes = new ArrayList<ServerConfig>();;

	private Socket cliente;
	private DataInputStream in;
	private DataOutputStream out;
	private String boasVindas;

	public ServerConfig(Socket cliente, ServidorTCPBasico servidor) {
		// indica que determinado client entrou no chat
		this.boasVindas = "Cliente " + cliente.getInetAddress().getHostAddress() + " entrou no chat." + "\n";

		// lógica do envio e recebimento das mensagens por parte do server
		try {
			this.cliente = cliente;
			clientes.add(this);

			in = new DataInputStream(new BufferedInputStream(cliente.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(cliente.getOutputStream()));

			distribuiMensagem(this.boasVindas);

			// synchronized (out) {
			// out.writeUTF("Bem vindo ao chat " + cliente.getInetAddress().getHostAddress()
			// + "\n");
			// }
			// out.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		// lógica do chat para desligamento e manutenção de uma thread
		try {
			try {
				boolean sair = false;
				while (!sair) {
					String texto = in.readUTF();
					if ("SAIR".equals(texto)) {
						sair = true;
						clientes.remove(this);
						distribuiMensagem("Cliente " + cliente.getInetAddress().getHostAddress() + " saiu do chat.");
					} else {
						distribuiMensagem(texto);
					}
				}
			} finally {
				if (!this.cliente.isClosed()) {
					this.cliente.close();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Não conseguiu comunicar com o cliente.");
		}
	}

	// método que formata uma mensagem
	public void distribuiMensagem(String msg) {
		LocalDateTime dataAgora = LocalDateTime.now();

		// // formatar a data
		// DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu");
		// String dataFormatada = formatterData.format(dataAgora);

		// formatar a hora
		DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
		String horaFormatada = formatterHora.format(dataAgora);

		System.out.println("Enviar msg: " + msg);
		synchronized (clientes) {
			for (ServerConfig cliente : this.clientes) {

				try {
					synchronized (cliente.out) {
						if (!msg.equals(boasVindas)) {
							cliente.out.writeUTF(msg + " enviado às: " + horaFormatada + " por: "
									+ this.cliente.getInetAddress().getHostAddress() + "\n");
						} else {
							cliente.out.writeUTF(msg);
						}
					}
					cliente.out.flush();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
