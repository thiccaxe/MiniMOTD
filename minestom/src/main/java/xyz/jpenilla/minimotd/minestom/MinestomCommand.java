package xyz.jpenilla.minimotd.minestom;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.permission.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.Constants;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class MinestomCommand extends Command {
  private final Permission adminPermission = new Permission("minimotd.admin");
  private final Component invalidMessageText = text("Invalid command usage. Use '/minimotd help' for a list of command provided by MiniMOTD.", RED)
    .hoverEvent(text("Click to execute '/minimotd help'"))
    .clickEvent(runCommand("/minimotd help"));

  private final List<SuggestionEntry> COMMANDS = List.of(
    new SuggestionEntry("about", text("Show information about MiniMOTD", TextColor.color(0x007FFF))),
    new SuggestionEntry("reload", text("Reload MiniMOTD configuration files", TextColor.color(0x007FFF))),
    new SuggestionEntry("help", text("Show the help menu", TextColor.color(0x007FFF)))
  );

  private final MiniMOTDExtension extension;
  private final CommandHandler handler;


  MinestomCommand(final @NonNull MiniMOTDExtension extension) {
    super("minimotd");
    this.extension = extension;
    this.handler = new CommandHandler(extension.miniMOTD());

    setDefaultExecutor((sender, context) -> this.onInvalidUse(sender));

    var command = ArgumentType.Word("command");

    command.setSuggestionCallback((sender, context, suggestion) -> {
      COMMANDS.forEach(suggestion::addEntry);
    });
    addConditionalSyntax((sender, commandString) -> true, (sender, context) -> {
    //addConditionalSyntax((sender, commandString) -> sender.hasPermission(adminPermission), (sender, context) -> {
      switch (context.get(command).toLowerCase()) {
        case "about":
          this.handler.about(sender);
          break;
        case "help":
          this.handler.help(sender);
          break;
        case "reload":
          this.handler.reload(sender);
          break;
        default:
          this.onInvalidUse(sender);
          break;
      }
    }, command);
  }

  private void onInvalidUse(final @NonNull Audience audience) {
    audience.sendMessage(invalidMessageText);
  }
}
