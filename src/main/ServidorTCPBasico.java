package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import config.ServerConfig;

public class ServidorTCPBasico {

	private int porta;
	public Socket cliente;
	public ServerSocket servidor;

	public static ArrayList<ServerConfig> clientes = new ArrayList<>();
	public Map<SocketAddress, String> contadorClientes = new HashMap<SocketAddress, String>();

	static ExecutorService pool = Executors.newFixedThreadPool(4);

	public static void main(String[] args) throws IOException {

		// inicia o server
		new ServidorTCPBasico(12345).executa();
	}

	public ServidorTCPBasico(int porta) {
		this.porta = porta;

	}

	public void executa() throws IOException {
		this.servidor = new ServerSocket(this.porta);
		System.out.println("Porta 12345 aberta!");

		try {
			while (true) {

				// aceita um cliente
				this.cliente = servidor.accept();
				System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());

				// cria tratador de cliente numa nova thread
				ServerConfig server = new ServerConfig(cliente, this);
				clientes.add(server);

				pool.execute(server);
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
