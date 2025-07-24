package org.lilbrocodes.theatrical.commands.argument_type;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.server.command.ServerCommandSource;
import org.lilbrocodes.theatrical.util.Misc;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerListArgumentType implements ArgumentType<List<String>> {
    private final boolean allowDuplicates;

    PlayerListArgumentType(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public boolean allowsDuplicates() {
        return allowDuplicates;
    }

    public static PlayerListArgumentType duplicates() {
        return new PlayerListArgumentType(true);
    }

    public static PlayerListArgumentType noDuplicates() {
        return new PlayerListArgumentType(false);
    }

    @Override
    public List<String> parse(StringReader reader) throws CommandSyntaxException {
        List<String> players = new ArrayList<>();
        while (reader.canRead()) {
            reader.skipWhitespace();
            int start = reader.getCursor();
            while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
                reader.skip();
            }
            String name = reader.getString().substring(start, reader.getCursor());
            if (!name.isEmpty()) {
                if (players.contains(name) && !allowDuplicates) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                            .dispatcherParseException()
                            .create("Duplicate player: " + name);
                }
                players.add(name);
            }
        }
        return players;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();
        String[] parts = remaining.split(" ");

        String lastPart = parts.length == 0 ? "" : parts[parts.length - 1];

        Set<String> alreadyTyped = new HashSet<>();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                alreadyTyped.add(parts[i]);
            }
        }

        Collection<String> allNames = new ArrayList<>();
        if (context.getSource() instanceof ServerCommandSource source) {
            allNames = source.getPlayerNames();
        } else if (context.getSource() instanceof ClientCommandSource source) {
            allNames = source.getPlayerNames();
        }

        for (String name : allNames) {
            if (!alreadyTyped.contains(name) && name.startsWith(lastPart)) {
                builder.suggest(Misc.joinAndAppend(parts, name));
            }
        }
        return builder.buildFuture();
    }
}
