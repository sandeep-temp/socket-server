package com.sk.test;

import java.io.IOException;

public class Test {

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		try {
			long count = 0;
			while (true) {
				String command = "curl -X GET http://localhost:8123/api/request?timeout=1&connId="
						+ count;
				Runtime.getRuntime().exec(command);

				count++;
				long currentRunningTime = System.nanoTime() - startTime;
				System.out.println((double)(count*100000000) / (double)currentRunningTime);
				Thread.sleep(0, 10000);
			}
		} catch (IOException e) {

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
