package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import config.ServerConfig;

public class ServidorTCPBasico {

	private int porta;
	public Socket cliente;

	public static void main(String[] args) throws IOException {

		// inicia o server
		new ServidorTCPBasico(12345).executa();
	}

	public ServidorTCPBasico(int porta) {
		this.porta = porta;

	}

	public void executa() throws IOException {
		ServerSocket servidor = new ServerSocket(this.porta);
		System.out.println("Porta 12345 aberta!");

		try {
			while (true) {
				// aceita um cliente
				this.cliente = servidor.accept();
				System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());

				// cria tratador de cliente numa nova thread
				ServerConfig server = new ServerConfig(cliente, this);
				new Thread(server).start();
			}
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			System.out.println("Não encontrou o host servidor.");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Não conseguiu abrir conexão com o host.");
		} finally {
			try {
				if (!servidor.isClosed()) {
					servidor.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("Erro ao fechar a conexão do socket servidor.");
			}
		}
	}

}
