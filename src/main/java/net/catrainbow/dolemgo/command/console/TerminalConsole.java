package net.catrainbow.dolemgo.command.console;

import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.command.CommandSender;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

public class TerminalConsole extends SimpleTerminalConsole {

    private final Server proxy;
    private final ConsoleThread consoleThread;

    public TerminalConsole(Server proxy) {
        this.proxy = proxy;
        this.consoleThread = new ConsoleThread(this);
    }

    @Override
    protected void runCommand(String command) {
        CommandSender console = this.proxy.getConsoleSender();
        this.proxy.getScheduler().scheduleTask(() -> this.proxy.dispatchCommand(console, command), false);
    }

    @Override
    protected void shutdown() {
        Server.getInstance().shutdown();
    }

    @Override
    protected boolean isRunning() {
        return this.proxy.isRunning;
    }

    public ConsoleThread getConsoleThread() {
        return this.consoleThread;
    }
}
