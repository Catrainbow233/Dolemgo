package net.catrainbow.dolemgo.command.console;

import net.catrainbow.dolemgo.Player;
import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.command.CommandMap;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandCompleter implements Completer {

    private final Server proxy;

    public CommandCompleter(Server proxy) {
        this.proxy = proxy;
    }

    private void addOptions(Consumer<String> commandConsumer) {
        CommandMap commandMap = this.proxy.getCommandMap();
        for (String command : commandMap.getCommands().keySet()) {
            commandConsumer.accept(command);
        }
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
        if (parsedLine.wordIndex() == 0) {
            if (parsedLine.word().isEmpty()) {
                this.addOptions(command -> candidates.add(new Candidate(command)));
                return;
            }

            List<String> commands = new ArrayList<>();
            this.addOptions(commands::add);
            for (String command : commands) {
                if (command.startsWith(parsedLine.word())) {
                    candidates.add(new Candidate(command));
                }
            }
            return;
        }

        if (parsedLine.wordIndex() > 1 && !parsedLine.word().isEmpty()) {
            String world = parsedLine.word();
            for (Player player : this.proxy.onlinePlayers.values()) {
                if (player.getName().toLowerCase().startsWith(world)) {
                    candidates.add(new Candidate(player.getName()));
                }

            }
        }
    }


}
