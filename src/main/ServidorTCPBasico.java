package main;

import java.net.*;
import java.util.Date;
import java.util.Scanner;

import java.io.*;

public class ServidorTCPBasico {
	public static void main(String[] args) throws IOException {
		ServerSocket servidor = new ServerSocket(12345);
		System.out.println("Porta 12345 aberta!");

		Socket cliente = servidor.accept();
		System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());

		Scanner mensagens = new Scanner(cliente.getInputStream());

		// se qualquer cliente parar de mandar mensagens o loop vai ser interrompido.
		while (mensagens.hasNextLine()) {
			Date data = new Date();

			System.out.println(cliente.getInetAddress() + " " + data.toString());
			System.out.println(mensagens.nextLine());
		}

		mensagens.close();
		servidor.close();
		cliente.close();
	}
}