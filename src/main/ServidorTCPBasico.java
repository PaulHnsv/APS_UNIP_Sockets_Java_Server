package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import config.ServerConfig;

public class ServidorTCPBasico {
	public static void main(String[] args) throws IOException {
		// inicia o servidor
		new ServidorTCPBasico(12345).executa();
	}

	private int porta;
	private List<PrintStream> clientes;
	private List<Socket> clientesLogados;
	public Socket cliente;

	public ServidorTCPBasico(int porta) {
		this.porta = porta;
		this.clientes = new ArrayList<PrintStream>();
		this.clientesLogados = new ArrayList<Socket>();
	}

	public void executa() throws IOException {
		ServerSocket servidor = new ServerSocket(this.porta);
		System.out.println("Porta 12345 aberta!");

		while (true) {
			// aceita um cliente
			this.cliente = servidor.accept();
			System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());
			this.clientesLogados.add(cliente);

			// adiciona saida do cliente à lista
			PrintStream ps = new PrintStream(cliente.getOutputStream());
			this.clientes.add(ps);

			// cria tratador de cliente numa nova thread
			ServerConfig tc = new ServerConfig(cliente.getInputStream(), this);
			new Thread(tc).start();
		}

	}

	public void distribuiMensagem(String msg, Socket clienteQueEnviou) {
		LocalDateTime dataAgora = LocalDateTime.now();

		// formatar a data
		DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu");
		String dataFormatada = formatterData.format(dataAgora);

		// formatar a hora
		DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
		String horaFormatada = formatterHora.format(dataAgora);

		// envia msg para todo mundo menos para o remetente
		for (Socket cliente : this.clientesLogados) {
			if (!cliente.equals(clienteQueEnviou)) {
				try {
					PrintStream mensagem = new PrintStream(cliente.getOutputStream());
					mensagem.println(msg + " enviado às: " + horaFormatada + " do dia " + dataFormatada);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
