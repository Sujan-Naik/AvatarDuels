package com.serene.avatarduels.scoreboard;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleScoreboard {

    private static Map<String, String> cache = new HashMap<>();

    private Scoreboard scoreboard;
    private String title;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<Object> updated;

    public SimpleScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.<Team>newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(String text, Integer score) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        String stripped = ChatColor.stripColor(text);

        // Use the first 16 real characters.
        if (stripped.length() > 16) {
            int formatCounts = 0;
            for (int i = 0; i < text.length(); ++i) {
                if (text.charAt(i) == ChatColor.COLOR_CHAR) {
                    ++formatCounts;
                }
            }

            text = text.substring(0, Math.min(15 + formatCounts * 2, text.length()));
        }

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
    }

    public boolean remove(Integer score, String text) {
        return remove(score, text, true);
    }

    public boolean remove(Integer score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null)
            return false;

        scores.remove(toRemove);

        if(b)
            removed.add(score);

        return true;
    }

    public String get(int score, String n) {
        String str = null;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score) &&
                    !entry.getKey().equals(n)) {
                str = entry.getKey();
            }
        }

        return str;
    }

    private Map.Entry<Team, String> createTeam(String text, int pos) {
        Team team;
        ChatColor color = ChatColor.values()[(pos < 0) ? -pos : pos];
        String result;

        if (!cache.containsKey(color.toString()))
            cache.put(color.toString(), color.toString());

        result = cache.get(color.toString());

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        applyText(team, text, result);

        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

	private void applyText(Team team, String text, String result) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        String prefix = iterator.next();

        team.setPrefix(prefix);

        if(!team.hasEntry(result))
            team.addEntry(result);

        if (text.length() > 16) {
            String prefixColor = ChatColor.getLastColors(prefix);
            String suffix = iterator.next();

            if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                prefix = prefix.substring(0, prefix.length() - 1);
                team.setPrefix(prefix);
                prefixColor = ChatColor.getByChar(suffix.charAt(0)).toString();
                suffix = suffix.substring(1);
            }

            if (prefixColor == null)
                prefixColor = "";

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
            }

            team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
        }
    }

	public void update() {
        if (updated.isEmpty()) {
            return;
        }

        if (obj == null) {
            obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy", (title.length() > 16 ? title.substring(0, 15) : title));
            obj.setDisplayName(title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (Integer aRemoved : removed) {
            for (String s : scoreboard.getEntries()) {
                Score score = obj.getScore(s);

                if (score == null)
                    continue;

                if (score.getScore() != aRemoved)
                    continue;

                scoreboard.resetScores(s);
            }
        }

        removed.clear();

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
        	Team t = scoreboard.getTeam(ChatColor.values()[(text.getValue() < 0) ? -text.getValue() : text.getValue()].toString());
            Map.Entry<Team, String> team;

            if(!updated.contains(text.getKey())) {
                continue;
            }

            if(t != null) {
            	String color = ChatColor.values()[(text.getValue() < 0) ? -text.getValue() : text.getValue()].toString();

                if (!cache.containsKey(color)) {
                    cache.put(color, color);
                }

                team = new AbstractMap.SimpleEntry<>(t, cache.get(color));
                applyText(team.getKey(), text.getKey(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(text.getKey(), text.getValue());
            }

            Integer score = text.getValue() != null ? text.getValue() : index;

            obj.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if(obj != null)
            obj.setDisplayName(title);
    }

    public void reset() {
        for (Team t : teams)
            t.unregister();
        teams.clear();
        scores.clear();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
        for (Player p : players)
            p.setScoreboard(scoreboard);
    }
}