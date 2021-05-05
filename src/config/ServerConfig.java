package config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import main.ServidorTCPBasico;

public class ServerConfig implements Runnable {

	private Socket cliente;
	private ServidorTCPBasico server;

	private DataInputStream in;
	private DataOutputStream out;

	private SocketAddress host;
	private String nome;
	private String boasVindas;
	private String mensagemSair;

	public ServerConfig(Socket cliente, ServidorTCPBasico servidor) {

		// lógica do envio e recebimento das mensagens por parte do server
		try {

			this.cliente = cliente;
			this.server = servidor;

			in = new DataInputStream(new BufferedInputStream(cliente.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(cliente.getOutputStream()));

			this.nome = in.readUTF();
			this.host = cliente.getLocalSocketAddress();

			// Atualiza o cliente existente ou adiciona um novo.
			if (server.contadorClientes.get(host) == null) {
				server.contadorClientes.put(host, nome);
			}

			// indica que determinado client entrou no chat
			this.boasVindas = "Cliente " + server.contadorClientes.get(host).toString() + " entrou no chat." + "\n";

			distribuiMensagem(this.boasVindas);

			synchronized (out) {
				out.writeUTF("Bem vindo ao chat " + server.contadorClientes.get(host).toString() + "\n");
			}
			out.flush();

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
					this.mensagemSair = "Cliente " + server.contadorClientes.get(host).toString() + " saiu do chat.";
					if ("SAIR".equals(texto)) {
						sair = true;
						ServidorTCPBasico.clientes.remove(this);
						distribuiMensagem(mensagemSair);
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
		for (ServerConfig cliente : ServidorTCPBasico.clientes) {
			try {
				if (msg.equals(boasVindas)) {
					cliente.out.writeUTF(msg);
				} else if (msg.equals(mensagemSair)) {
					cliente.out.writeUTF(msg + "\n");
				} else {
					cliente.out.writeUTF(msg + " Enviado às: " + horaFormatada + " Por: "
							+ server.contadorClientes.get(host).toString() + "\n");
				}
				cliente.out.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
