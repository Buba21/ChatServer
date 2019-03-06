package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {


	private BufferedReader serverInput;


	public void start(String host, int portNumber) {
		Socket clientSocket;
		BufferedReader keyboardInput;
		PrintWriter outputClientToServer;
		System.out.println("metodo start");


		try {
			clientSocket = new Socket(host,portNumber);
			keyboardInput = new BufferedReader(new InputStreamReader(System.in));
			serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outputClientToServer = new PrintWriter(clientSocket.getOutputStream());
			newThread();
			System.out.println("criaççao dos sockets");

			while (true) { //por a receber o commando de /exit?????
				System.out.println("entrou no while do start");
				outputClientToServer.println(keyboardInput.readLine());

			}


		} catch (IOException error) {
			error.getMessage();
		}


	}

	private void newThread() {
		ExecutorService singleExecutor;
		singleExecutor = Executors.newSingleThreadExecutor();

		singleExecutor.submit(new Runnable() {

			@Override
			public void run() {
				String currentMessage;
				System.out.println("new thread run");

				while (true) {  //por a receber commando de /exit
					try {
						System.out.println("entrou no while dentro do run");
					currentMessage = serverInput.readLine();
						System.out.println(currentMessage);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
		});

	}

	public static void main(String[] args) {
		Client client = new Client();
		client.start("localhost",6000);
	}

}
