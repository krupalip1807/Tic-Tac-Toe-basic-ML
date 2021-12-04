import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TicTacToe {

	static String[] board;
	static String turn;
	static int count = 0;
	static String firstTurn;
	static boolean Xcount = false;
	static String file = "";

	static List<Integer> steps = new ArrayList<>();

	static String checkWin() {
		for (int a = 0; a < 8; a++) {
			String line = null;

			switch (a) {
			case 0:
				line = board[0] + board[1] + board[2];
				break;
			case 1:
				line = board[3] + board[4] + board[5];
				break;
			case 2:
				line = board[6] + board[7] + board[8];
				break;
			case 3:
				line = board[0] + board[3] + board[6];
				break;
			case 4:
				line = board[1] + board[4] + board[7];
				break;
			case 5:
				line = board[2] + board[5] + board[8];
				break;
			case 6:
				line = board[0] + board[4] + board[8];
				break;
			case 7:
				line = board[2] + board[4] + board[6];
				break;
			}
			// For X winner
			if (line.equals("XXX")) {
				return "X";
			}

			// For O winner
			else if (line.equals("OOO")) {
				return "O";
			}
		}

		for (int a = 0; a < 9; a++) {
			if (Arrays.asList(board).contains(String.valueOf(a + 1))) {
				break;
			} else if (a == 8) {
				return "draw";
			}
		}

		// Enter X OR O at same place on board..
		if (turn.equals("X")) {
			if (!Xcount) {
				Xcount = true;
				System.out.println("Computer's turn:");
			}
		} else {
			System.out.println("Your turn; enter a slot number to place:");
		}
		return "";
	}

	static void printBoard() {
		System.out.println("|   |   |   |");
		System.out.println("| " + board[0] + " | " + board[1] + " | " + board[2] + " |");
		System.out.println("|           |");
		System.out.println("| " + board[3] + " | " + board[4] + " | " + board[5] + " |");
		System.out.println("|           |");
		System.out.println("| " + board[6] + " | " + board[7] + " | " + board[8] + " |");
		System.out.println("|   |   |   |");
	}

	static String firstTurnDecide() {
		Scanner in = new Scanner(System.in);
		System.out.println("Who will take first turn?\nEnter 1 for Computer\nEnter 2 for yourself.");
		int turn = in.nextInt();
		String tr = "X";

		switch (turn) {
		case 1:
			System.out.println("Computer's turn -");
			tr = "X";
			break;
		case 2:
			System.out.println("Your turn -");
			tr = "O";
			break;
		default:
			System.out.println("Invalid entry -");
			tr = firstTurnDecide();
		}
		return tr;
	}

	static int playComputer() {
		// w to check win status.
		// put to check turn status.
		boolean w = false, put = false;
		String isWin = "---";
		int numInput = -1;
		// int sensoredInt = -1;
		List<Integer> sensoredInts = new ArrayList<>();
		List<Integer> successInts = new ArrayList<>();

		// Check if there is any place left to fill that can end up helping Computer to
		// win?
		for (int a = 0; a < 9; a++) {
			if (Arrays.asList(board).contains(String.valueOf(a + 1))) {
				board[a] = "X";
				isWin = checkWin();
				if (isWin.equalsIgnoreCase("X")) {
					w = true;
					put = true;
					numInput = a + 1;
					break;
				} else {
					board[a] = String.valueOf(a + 1);
					put = false;
					isWin = "";
				}
			}
		}

		// Machine Learning
		if (!w && !put && steps.size() > 0) {
			try {
				File myObj = new File(file);
				Scanner myReader = new Scanner(myObj);
				boolean isMatch = false; // To match current combination of inputs with list of previous inputs
				int stepCounter = 0; // To keep track of Current inputs check while matching it with previous inputs

				while (myReader.hasNextLine()) { // Read file
					String data = myReader.nextLine(); // Fetch line
					String[] refSteps = data.split(" ");
					stepCounter = 0;
					// Check for data in WIN condition - to follow
					// if(refSteps[(refSteps.length)-1] == "999") {

					for (int i = 0; i < steps.size() && i < refSteps.length; i++) { // Compare steps from line
						String step = String.valueOf(steps.get(i));

						if (step.equals(refSteps[i])) {
							stepCounter++;
							continue;
						} else {
							isMatch = false;
							break;
						}
					}

					if (stepCounter == steps.size()) { // If all current steps are visited and all are matched
						isMatch = true;
						if (refSteps.length > steps.size()) {
							int nextInt = Integer.valueOf(refSteps[steps.size()]);

							System.out.println("nextInt :" + nextInt);

							if (refSteps[(refSteps.length) - 1].equals("999")) {
								successInts.add(nextInt);
							} else if (refSteps[(refSteps.length) - 1].equals("111")) {
								if (nextInt != 5 && !sensoredInts.contains(nextInt)) {
									sensoredInts.add(nextInt);
								}
								put = false;
								w = false;
							} else {
								put = false;
								w = false;
							}

						} else {
							w = false;
							put = false;
						}
					}

					System.out.println("> " + data);
					for (int i = 0; i < sensoredInts.size(); i++) {
						System.out.print("sensoredInts: ");
						System.out.println(sensoredInts.get(i));
					}
				}
				myReader.close();

				for (int i = 0; i < successInts.size(); i++) {
					if (!sensoredInts.contains(successInts.get(i))) {
						numInput = successInts.get(i);
						board[numInput - 1] = "X"; // Put next value from ref. steps to win
						isWin = checkWin();
						put = true;
						if (isWin.equalsIgnoreCase("X")) {
							w = true;
						} else {
							w = false;
						}
						break;
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}

		// Check if there is any place left to fill that can end up helping Human to
		// win? Then defense there..
		if (!w && !put) {
			for (int a = 0; a < 9; a++) {
				if (Arrays.asList(board).contains(String.valueOf(a + 1))) {
					board[a] = "O";
					isWin = checkWin();
					if (isWin.equals("O")) {
						board[a] = "X";
						put = true;
						numInput = a + 1;
						w = false;
						break;
					} else {
						board[a] = String.valueOf(a + 1);
						put = false;
						isWin = "";
					}
				}
			}
		}

		if (!w && !put) {

			Random rn = new Random();
			int randomInt = rn.nextInt(9) + 1;
			if (sensoredInts.size() >= 8) {
				while (!board[randomInt - 1].equalsIgnoreCase(String.valueOf(randomInt))) {
					randomInt = rn.nextInt(9) + 1;
				}
			} else {
				while (sensoredInts.contains(randomInt)
						|| !board[randomInt - 1].equalsIgnoreCase(String.valueOf(randomInt))) {
					randomInt = rn.nextInt(9) + 1;
				}
			}
			board[randomInt - 1] = "X";
			numInput = randomInt;
			put = true;

			/*
			 * if (board[4].equalsIgnoreCase("5")) { // center : priority high 9, 6, 7, 8,
			 * 1, 4, 5, 111 board[4] = "X"; numInput = 5; put = true; } else if
			 * (board[0].equalsIgnoreCase("1") && !sensoredInts.contains(1)) { // up-left
			 * corner : priority // medium board[0] = "X"; numInput = 1; put = true; } else
			 * if (board[2].equalsIgnoreCase("3") && !sensoredInts.contains(3)) { //
			 * up-right corner : priority // medium board[2] = "X"; numInput = 3; put =
			 * true; } else if (board[6].equalsIgnoreCase("7") && !sensoredInts.contains(7))
			 * { // down-left corner : priority // medium board[6] = "X"; numInput = 7; put
			 * = true; } else if (board[8].equalsIgnoreCase("9") &&
			 * !sensoredInts.contains(9)) { // down-right corner : priority // medium
			 * board[8] = "X"; numInput = 9; put = true; } else if
			 * (board[1].equalsIgnoreCase("2") && !sensoredInts.contains(2)) { // up-left
			 * corner : priority low board[1] = "X"; numInput = 2; put = true; } else if
			 * (board[3].equalsIgnoreCase("4") && !sensoredInts.contains(4)) { // up-right
			 * corner : priority low board[3] = "X"; numInput = 4; put = true; } else if
			 * (board[5].equalsIgnoreCase("6") && !sensoredInts.contains(6)) { // down-left
			 * corner : priority low board[5] = "X"; numInput = 6; put = true; } else if
			 * (board[7].equalsIgnoreCase("8") && !sensoredInts.contains(8)) { // down-right
			 * corner : priority // low board[7] = "X"; numInput = 8; put = true; } else {
			 * printBoard(); }
			 */
		}

		return numInput;
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		board = new String[9];
		turn = "X";
		String winner = null;

		for (int a = 0; a < 9; a++) {
			board[a] = String.valueOf(a + 1);
		}

		System.out.println("Tic Tac Toe Board -");
		printBoard();
		turn = firstTurnDecide();
		firstTurn = turn;

		if (firstTurn.equals("X")) {
			file = "ComputerFirstTurn.txt";
		} else {
			file = "HumanFirstTurn.txt";
		}

		while (winner == null || winner == "") {
			int numInput;
			if (turn.equals("O")) {
				Xcount = false;
				try {
					numInput = in.nextInt();
					if (!(numInput > 0 && numInput <= 9)) {
						System.out.println("Invalid input- Enter input from 1 to 9 only.");
						continue;
					}
				} catch (InputMismatchException e) {
					System.out.println("Invalid input.");
					continue;
				}
				// steps.add(numInput);
				// System.out.println(steps);
			} else {
				numInput = -1;
				while (numInput == -1) {
					numInput = playComputer();
					// steps.add(numInput);
					// System.out.println(steps);
					Xcount = true;
				}
			}

			// Who's turn?
			if (turn.equals("X")) {
				steps.add(numInput);
				turn = "O";
				printBoard();
				winner = checkWin();
			} else {
				if (board[numInput - 1].equals(String.valueOf(numInput))) {
					steps.add(numInput);
					board[numInput - 1] = turn;
					turn = "X";
					printBoard();
					winner = checkWin();
				} else {
					System.out.println("Not available.");
				}
			}

			if (winner.equalsIgnoreCase("draw")) {
				System.out.println("It's a draw..");
				steps.add(555);
			} else if (!winner.equalsIgnoreCase("")) {
				if (winner.equalsIgnoreCase("O")) {
					System.out.println("Congratulations! You won the match!");
				} else {
					System.out.println("Oops! You lost!");
				}

				if (winner.equals("X")) {
					steps.add(999);
				} else if (winner.equals("O")) {
					steps.add(111);
				}
			}
		}

		System.out.println(steps);

		String line = "";

		for (int i = 0; i < steps.size(); i++) {

			line = line + steps.get(i) + " ";
		}

		try (FileWriter f = new FileWriter(file, true);
				BufferedWriter b = new BufferedWriter(f);
				PrintWriter p = new PrintWriter(b);) {
			p.println(line);
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
}
