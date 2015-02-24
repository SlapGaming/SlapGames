package nl.stoux.SlapGames.Commands.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Stoux on 11/02/2015.
 */
@Getter
public class CommandAlias {

    private String description;
    private String command;
    private HashSet<String> aliases = new HashSet<>();

    public CommandAlias(String command, String description, CmdTrain[] cmdTrains) {
        this.command = command;
        this.description = description;
        addAliases(cmdTrains);
    }

    public void addAliases(CmdTrain[] cmdTrains) {
        for (CmdTrain alias : cmdTrains) {
            String v = alias.value().toLowerCase();
            if (!v.equalsIgnoreCase(command)) {
                this.aliases.add(v);
            }
        }
    }

}
