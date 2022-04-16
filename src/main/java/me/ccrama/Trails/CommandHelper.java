package me.ccrama.Trails;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class CommandHelper {
    private final Map<String, SimpleEntry<Method, Object>> commandMap = new HashMap<>();
    private CommandMap map;
    private final Plugin plugin;

    public CommandHelper(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();

            try {
                Field e = SimplePluginManager.class.getDeclaredField("commandMap");
                e.setAccessible(true);
                this.map = (CommandMap) e.get(manager);
            } catch (NoSuchFieldException | IllegalAccessException | SecurityException | IllegalArgumentException var4) {
                var4.printStackTrace();
            }
        }

    }

    public boolean handleCommand(CommandSender sender, String label, org.bukkit.command.Command cmd, String[] args) {
        for (int i = args.length; i >= 0; --i) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase());

            for (int cmdLabel = 0; cmdLabel < i; ++cmdLabel) {
                buffer.append(".").append(args[cmdLabel].toLowerCase());
            }

            String var12 = buffer.toString();
            if (this.commandMap.containsKey(var12)) {
                Entry<Method, Object> entry = this.commandMap.get(var12);
                CommandHelper.Command command = entry.getKey().getAnnotation(Command.class);
                if (!sender.hasPermission(command.permission())) {
                    sender.sendMessage(command.noPerm());
                    return true;
                }

                try {
                    entry.getKey().invoke(entry.getValue(), new CommandArgs(sender, cmd, label, args, var12.split("\\.").length - 1));
                } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException var11) {
                    var11.printStackTrace();
                }

                return true;
            }
        }

        this.defaultCommand(new CommandHelper.CommandArgs(sender, cmd, label, args, 0));
        return true;
    }

    public void registerCommands(Object obj) {
        Method[] var5;
        int var4 = (var5 = obj.getClass().getMethods()).length;

        for (int var3 = 0; var3 < var4; ++var3) {
            Method m = var5[var3];
            String alias;
            int var8;
            int var9;
            String[] var10;
            if (m.getAnnotation(CommandHelper.Command.class) != null) {
                CommandHelper.Command var11 = m.getAnnotation(Command.class);
                if (m.getParameterTypes().length <= 1 && m.getParameterTypes()[0] == CommandHelper.CommandArgs.class) {
                    this.registerCommand(var11, var11.name(), m, obj);
                    var9 = (var10 = var11.aliases()).length;

                    for (var8 = 0; var8 < var9; ++var8) {
                        alias = var10[var8];
                        this.registerCommand(var11, alias, m, obj);
                    }
                } else {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                }
            } else if (m.getAnnotation(CommandHelper.Completer.class) != null) {
                CommandHelper.Completer comp = m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == CommandArgs.class) {
                    if (m.getReturnType() != List.class) {
                        System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                    } else {
                        this.registerCompleter(comp.name(), m, obj);
                        var9 = (var10 = comp.aliases()).length;

                        for (var8 = 0; var8 < var9; ++var8) {
                            alias = var10[var8];
                            this.registerCompleter(alias, m, obj);
                        }
                    }
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                }
            }
        }

    }

    public void registerHelp() {
        TreeSet<HelpTopic> help = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());

        for (String topic : this.commandMap.keySet()) {
            if (!topic.contains(".")) {
                org.bukkit.command.Command cmd = this.map.getCommand(topic);
                GenericCommandHelpTopic topic1 = new GenericCommandHelpTopic(cmd);
                help.add(topic1);
            }
        }

        IndexHelpTopic topic2 = new IndexHelpTopic(this.plugin.getName(), "All commands for " + this.plugin.getName(), null, help, "Below is a list of all " + this.plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic2);
    }

    private void registerCommand(CommandHelper.Command command, String label, Method m, Object obj) {
        SimpleEntry<Method, Object> entry = new SimpleEntry<>(m, obj);
        this.commandMap.put(label.toLowerCase(), entry);
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            CommandHelper.BukkitCommand cmd = new CommandHelper.BukkitCommand(cmdLabel, this.plugin);
            this.map.register(this.plugin.getName(), cmd);
        }

        if (!command.description().equalsIgnoreCase("") && cmdLabel.equals(label)) {
            this.map.getCommand(cmdLabel).setDescription(command.description());
        }

        if (!command.usage().equalsIgnoreCase("") && cmdLabel.equals(label)) {
            this.map.getCommand(cmdLabel).setUsage(command.usage());
        }

    }

    private void registerCompleter(String label, Method m, Object obj) {
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        CommandHelper.BukkitCommand ex;
        if (this.map.getCommand(cmdLabel) == null) {
            ex = new CommandHelper.BukkitCommand(cmdLabel, this.plugin);
            this.map.register(this.plugin.getName(), ex);
        }

        if (this.map.getCommand(cmdLabel) instanceof CommandHelper.BukkitCommand) {
            ex = (CommandHelper.BukkitCommand) this.map.getCommand(cmdLabel);
            if (ex.completer == null) {
                ex.completer = new CommandHelper.BukkitCompleter();
            }

            ex.completer.addCompleter(label, m, obj);
        } else if (this.map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                org.bukkit.command.Command ex1 = this.map.getCommand(cmdLabel);
                Field field = ex1.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                CommandHelper.BukkitCompleter completer;
                if (field.get(ex1) == null) {
                    completer = new CommandHelper.BukkitCompleter();
                    completer.addCompleter(label, m, obj);
                    field.set(ex1, completer);
                } else if (field.get(ex1) instanceof CommandHelper.BukkitCompleter) {
                    completer = (CommandHelper.BukkitCompleter) field.get(ex1);
                    completer.addCompleter(label, m, obj);
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }

    }

    private void defaultCommand(CommandHelper.CommandArgs args) {
        args.getSender().sendMessage(args.getLabel() + " is not handled! Oh noes!");
    }

    class BukkitCommand extends org.bukkit.command.Command {
        private final Plugin owningPlugin;
        protected CommandHelper.BukkitCompleter completer;
        private final CommandExecutor executor;

        protected BukkitCommand(String label, Plugin owner) {
            super(label);
            this.executor = owner;
            this.owningPlugin = owner;
            this.usageMessage = "";
        }

        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            boolean success = false;
            if (!this.owningPlugin.isEnabled()) {
                return false;
            } else if (!this.testPermission(sender)) {
                return true;
            } else {
                try {
                    success = this.executor.onCommand(sender, this, commandLabel, args);
                } catch (Throwable var9) {
                    throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.owningPlugin.getDescription().getFullName(), var9);
                }

                if (!success && this.usageMessage.length() > 0) {
                    String[] var8;
                    int var7 = (var8 = this.usageMessage.replace("<command>", commandLabel).split("\n")).length;

                    for (int var6 = 0; var6 < var7; ++var6) {
                        String line = var8[var6];
                        sender.sendMessage(line);
                    }
                }

                return success;
            }
        }

        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
            Validate.notNull(sender, "Sender cannot be null");
            Validate.notNull(args, "Arguments cannot be null");
            Validate.notNull(alias, "Alias cannot be null");
            List<String> completions = null;

            try {
                if (this.completer != null) {
                    completions = this.completer.onTabComplete(sender, this, alias, args);
                }

                if (completions == null && this.executor instanceof TabCompleter) {
                    completions = ((TabCompleter) this.executor).onTabComplete(sender, this, alias, args);
                }
            } catch (Throwable var11) {
                StringBuilder message = new StringBuilder();
                message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
                String[] var10 = args;
                int var9 = args.length;

                for (int var8 = 0; var8 < var9; ++var8) {
                    String arg = var10[var8];
                    message.append(arg).append(' ');
                }

                message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.owningPlugin.getDescription().getFullName());
                throw new CommandException(message.toString(), var11);
            }

            return completions == null ? super.tabComplete(sender, alias, args) : completions;
        }
    }

    class BukkitCompleter implements TabCompleter {
        private final Map<String, SimpleEntry<Method, Object>> completers = new HashMap<>();

        public void addCompleter(String label, Method m, Object obj) {
            this.completers.put(label, new SimpleEntry<Method, Object>(m, obj));
        }

        @SuppressWarnings("unchecked")
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            for (int i = args.length; i >= 0; --i) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(label.toLowerCase());

                for (int cmdLabel = 0; cmdLabel < i; ++cmdLabel) {
                    if (!args[cmdLabel].equals("") && !args[cmdLabel].equals(" ")) {
                        buffer.append(".").append(args[cmdLabel].toLowerCase());
                    }
                }

                String var11 = buffer.toString();
                if (this.completers.containsKey(var11)) {
                    Entry<Method, Object> entry = this.completers.get(var11);

                    try {
                        return (List<String>) entry.getKey().invoke(entry.getValue(), new Object[]{CommandHelper.this.new CommandArgs(sender, command, label, args, var11.split("\\.").length - 1)});
                    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var10) {
                        var10.printStackTrace();
                    }
                }
            }

            return null;
        }
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {
        String name();

        String permission() default "";

        String noPerm() default "You do not have permission to perform that action";

        String[] aliases() default {};

        String description() default "";

        String usage() default "";
    }

    public class CommandArgs {
        private final CommandSender sender;
        private final org.bukkit.command.Command command;
        private final String label;
        private final String[] args;

        protected CommandArgs(CommandSender sender, org.bukkit.command.Command command, String label, String[] args, int subCommand) {
            String[] modArgs = new String[args.length - subCommand];
            System.arraycopy(args, 0 + subCommand, modArgs, 0, args.length - subCommand);
            StringBuilder buffer = new StringBuilder();
            buffer.append(label);

            for (int cmdLabel = 0; cmdLabel < subCommand; ++cmdLabel) {
                buffer.append(".").append(args[cmdLabel]);
            }

            String var10 = buffer.toString();
            this.sender = sender;
            this.command = command;
            this.label = var10;
            this.args = modArgs;
        }

        public CommandSender getSender() {
            return this.sender;
        }

        public org.bukkit.command.Command getCommand() {
            return this.command;
        }

        public String getLabel() {
            return this.label;
        }

        public String[] getArgs() {
            return this.args;
        }

        public boolean isPlayer() {
            return this.sender instanceof Player;
        }

        public Player getPlayer() {
            return this.sender instanceof Player ? (Player) this.sender : null;
        }
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Completer {
        String name();

        String[] aliases() default {};
    }
}
