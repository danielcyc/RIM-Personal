package duke.core;

import duke.command.*;
import duke.exception.*;
import duke.task.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * The main class that instantiates all the sub-classes that carry out
 * the relevant sub-tasks of Duke.
 */
public class Duke {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Constructor for Duke that instantiates the necessary sub-classes for
     * Duke's operation.
     * @param filePath the file path of the document where the task data is stored.
     * @throws FileNotFoundException if file path is invalid
     * @throws ParseException if data is stored in an invalid format and is thus unable to be parsed
     */
    public Duke(String filePath) throws FileNotFoundException, ParseException {
        storage = new Storage(filePath);
        tasks = new TaskList(storage.getItems());
        parser = new Parser();
        ui = new Ui();
    }

    /**
     * This method repeatedly runs the parser, which obtains and parses the input, and
     * depending to the parsed input, creates an executable command, which then carries out
     * the necessary tasks. Will halt when a command issues an exit code of true.
     * @throws ParseException if input is un-parsable
     * @throws IOException if there is an error in reading input or printing output
     */
    public void run() throws ParseException, IOException {
        int daysDue = 4;
        if ((tasks.searchItemsDue(daysDue).isEmpty())) {
            ui.print("No tasks due today!");
        } else {
            ui.printTaskArray("REMINDER--The following deadlines below are due soon:", tasks.searchItemsDue(daysDue));
        }

        Boolean toExit = false;
        while (!toExit) {
            try {
                Command c = parser.parseInput(ui.getInput());
                c.execute(ui, storage, tasks);
                toExit = c.getExitCode();
            }
            catch (DukeException e) {
                e.showError();
            }
        }
    }

    /**
     * The main method that calls the Duke constructor and sets the ball rolling.
     * @throws FileNotFoundException if file path does not exist
     * @throws ParseException if any input is un-parsable
     * @throws IOException if there is an error in reading input or printing output
     * @throws DukeException if the input has no meaning or does not follow our format
     */
    public static void main(String[] args) throws FileNotFoundException, ParseException, IOException, DukeException {
        String logo = "\n" +
                "          _____                    _____                    _____          \n" +
                "         /\\    \\                  /\\    \\                  /\\    \\         \n" +
                "        /::\\    \\                /::\\    \\                /::\\____\\        \n" +
                "       /::::\\    \\               \\:::\\    \\              /::::|   |        \n" +
                "      /::::::\\    \\               \\:::\\    \\            /:::::|   |        \n" +
                "     /:::/\\:::\\    \\               \\:::\\    \\          /::::::|   |        \n" +
                "    /:::/__\\:::\\    \\               \\:::\\    \\        /:::/|::|   |        \n" +
                "   /::::\\   \\:::\\    \\              /::::\\    \\      /:::/ |::|   |        \n" +
                "  /::::::\\   \\:::\\    \\    ____    /::::::\\    \\    /:::/  |::|___|______  \n" +
                " /:::/\\:::\\   \\:::\\____\\  /\\   \\  /:::/\\:::\\    \\  /:::/   |::::::::\\    \\ \n" +
                "/:::/  \\:::\\   \\:::|    |/::\\   \\/:::/  \\:::\\____\\/:::/    |:::::::::\\____\\\n" +
                "\\::/   |::::\\  /:::|____|\\:::\\  /:::/    \\::/    /\\::/    / ~~~~~/:::/    /\n" +
                " \\/____|:::::\\/:::/    /  \\:::\\/:::/    / \\/____/  \\/____/      /:::/    / \n" +
                "       |:::::::::/    /    \\::::::/    /                       /:::/    /  \n" +
                "       |::|\\::::/    /      \\::::/____/                       /:::/    /   \n" +
                "       |::| \\::/____/        \\:::\\    \\                      /:::/    /    \n" +
                "       |::|  ~|               \\:::\\    \\                    /:::/    /     \n" +
                "       |::|   |                \\:::\\    \\                  /:::/    /      \n" +
                "       \\::|   |                 \\:::\\____\\                /:::/    /       \n" +
                "        \\:|   |                  \\::/    /                \\::/    /        \n" +
                "         \\|___|                   \\/____/                  \\/____/         \n" +
                "                                                      ";
        System.out.println(logo);
        new Duke("data/duke.txt").run();
    }

}
