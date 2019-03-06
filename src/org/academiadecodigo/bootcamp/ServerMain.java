package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain { // Ver quais propriedades podem passar para metodos locais

	private ServerSocket server;
	private ArrayList<ClientConnection> clientConnections;
	private ClientConnection clientConnection;


	private ServerMain(int portNumber) {

		try {
			server = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println("Error 1: Wrong port number, please input the correct port number");
		}
		clientConnections = new ArrayList<>();
	}

	private void serverCreation() {
		Socket clientSocket;

		try {
			clientSocket = server.accept();

			clientConnection = new ClientConnection(clientSocket);

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void newClient(ClientConnection clientConnection) {

		clientConnections.add(clientConnection);
	}


	private void newThread(ClientConnection clientConnection) {

		ExecutorService cachedPool = Executors.newCachedThreadPool();

		cachedPool.submit(clientConnection);

	}

	private void sendAll(String message) {

		for (ClientConnection client : clientConnections) {

			client.sendMessage(message);

		}

	}

	private void start() {
		while (true) {

			serverCreation();

			newThread(clientConnection);

			newClient(clientConnection);


		}
	}


	public static void main(String[] args) {
		ServerMain server = new ServerMain(6060);
		server.start();

	}


	public class ClientConnection implements Runnable {

		private String currentMessage;
		Socket clientSocket; // talvez possa ser local???
		private PrintWriter outputToClient;
		private BufferedReader inputFromClient;
		private String nickname;

		private ClientConnection(Socket clientSocket) {  //Implementar o nickname no construtor quando estiver a funcionar
			this.clientSocket = clientSocket;
			try {
				outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
				inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}


		}

		@Override
		public void run() {


			outputToClient.println("What is your nickname?");
			nickname = chooseName();
			menu();

			while (!clientSocket.isClosed()) {
				try {
					currentMessage = inputFromClient.readLine();
					if (currentMessage.startsWith("/")) {

						executeCommands(currentMessage);
					}

					System.out.println(nickname + ": " + currentMessage);

					sendAll(nickname + ": " + currentMessage);

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		private void sendMessage(String message) {
			outputToClient.println(message);

		}

		private String chooseName() {

			String name = null;

			try {

				name = inputFromClient.readLine();

				if (name.equals("") || name.equals(" ")) {
					name = "asshole";
					return name;
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
			return name;

		}

		public String getNickname() {
			return nickname;
		}

		private void executeCommands(String commands) {
			switch (commands) {
				case "/exit":
					closeConnection();
					break;
				case "/list":
					showAll();
					break;

			}
		}

		private void closeConnection() {

			try {
				inputFromClient.close();
				outputToClient.close();
				clientSocket.close();
				System.out.println(chooseName() + " has left the chat");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void menu() {
			outputToClient.println("//////////////////////////////////////////////////////////////////////////");
			outputToClient.println("////                     Welcome to Chateau d'Buba                    ////");
			outputToClient.println("//////////////////////////////////////////////////////////////////////////");
			outputToClient.println("//// This are the commands available to you, hope you enjoy the stay  ////");
			outputToClient.println("////           If you want to leave the chat use /quit                ////");
			outputToClient.println("////       If you want to know who is in the chat use /list           ////");
			outputToClient.println("//////////////////////////////////////////////////////////////////////////");


		}

		private void showAll() {
			String list = "";
			for (ClientConnection client : clientConnections) {
				list += client.getNickname() + "\n";

			}
			sendMessage(list);
		}
	}
}
