package org.brokie.trytofindme.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Replaces displayed numbers close to the local player's coordinates. */
public final class CoordinateTextFilter {
    private static final double COORDINATE_TOLERANCE = 128;
    private static final Pattern NUMBER = Pattern.compile("(?<![A-Za-z0-9_.])[-+]?(?:\\d+(?:\\.\\d+)?|\\.\\d+)(?![A-Za-z0-9_.])");
    private static final Pattern REGION_FILE = Pattern.compile("(?i)r\\.(-?\\d+)\\.(-?\\d+)\\.mca");

    private CoordinateTextFilter() {
    }

    public static FormattedCharSequence hideCoordinates(FormattedCharSequence text) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return text;
        }

        List<Integer> codepoints = new ArrayList<>();
        List<Style> styles = new ArrayList<>();
        List<Integer> charOffsets = new ArrayList<>();
        StringBuilder plainText = new StringBuilder();
        text.accept((index, style, codepoint) -> {
            charOffsets.add(plainText.length());
            codepoints.add(codepoint);
            styles.add(style);
            plainText.appendCodePoint(codepoint);
            return true;
        });

        List<int[]> hiddenRanges = new ArrayList<>();
        int playerRegionX = (int) Math.floor(Math.floor(player.getX() / 16.0) / 32.0);
        int playerRegionZ = (int) Math.floor(Math.floor(player.getZ() / 16.0) / 32.0);
        Matcher regionMatcher = REGION_FILE.matcher(plainText);
        while (regionMatcher.find()) {
            try {
                int regionX = Integer.parseInt(regionMatcher.group(1));
                int regionZ = Integer.parseInt(regionMatcher.group(2));
                if (regionX == playerRegionX && regionZ == playerRegionZ) {
                    addRange(hiddenRanges, charOffsets, codepoints.size(), plainText.length(),
                            regionMatcher.start(1), regionMatcher.end(1));
                    addRange(hiddenRanges, charOffsets, codepoints.size(), plainText.length(),
                            regionMatcher.start(2), regionMatcher.end(2));
                }
            } catch (NumberFormatException ignored) {
                // Ignore region numbers outside the integer range.
            }
        }

        Matcher matcher = NUMBER.matcher(plainText);
        while (matcher.find()) {
            double number;
            try {
                number = Double.parseDouble(matcher.group());
            } catch (NumberFormatException ignored) {
                continue;
            }

            if (isNearPlayer(number, player)) {
                addRange(hiddenRanges, charOffsets, codepoints.size(), plainText.length(),
                        matcher.start(), matcher.end());
            }
        }

        if (hiddenRanges.isEmpty()) {
            return text;
        }
        hiddenRanges.sort(Comparator.comparingInt(range -> range[0]));

        List<FormattedCharSequence> parts = new ArrayList<>();
        int rangeIndex = 0;
        for (int i = 0; i < codepoints.size();) {
            if (rangeIndex < hiddenRanges.size() && i == hiddenRanges.get(rangeIndex)[0]) {
                int[] range = hiddenRanges.get(rangeIndex++);
                parts.add(FormattedCharSequence.forward("??", styles.get(i)));
                i = range[1];
            } else {
                parts.add(FormattedCharSequence.codepoint(codepoints.get(i), styles.get(i)));
                i++;
            }
        }

        return FormattedCharSequence.composite(parts);
    }

    private static boolean isNearPlayer(double value, LocalPlayer player) {
        double chunkX = Math.floor(player.getX() / 16.0);
        double chunkZ = Math.floor(player.getZ() / 16.0);
        return Math.abs(value - player.getX()) <= COORDINATE_TOLERANCE
                || Math.abs(value - player.getZ()) <= COORDINATE_TOLERANCE
                || Math.abs(value - chunkX) <= COORDINATE_TOLERANCE
                || Math.abs(value - chunkZ) <= COORDINATE_TOLERANCE;
    }

    private static void addRange(List<int[]> ranges, List<Integer> charOffsets,
                                 int codepointCount, int textLength, int charStart, int charEnd) {
        int start = charOffsets.indexOf(charStart);
        int end = charEnd == textLength ? codepointCount : charOffsets.indexOf(charEnd);
        if (start >= 0 && end > start) {
            ranges.add(new int[]{start, end});
        }
    }
}
